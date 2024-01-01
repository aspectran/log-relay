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
package app.logrelay;

import com.aspectran.utils.apon.AbstractParameters;
import com.aspectran.utils.apon.ParameterKey;
import com.aspectran.utils.apon.ValueType;

/**
 * <p>Created: 2020/02/12</p>
 */
public class LogTailerInfo extends AbstractParameters {

    private static final ParameterKey name;
    private static final ParameterKey file;
    private static final ParameterKey charset;
    private static final ParameterKey sampleInterval;
    private static final ParameterKey bufferSize;
    private static final ParameterKey lastLines;
    private static final ParameterKey visualizer;

    private static final ParameterKey[] parameterKeys;

    static {
        name = new ParameterKey("name", ValueType.STRING);
        file = new ParameterKey("file", ValueType.STRING);
        charset = new ParameterKey("charset", ValueType.STRING);
        sampleInterval = new ParameterKey("sampleInterval", ValueType.INT);
        bufferSize = new ParameterKey("bufferSize", ValueType.INT);
        lastLines = new ParameterKey("lastLines", ValueType.INT);
        visualizer = new ParameterKey("visualizer", ValueType.STRING);

        parameterKeys = new ParameterKey[] {
                name,
                file,
                charset,
                sampleInterval,
                bufferSize,
                lastLines,
                visualizer
        };
    }

    public LogTailerInfo() {
        super(parameterKeys);
    }

    public String getName() {
        return getString(name);
    }

    public void setName(String name) {
        putValue(LogTailerInfo.name, name);
    }

    public String getFile() {
        return getString(file);
    }

    public void setFile(String file) {
        putValue(LogTailerInfo.file, file);
    }

    public String getCharset() {
        return getString(charset);
    }

    public void setCharset(String charset) {
        putValue(LogTailerInfo.charset, charset);
    }

    public int getSampleInterval() {
        return getInt(sampleInterval, 0);
    }

    public void setSampleInterval(int sampleInterval) {
        putValue(LogTailerInfo.sampleInterval, sampleInterval);
    }

    public int getBufferSize() {
        return getInt(bufferSize, 0);
    }

    public void setBufferSize(int bufferSize) {
        putValue(LogTailerInfo.bufferSize, bufferSize);
    }

    public int getLastLines() {
        return getInt(lastLines, 0);
    }

    public void setLastLines(int lastLines) {
        putValue(LogTailerInfo.lastLines, lastLines);
    }

    public String getVisualizer() {
        return getString(visualizer);
    }

    public void setVisualizer(String visualizer) {
        putValue(LogTailerInfo.visualizer, visualizer);
    }

}
