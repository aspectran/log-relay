/*
 * Copyright (c) 2008-2023 The Aspectran Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.logrelay;

import com.aspectran.core.activity.InstantActivitySupport;
import com.aspectran.core.component.bean.ablility.InitializableBean;
import com.aspectran.core.component.bean.annotation.AvoidAdvice;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.json.JsonWriter;
import com.aspectran.core.util.logging.Logger;
import com.aspectran.core.util.logging.LoggerFactory;
import com.aspectran.core.util.security.InvalidPBTokenException;
import com.aspectran.core.util.security.TimeLimitedPBTokenIssuer;
import com.aspectran.websocket.jsr356.AspectranConfigurator;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@ServerEndpoint(
        value = "/logtail/{token}",
        configurator = AspectranConfigurator.class
)
@AvoidAdvice
public class LogtailEndpoint extends InstantActivitySupport implements InitializableBean {

    private static final Logger logger = LoggerFactory.getLogger(LogtailEndpoint.class);

    private static final String LOGTAILER_CONFIG_FILE = "/config/logtailer-config.apon";

    private static final String COMMAND_JOIN = "JOIN:";

    private static final String COMMAND_LEAVE = "LEAVE";

    private static final String HEARTBEAT_PING_MSG = "--heartbeat-ping--";

    private static final String HEARTBEAT_PONG_MSG = "--heartbeat-pong--";

    private static final String MSG_AVAILABLE_TAILERS = "availableTailers:";

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    private LogTailerManager logTailerManager;

    @Override
    public void initialize() throws Exception {
        File file = getApplicationAdapter().toRealPathAsFile(LOGTAILER_CONFIG_FILE);
        LogTailerConfig logTailerConfig = new LogTailerConfig(file);
        this.logTailerManager = new LogTailerManager(this, logTailerConfig);
    }

    @OnOpen
    public void onOpen(@PathParam("token") String token, Session session) throws IOException {
        try {
            TimeLimitedPBTokenIssuer.validate(token);
        } catch (InvalidPBTokenException e) {
            logger.error("Invalid token: " + token);
            String reason = "Invalid token";
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, reason));
            throw new IOException(reason, e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("WebSocket connection established with token: " + token);
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        if (HEARTBEAT_PING_MSG.equals(message)) {
            session.getAsyncRemote().sendText(HEARTBEAT_PONG_MSG);
            return;
        }
        if (message != null && message.startsWith(COMMAND_JOIN)) {
            addSession(session, message.substring(COMMAND_JOIN.length()));
        } else if (COMMAND_LEAVE.equals(message)) {
            removeSession(session);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        if (logger.isDebugEnabled()) {
            logger.debug("Websocket session " + session.getId() + " has been closed. Reason: " + reason);
        }
        removeSession(session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("Error in websocket session: " + session.getId(), error);
        try {
            removeSession(session);
            session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, null));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected void broadcast(String message) {
        synchronized (sessions) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(message);
                }
            }
        }
    }

    private void sendAvailableTailers(Session session) throws IOException {
        JsonWriter jsonWriter = new JsonWriter().nullWritable(false);
        jsonWriter.write(logTailerManager.getLogTailerInfoList(false));
        session.getAsyncRemote().sendText(MSG_AVAILABLE_TAILERS + jsonWriter);
    }

    private void addSession(Session session, String message) throws IOException {
        if (sessions.add(session)) {
            sendAvailableTailers(session);
            String[] names = StringUtils.splitCommaDelimitedString(message);
            logTailerManager.join(session, names);
        }
    }

    private void removeSession(Session session) {
        if (sessions.remove(session)) {
            logTailerManager.release(session);
        }
    }

    Set<Session> getSessions() {
        return sessions;
    }

}
