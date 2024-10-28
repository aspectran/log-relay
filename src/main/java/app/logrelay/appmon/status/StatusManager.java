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
package app.logrelay.appmon.status;

import app.logrelay.appmon.AppMonManager;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatusManager {

    private static final Logger logger = LoggerFactory.getLogger(StatusManager.class);

    private final Map<String, StatusService> statusServices = new LinkedHashMap<>();

    private final AppMonManager appMonManager;

    public StatusManager(AppMonManager appMonManager) {
        this.appMonManager = appMonManager;
    }

    void addStatusService(String name, StatusService statusService) {
        statusServices.put(name, statusService);
    }

    public List<StatusInfo> getStatusInfoList(String[] joinGroups) {
        List<StatusInfo> infoList = new ArrayList<>(statusServices.size());
        if (joinGroups != null && joinGroups.length > 0) {
            for (String name : joinGroups) {
                for (StatusService service : statusServices.values()) {
                    if (service.getInfo().getGroup().equals(name)) {
                        infoList.add(service.getInfo());
                    }
                }
            }
        } else {
            for (StatusService service : statusServices.values()) {
                infoList.add(service.getInfo());
            }
        }
        return infoList;
    }

    public void join(String[] joinGroups) {
        if (!statusServices.isEmpty()) {
            if (joinGroups != null && joinGroups.length > 0) {
                for (StatusService service : statusServices.values()) {
                    for (String group : joinGroups) {
                        if (service.getGroup().equals(group)) {
                            start(service);
                        }
                    }
                }
            } else {
                for (StatusService service : statusServices.values()) {
                    start(service);
                }
            }
        }
    }

    private void start(StatusService service) {
        try {
            if (service.isRunning()) {
                service.refresh();
            } else {
                service.start();
            }
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    public void release(String[] unusedGroups) {
        if (!statusServices.isEmpty()) {
            if (unusedGroups != null) {
                for (StatusService service : statusServices.values()) {
                    for (String group : unusedGroups) {
                        if (service.getGroup().equals(group)) {
                            stop(service);
                        }
                    }
                }
            }
        }
    }

    private void stop(StatusService service) {
        try {
            service.stop();
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    public ActivityContext getActivityContext() {
        return appMonManager.getActivityContext();
    }

    public <V> V getBean(@NonNull String id) {
        return getActivityContext().getBeanRegistry().getBean(id);
    }

    public void broadcast(String name, String msg) {
        appMonManager.broadcast(name + ":" + msg);
    }

}
