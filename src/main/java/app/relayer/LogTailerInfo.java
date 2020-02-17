package app.relayer;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterKey;
import com.aspectran.core.util.apon.ValueType;

/**
 * <p>Created: 2020/02/12</p>
 */
public class LogTailerInfo extends AbstractParameters {

    private static final ParameterKey name;
    private static final ParameterKey file;
    private static final ParameterKey sampleInterval;
    private static final ParameterKey visualizer;

    private static final ParameterKey[] parameterKeys;

    static {
        name = new ParameterKey("name", ValueType.STRING);
        file = new ParameterKey("file", ValueType.STRING);
        sampleInterval = new ParameterKey("sampleInterval", ValueType.LONG);
        visualizer = new ParameterKey("visualizer", ValueType.STRING);

        parameterKeys = new ParameterKey[] {
                name,
                file,
                sampleInterval,
                visualizer
        };
    }

    public LogTailerInfo() {
        super(parameterKeys);
    }

    public String getName() {
        return getString(name);
    }

    public void setName(String name) {
        putValue(LogTailerInfo.name, name);
    }

    public String getFile() {
        return getString(file);
    }

    public void setFile(String file) {
        putValue(LogTailerInfo.file, file);
    }

    public long getSampleInterval() {
        return getLong(sampleInterval, 0);
    }

    public void setSampleInterval(long sampleInterval) {
        putValue(LogTailerInfo.sampleInterval, sampleInterval);
    }

    public String getVisualizer() {
        return getString(visualizer);
    }

    public void setVisualizer(String visualizer) {
        putValue(LogTailerInfo.visualizer, visualizer);
    }

}
