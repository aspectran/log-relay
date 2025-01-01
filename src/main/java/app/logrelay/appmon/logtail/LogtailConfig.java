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
package app.logrelay.appmon.logtail;

import com.aspectran.utils.apon.AbstractParameters;
import com.aspectran.utils.apon.ParameterKey;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * <p>Created: 2020/02/12</p>
 */
public class LogtailConfig extends AbstractParameters {

    private static final ParameterKey logtail;

    private static final ParameterKey[] parameterKeys;

    static {
        logtail = new ParameterKey("logtail", LogtailInfo.class, true, true);

        parameterKeys = new ParameterKey[] {
                logtail
        };
    }

    public LogtailConfig() {
        super(parameterKeys);
    }

    public LogtailConfig(String text) throws IOException {
        this();
        readFrom(text);
    }

    public LogtailConfig(File file) throws IOException {
        this();
        readFrom(file);
    }

    public LogtailConfig(Reader reader) throws IOException {
        this();
        readFrom(reader);
    }

    public List<LogtailInfo> getLogTailInfoList() {
        return getParametersList(logtail);
    }

}
