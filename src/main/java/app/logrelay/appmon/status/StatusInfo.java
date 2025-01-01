/*
 * Copyright (c) ${project.inceptionYear}-2025 The Aspectran Project
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

import com.aspectran.utils.apon.AbstractParameters;
import com.aspectran.utils.apon.ParameterKey;
import com.aspectran.utils.apon.ValueType;

/**
 * <p>Created: 2020/02/12</p>
 */
public class StatusInfo extends AbstractParameters {

    static final ParameterKey group;
    static final ParameterKey name;
    private static final ParameterKey title;
    static final ParameterKey collector;
    static final ParameterKey source;
    static final ParameterKey label;
    private static final ParameterKey sampleInterval;

    private static final ParameterKey[] parameterKeys;

    static {
        group = new ParameterKey("group", ValueType.STRING);
        name = new ParameterKey("name", ValueType.STRING);
        title = new ParameterKey("title", ValueType.STRING);
        collector = new ParameterKey("collector", ValueType.STRING);
        source = new ParameterKey("source", ValueType.STRING);
        label = new ParameterKey("label", ValueType.STRING);
        sampleInterval = new ParameterKey("sampleInterval", ValueType.INT);

        parameterKeys = new ParameterKey[] {
                group,
                name,
                title,
                collector,
                source,
                label,
                sampleInterval
        };
    }

    public StatusInfo() {
        super(parameterKeys);
    }

    public String getGroup() {
        return getString(group);
    }

    public void setGroup(String group) {
        putValue(StatusInfo.group, group);
    }

    public String getName() {
        return getString(name);
    }

    public void setName(String name) {
        putValue(StatusInfo.name, name);
    }

    public String getTitle() {
        return getString(title);
    }

    public void setTitle(String title) {
        putValue(StatusInfo.title, title);
    }

    public String getCollector() {
        return getString(collector);
    }

    public void setCollector(String collector) {
        putValue(StatusInfo.collector, collector);
    }

    public String getSource() {
        return getString(source);
    }

    public void setSource(String source) {
        putValue(StatusInfo.source, source);
    }

    public String getLabel() {
        return getString(label);
    }

    public void setLabel(String label) {
        putValue(StatusInfo.label, label);
    }

    public int getSampleInterval() {
        return getInt(sampleInterval, 0);
    }

    public void setSampleInterval(int sampleInterval) {
        putValue(StatusInfo.sampleInterval, sampleInterval);
    }

}
