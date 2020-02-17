/*
 * Copyright (c) 2008-2020 The Aspectran Project
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
package app.relayer;

import com.aspectran.core.component.bean.annotation.AvoidAdvice;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.json.JsonWriter;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;
import com.aspectran.web.socket.jsr356.ActivityContextAwareEndpoint;
import com.aspectran.web.socket.jsr356.AspectranConfigurator;

import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@ServerEndpoint(
        value = "/logtail",
        configurator = AspectranConfigurator.class
)
@AvoidAdvice
public class LogtailEndpoint extends ActivityContextAwareEndpoint {

    private static final Log log = LogFactory.getLog(LogtailEndpoint.class);

    private static final String COMMAND_JOIN = "JOIN:";

    private static final String COMMAND_LEAVE = "LEAVE";

    private static final String HEARTBEAT_PING_MSG = "--heartbeat-ping--";

    private static final String HEARTBEAT_PONG_MSG = "--heartbeat-pong--";

    private static final String MSG_AVAILABLE_TAILERS = "availableTailers:";

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    private LogTailerManager logTailerManager;

    public void setLogTailerManager(LogTailerManager logTailerManager) {
        this.logTailerManager = logTailerManager;
    }

    @OnOpen
    public void onOpen(Session session) {
        if (log.isDebugEnabled()) {
            log.debug("WebSocket connection established with session: " + session.getId());
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        if (HEARTBEAT_PING_MSG.equals(message)) {
            session.getAsyncRemote().sendText(HEARTBEAT_PONG_MSG);
            return;
        }
        if (message != null && message.startsWith(COMMAND_JOIN)) {
            addSession(session, message);
            broadcastAvailableTailers();
        } else if (COMMAND_LEAVE.equals(message)) {
            removeSession(session);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        removeSession(session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("Error in websocket session: " + session.getId(), error);
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

    private void broadcastAvailableTailers() throws IOException {
        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.nullWritable(false);
        jsonWriter.write(logTailerManager.getLogTailerInfoList());
        broadcast(MSG_AVAILABLE_TAILERS + jsonWriter.toString());
    }

    private void addSession(Session session, String message) {
        if (sessions.add(session)) {
            String[] names = StringUtils.splitCommaDelimitedString(message.substring(COMMAND_JOIN.length()));
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
