package app.relayer;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterKey;
import com.aspectran.core.util.apon.ValueType;

/**
 * <p>Created: 2020/02/12</p>
 */
public class EndpointInfo extends AbstractParameters {

    private static final ParameterKey name;
    private static final ParameterKey url;

    private static final ParameterKey[] parameterKeys;

    static {
        name = new ParameterKey("name", ValueType.STRING);
        url = new ParameterKey("url", ValueType.STRING);

        parameterKeys = new ParameterKey[] {
                name,
                url
        };
    }

    public EndpointInfo() {
        super(parameterKeys);
    }

    public String getName() {
        return getString(name);
    }

    public void setName(String name) {
        putValue(EndpointInfo.name, name);
    }

    public String getUrl() {
        return getString(url);
    }

    public void setUrl(String url) {
        putValue(EndpointInfo.url, url);
    }

}
