package app.relayer;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterKey;

import java.io.File;
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
        readFrom(text);
    }

    public LogTailerConfig(File file) throws IOException {
        this();
        readFrom(file);
    }

    public List<LogTailerInfo> getLogTailerInfoList() {
        return getParametersList(tailer);
    }

    public LogTailerConfig addLogTailerInfo(LogTailerInfo logTailerInfo) {
        putValue(tailer, logTailerInfo);
        return this;
    }

}
