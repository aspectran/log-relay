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

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <p>Created: 2020/02/12</p>
 */
public class EndpointConfig extends AbstractParameters {

    private static final ParameterKey endpoint;

    private static final ParameterKey[] parameterKeys;

    static {
        endpoint = new ParameterKey("endpoint", EndpointInfo.class, true, true);

        parameterKeys = new ParameterKey[] {
                endpoint
        };
    }

    public EndpointConfig() {
        super(parameterKeys);
    }

    public EndpointConfig(String text) throws IOException {
        this();
        readFrom(text);
    }

    public EndpointConfig(File file) throws IOException {
        this();
        readFrom(file);
    }

    public List<EndpointInfo> getEndpointInfoList() {
        return getParametersList(endpoint);
    }

    public EndpointConfig addEndpointInfo(EndpointInfo endpointInfo) {
        putValue(endpoint, endpointInfo);
        return this;
    }

}
