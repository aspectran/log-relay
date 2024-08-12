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

import com.aspectran.utils.apon.AbstractParameters;
import com.aspectran.utils.apon.ParameterKey;
import com.aspectran.utils.apon.ValueType;

/**
 * <p>Created: 2020/02/12</p>
 */
public class LogtailInfo extends AbstractParameters {

    static final ParameterKey group;
    static final ParameterKey name;
    private static final ParameterKey title;
    static final ParameterKey file;
    private static final ParameterKey charset;
    private static final ParameterKey sampleInterval;
    private static final ParameterKey lastLines;
    private static final ParameterKey visualizing;

    private static final ParameterKey[] parameterKeys;

    static {
        group = new ParameterKey("group", ValueType.STRING);
        name = new ParameterKey("name", ValueType.STRING);
        file = new ParameterKey("file", ValueType.STRING);
        title = new ParameterKey("title", ValueType.STRING);
        charset = new ParameterKey("charset", ValueType.STRING);
        sampleInterval = new ParameterKey("sampleInterval", ValueType.INT);
        lastLines = new ParameterKey("lastLines", ValueType.INT);
        visualizing = new ParameterKey("visualizing", ValueType.BOOLEAN);

        parameterKeys = new ParameterKey[] {
                group,
                name,
                title,
                file,
                charset,
                sampleInterval,
                lastLines,
                visualizing
        };
    }

    public LogtailInfo() {
        super(parameterKeys);
    }

    public String getGroup() {
        return getString(group);
    }

    public void setGroup(String group) {
        putValue(LogtailInfo.group, group);
    }

    public String getName() {
        return getString(name);
    }

    public void setName(String name) {
        putValue(LogtailInfo.name, name);
    }

    public String getTitle() {
        return getString(title);
    }

    public void setTitle(String title) {
        putValue(LogtailInfo.title, title);
    }

    public String getFile() {
        return getString(file);
    }

    public void setFile(String file) {
        putValue(LogtailInfo.file, file);
    }

    public String getCharset() {
        return getString(charset);
    }

    public void setCharset(String charset) {
        putValue(LogtailInfo.charset, charset);
    }

    public int getSampleInterval() {
        return getInt(sampleInterval, 0);
    }

    public void setSampleInterval(int sampleInterval) {
        putValue(LogtailInfo.sampleInterval, sampleInterval);
    }

    public int getLastLines() {
        return getInt(lastLines, 0);
    }

    public void setLastLines(int lastLines) {
        putValue(LogtailInfo.lastLines, lastLines);
    }

    public boolean isVisualizing() {
        return getBoolean(visualizing, false);
    }

    public void setVisualizing(boolean visualizing) {
        putValue(LogtailInfo.visualizing, visualizing);
    }

}
