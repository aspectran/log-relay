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

    private static final ParameterKey tailer;

    private static final ParameterKey[] parameterKeys;

    static {
        tailer = new ParameterKey("tailer", LogTailerInfo.class, true, true);

        parameterKeys = new ParameterKey[] {
                tailer
        };
    }

    public LogTailerConfig() {
        super(parameterKeys);
    }

    public LogTailerConfig(String text) throws IOException {
        this();
        readFrom("tailers: [\n" + StringUtils.trimWhitespace(text) + "\n]");
    }

    public List<LogTailerInfo> getLogTailerInfoList() {
        return getParametersList(tailer);
    }

    public LogTailerConfig addLogTailerInfo(LogTailerInfo logTailerInfo) {
        putValue(tailer, logTailerInfo);
        return this;
    }

}
