package app.relayer;

import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterKey;

import java.io.IOException;
import java.util.List;

/**
 * <p>Created: 2020/02/12</p>
 */
public class LogTailerConfig extends AbstractParameters {

    private static final ParameterKey tailers;

    private static final ParameterKey[] parameterKeys;

    static {
        tailers = new ParameterKey("tailers", LogTailerItem.class);

        parameterKeys = new ParameterKey[] {
                tailers
        };
    }

    public LogTailerConfig() {
        super(parameterKeys);
    }

    public LogTailerConfig(String text) throws IOException {
        this();
        readFrom("tailers: [\n" + StringUtils.trimWhitespace(text) + "\n]");
    }

    public List<LogTailerItem> getLogTailerItems() {
        return getParametersList(tailers);
    }

    public LogTailerConfig addLogTailerItem(LogTailerItem logTailerItem) {
        putValue(tailers, logTailerItem);
        return this;
    }

}
