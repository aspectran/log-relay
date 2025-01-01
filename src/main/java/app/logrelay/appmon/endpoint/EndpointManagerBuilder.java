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
package app.logrelay.appmon.endpoint;

import com.aspectran.utils.ResourceUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;

import java.io.IOException;
import java.util.List;

public abstract class EndpointManagerBuilder {

    private static final String ENDPOINT_CONFIG_FILE = "appmon/endpoint-config.apon";

    @NonNull
    public static EndpointManager build() throws IOException {
        EndpointConfig endpointConfig = new EndpointConfig(ResourceUtils.getResourceAsReader(ENDPOINT_CONFIG_FILE));
        List<EndpointInfo> endpointInfoList = endpointConfig.getEndpointInfoList();
        return new EndpointManager(endpointInfoList);
    }

}
