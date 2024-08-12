package app.logrelay.appmon.endpoint;

import com.aspectran.utils.apon.AbstractParameters;
import com.aspectran.utils.apon.ParameterKey;
import com.aspectran.utils.apon.ValueType;

public class EndpointPollingConfig extends AbstractParameters {

    private static final ParameterKey pollingInterval;
    private static final ParameterKey sessionTimeout;
    private static final ParameterKey initialBufferSize;
    private static final ParameterKey enabled;

    private static final ParameterKey[] parameterKeys;

    static {
        pollingInterval = new ParameterKey("pollingInterval", ValueType.INT);
        sessionTimeout = new ParameterKey("sessionTimeout", ValueType.INT);
        initialBufferSize = new ParameterKey("initialBufferSize", ValueType.INT);
        enabled = new ParameterKey("enabled", ValueType.BOOLEAN);

        parameterKeys = new ParameterKey[] {
                pollingInterval,
                sessionTimeout,
                initialBufferSize,
                enabled
        };
    }

    public EndpointPollingConfig() {
        super(parameterKeys);
    }

    public int getPollingInterval() {
        return getInt(pollingInterval, 0);
    }

    public void setPollingInterval(int pollingInterval) {
        putValue(EndpointPollingConfig.pollingInterval, pollingInterval);
    }

    public int getSessionTimeout() {
        return getInt(sessionTimeout, 0);
    }

    public void setSessionTimeout(int sessionTimeout) {
        putValue(EndpointPollingConfig.sessionTimeout, sessionTimeout);
    }

    public int getInitialBufferSize() {
        return getInt(initialBufferSize, 0);
    }

    public void setInitialBufferSize(int initialBufferSize) {
        putValue(EndpointPollingConfig.initialBufferSize, initialBufferSize);
    }

    public boolean isEnabled() {
        return getBoolean(enabled, true);
    }

    public void setEnabled(boolean enabled) {
        putValue(EndpointPollingConfig.enabled, enabled);
    }

}
