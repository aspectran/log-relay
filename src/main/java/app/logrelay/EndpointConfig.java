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
