/*
 * Copyright (c) ${project.inceptionYear}-2024 The Aspectran Project
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
package app.logrelay.appmon.logtail;

import app.logrelay.appmon.AppMonManager;
import app.logrelay.appmon.AppMonSession;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LogtailManager {

    private static final Logger logger = LoggerFactory.getLogger(LogtailManager.class);

    private final Map<String, LogtailService> logtailServices = new LinkedHashMap<>();

    private final AppMonManager appMonManager;

    public LogtailManager(AppMonManager appMonManager) {
        this.appMonManager = appMonManager;
    }

    public void addLogtailService(String name, LogtailService service) {
        logtailServices.put(name, service);
    }

    public List<LogtailInfo> getLogtailInfoList(String[] joinGroups) {
        List<LogtailInfo> infoList = new ArrayList<>(logtailServices.size());
        if (joinGroups != null && joinGroups.length > 0) {
            for (String name : joinGroups) {
                for (LogtailService logtailService : logtailServices.values()) {
                    if (logtailService.getInfo().getGroup().equals(name)) {
                        infoList.add(logtailService.getInfo());
                    }
                }
            }
        } else {
            for (LogtailService logtailService : logtailServices.values()) {
                infoList.add(logtailService.getInfo());
            }
        }
        return infoList;
    }

    public void join(AppMonSession session) {
        if (!logtailServices.isEmpty()) {
            String[] joinGroups = session.getJoinedGroups();
            if (joinGroups != null && joinGroups.length > 0) {
                for (LogtailService service : logtailServices.values()) {
                    for (String group : joinGroups) {
                        if (service.getInfo().getGroup().equals(group)) {
                            start(service);
                        }
                    }
                }
            } else {
                for (LogtailService service : logtailServices.values()) {
                    start(service);
                }
            }
        }
    }

    public void collectLastMessages(AppMonSession session, List<String> messages) {
        if (!logtailServices.isEmpty()) {
            String[] joinGroups = session.getJoinedGroups();
            if (joinGroups != null && joinGroups.length > 0) {
                for (LogtailService service : logtailServices.values()) {
                    for (String group : joinGroups) {
                        if (service.getInfo().getGroup().equals(group)) {
                            service.readLastLines(messages);
                        }
                    }
                }
            } else {
                for (LogtailService service : logtailServices.values()) {
                    service.readLastLines(messages);
                }
            }
        }
    }

    private void start(@NonNull LogtailService service) {
        try {
            service.start();
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    public void release(String[] unusedGroups) {
        if (!logtailServices.isEmpty()) {
            if (unusedGroups != null) {
                for (LogtailService service : logtailServices.values()) {
                    for (String group : unusedGroups) {
                        if (service.getInfo().getGroup().equals(group) && service.isRunning()) {
                            stop(service);
                        }
                    }
                }
            }
        }
    }

    private void stop(LogtailService service) {
        try {
            service.stop();
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    void broadcast(String message) {
        appMonManager.broadcast(message);
    }

    void broadcast(AppMonSession session, String message) {
        appMonManager.broadcast(session, message);
    }

}
