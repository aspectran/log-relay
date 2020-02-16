package app.relayer;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterKey;
import com.aspectran.core.util.apon.ValueType;

/**
 * <p>Created: 2020/02/12</p>
 */
public class LogTailerInfo extends AbstractParameters {

    private static final ParameterKey name;
    private static final ParameterKey logFile;
    private static final ParameterKey sampleInterval;
    private static final ParameterKey visualizer;

    private static final ParameterKey[] parameterKeys;

    static {
        name = new ParameterKey("name", ValueType.STRING);
        logFile = new ParameterKey("logFile", ValueType.STRING);
        sampleInterval = new ParameterKey("sampleInterval", ValueType.LONG);
        visualizer = new ParameterKey("visualizer", ValueType.STRING);

        parameterKeys = new ParameterKey[] {
                name,
                logFile,
                sampleInterval,
                visualizer
        };
    }

    public LogTailerInfo() {
        super(parameterKeys);
    }

}
