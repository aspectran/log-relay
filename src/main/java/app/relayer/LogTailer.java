/*
 * Copyright (c) 2008-2021 The Aspectran Project
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

import com.aspectran.core.util.lifecycle.AbstractLifeCycle;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.io.input.Tailer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogTailer extends AbstractLifeCycle {

    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    private static final int DEFAULT_SAMPLE_INTERVAL = 1000;

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private final String name;

    /** the log file to tail */
    private final String file;

    /** the Charset to be used for reading the file */
    private final Charset charset;

    /** how frequently to check for file changes; defaults to 1 second */
    private int sampleInterval = DEFAULT_SAMPLE_INTERVAL;

    private int bufferSize = DEFAULT_BUFFER_SIZE;

    private int lastLines;

    private String visualizerName;

    private LogTailerListener tailerListener;

    private Tailer tailer;

    public LogTailer(String name, String file) {
        this(name, file, DEFAULT_CHARSET);
    }

    public LogTailer(String name, String file, String charset) {
        this(name, file, (charset != null ? Charset.forName(charset) : null));
    }

    public LogTailer(String name, String file, Charset charset) {
        this.name = name;
        this.file = file;
        this.charset = (charset == null ? DEFAULT_CHARSET : charset);
    }

    public String getName() {
        return name;
    }

    public String getFile() {
        return file;
    }

    public Charset getCharset() {
        return charset;
    }

    public int getSampleInterval() {
        return sampleInterval;
    }

    public void setSampleInterval(int sampleInterval) {
        this.sampleInterval = (sampleInterval > 0 ? sampleInterval : DEFAULT_SAMPLE_INTERVAL);
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = (bufferSize > 0 ? bufferSize : DEFAULT_BUFFER_SIZE);
    }

    public int getLastLines() {
        return lastLines;
    }

    public void setLastLines(int lastLines) {
        this.lastLines = lastLines;
    }

    public String getVisualizerName() {
        return visualizerName;
    }

    public void setVisualizerName(String visualizerName) {
        this.visualizerName = visualizerName;
    }

    public void setEndpoint(LogtailEndpoint endpoint) {
        this.tailerListener = new LogTailerListener(name, endpoint);
    }

    protected void readLastLines() {
        if (lastLines > 0) {
            File logFile = new File(file);
            String[] lines = readLastLines(logFile, lastLines);
            for (String line : lines) {
                tailerListener.handle(line);
            }
        }
    }

    protected void doStart() throws Exception {
        if (tailerListener == null) {
            throw new IllegalStateException("No TailerListener configured");
        }

        File logFile = new File(file);
        tailer = Tailer.create(logFile, charset, tailerListener, sampleInterval, true, false, bufferSize);
    }

    protected void doStop() throws Exception {
        if (tailer != null) {
            tailer.stop();
            tailer = null;
        }
    }

    private String[] readLastLines(File file, int lastLines) {
        List<String> list = new ArrayList<>();
        try (ReversedLinesFileReader reversedLinesFileReader = new ReversedLinesFileReader(file, charset)) {
            int count = 0;
            while (count++ < lastLines) {
                String line = reversedLinesFileReader.readLine();
                if (line == null) {
                    break;
                }
                list.add(line);
            }
            Collections.reverse(list);
        } catch (IOException e) {
            // ignore
        }
        return list.toArray(new String[0]);
    }

}
