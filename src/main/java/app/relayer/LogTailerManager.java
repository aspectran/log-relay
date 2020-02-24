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

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogTailerManager {

    private static final String TAILERS_PROPERTY = "tailers";

    private final Map<String, LogTailer> tailers = new HashMap<>();

    private final LogtailEndpoint endpoint;

    public LogTailerManager(LogtailEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public LogTailerManager(LogtailEndpoint endpoint, LogTailer[] tailers) {
        this(endpoint);
        addLogTailer(tailers);
    }

    public LogTailerManager(LogtailEndpoint endpoint, LogTailerConfig tailerConfig) {
        this(endpoint);
        addLogTailer(tailerConfig);
    }

    public void addLogTailer(LogTailer... tailers) {
        if (tailers != null) {
            for (LogTailer tailer : tailers) {
                tailer.setEndpoint(endpoint);
                this.tailers.put(tailer.getName(), tailer);
            }
        }
    }

    public void addLogTailer(LogTailerConfig tailerConfig) {
        if (tailerConfig == null) {
            throw new IllegalArgumentException("tailerConfig must not be null");
        }
        List<LogTailerInfo> tailerInfoList = tailerConfig.getLogTailerInfoList();
        for (LogTailerInfo tailerInfo : tailerInfoList) {
            String name = tailerInfo.getName();
            String logFile = tailerInfo.getFile();
            String charset = tailerInfo.getCharset();
            int sampleInterval = tailerInfo.getSampleInterval();
            int bufferSize = tailerInfo.getBufferSize();
            int lastLines = tailerInfo.getLastLines();
            String visualizer = tailerInfo.getVisualizer();
            LogTailer tailer = new LogTailer(name, logFile, charset);
            tailer.setSampleInterval(sampleInterval);
            tailer.setBufferSize(bufferSize);
            tailer.setLastLines(lastLines);
            tailer.setVisualizerName(visualizer);
            addLogTailer(tailer);
        }
    }

    public List<LogTailerInfo> getLogTailerInfoList(boolean detail) {
        List<LogTailerInfo> tailerInfoList = new ArrayList<>();
        for (LogTailer tailer : tailers.values()) {
            LogTailerInfo tailerInfo = new LogTailerInfo();
            tailerInfo.setName(tailer.getName());
            tailerInfo.setFile(tailer.getFile());
            if (detail) {
                tailerInfo.setCharset(tailer.getCharset().toString());
                tailerInfo.setSampleInterval(tailer.getSampleInterval());
                tailerInfo.setBufferSize((tailer.getBufferSize()));
                tailerInfo.setLastLines((tailer.getLastLines()));
            }
            tailerInfo.setVisualizer(tailer.getVisualizerName());
            tailerInfoList.add(tailerInfo);
        }
        return tailerInfoList;
    }

    void join(Session session, String[] names) {
        if (!tailers.isEmpty()) {
            if (names != null && names.length > 0) {
                List<String> list = new ArrayList<>();
                String[] existingNames = (String[])session.getUserProperties().get(TAILERS_PROPERTY);
                if (existingNames != null) {
                    Collections.addAll(list, existingNames);
                }
                for (String name : names) {
                    LogTailer tailer = tailers.get(name);
                    if (tailer != null) {
                        list.add(name);
                        tailer.readLastLines();
                        if (!tailer.isRunning()) {
                            try {
                                tailer.start();
                            } catch (Exception e) {
                                // ignore
                            }
                        }
                    }
                }
                session.getUserProperties().put(TAILERS_PROPERTY, list.toArray(new String[0]));
            } else {
                for (LogTailer tailer : tailers.values()) {
                    tailer.readLastLines();
                    if (!tailer.isRunning()) {
                        try {
                            tailer.start();
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                }
            }
        }
    }

    void release(Session session) {
        if (!tailers.isEmpty()) {
            String[] names = (String[])session.getUserProperties().get(TAILERS_PROPERTY);
            if (names != null) {
                for (String name : names) {
                    LogTailer tailer = tailers.get(name);
                    if (tailer != null && tailer.isRunning() && !isUsedTailer(name)) {
                        try {
                            tailer.stop();
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                }
            } else {
                for (LogTailer tailer : tailers.values()) {
                    if (!tailer.isRunning()) {
                        try {
                            tailer.stop();
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                }
            }
        }
    }

    private boolean isUsedTailer(String name) {
        for (Session session : endpoint.getSessions()) {
            String[] names = (String[])session.getUserProperties().get(TAILERS_PROPERTY);
            if (names != null) {
                for (String name2 : names) {
                    if (name.equals(name2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
