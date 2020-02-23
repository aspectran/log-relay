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
    private static final ParameterKey charset;
    private static final ParameterKey sampleInterval;
    private static final ParameterKey bufferSize;
    private static final ParameterKey lastLines;
    private static final ParameterKey visualizer;

    private static final ParameterKey[] parameterKeys;

    static {
        name = new ParameterKey("name", ValueType.STRING);
        file = new ParameterKey("file", ValueType.STRING);
        charset = new ParameterKey("charset", ValueType.STRING);
        sampleInterval = new ParameterKey("sampleInterval", ValueType.INT);
        bufferSize = new ParameterKey("bufferSize", ValueType.INT);
        lastLines = new ParameterKey("lastLines", ValueType.INT);
        visualizer = new ParameterKey("visualizer", ValueType.STRING);

        parameterKeys = new ParameterKey[] {
                name,
                file,
                charset,
                sampleInterval,
                bufferSize,
                lastLines,
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

    public String getCharset() {
        return getString(charset);
    }

    public void setCharset(String charset) {
        putValue(LogTailerInfo.charset, charset);
    }

    public int getSampleInterval() {
        return getInt(sampleInterval, 0);
    }

    public void setSampleInterval(int sampleInterval) {
        putValue(LogTailerInfo.sampleInterval, sampleInterval);
    }

    public int getBufferSize() {
        return getInt(bufferSize, 0);
    }

    public void setBufferSize(int bufferSize) {
        putValue(LogTailerInfo.bufferSize, bufferSize);
    }

    public int getLastLines() {
        return getInt(lastLines, 0);
    }

    public void setLastLines(int lastLines) {
        putValue(LogTailerInfo.lastLines, lastLines);
    }

    public String getVisualizer() {
        return getString(visualizer);
    }

    public void setVisualizer(String visualizer) {
        putValue(LogTailerInfo.visualizer, visualizer);
    }

}
