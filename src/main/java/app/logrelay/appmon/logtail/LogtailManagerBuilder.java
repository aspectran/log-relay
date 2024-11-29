package app.logrelay.appmon.logtail;

import app.logrelay.appmon.AppMonManager;
import com.aspectran.utils.Assert;
import com.aspectran.utils.ResourceUtils;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.apon.ParameterKey;
import com.aspectran.utils.apon.Parameters;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;

public abstract class LogtailManagerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(LogtailManagerBuilder.class);

    private static final String LOGTAIL_CONFIG_FILE = "appmon/logtail-config.apon";

    @NonNull
    public static LogtailManager build(@NonNull AppMonManager appMonManager) throws IOException {
        LogtailConfig logTailConfig = new LogtailConfig(ResourceUtils.getResourceAsReader(LOGTAIL_CONFIG_FILE));
        LogtailManager logtailManager = new LogtailManager(appMonManager);
        for (LogtailInfo logTailInfo : logTailConfig.getLogTailInfoList()) {
            if (logger.isDebugEnabled()) {
                logger.debug(ToStringBuilder.toString("Create LogtailService", logTailInfo));
            }
            validateRequiredParameter(logTailInfo, LogtailInfo.group);
            validateRequiredParameter(logTailInfo, LogtailInfo.name);
            validateRequiredParameter(logTailInfo, LogtailInfo.file);

            File logFile = null;
            try {
                String file = appMonManager.getApplicationAdapter().toRealPath(logTailInfo.getFile());
                logFile = new File(file).getCanonicalFile();
            } catch (IOException e) {
                logger.error("Failed to resolve absolute path to log file " + logTailInfo.getFile(), e);
            }
            if (logFile != null) {
                LogtailService logtailService = new LogtailService(logtailManager, logTailInfo, logFile);
                logtailManager.addLogtailService(logTailInfo.getName(), logtailService);
            }
        }
        return logtailManager;
    }

    private static void validateRequiredParameter(@NonNull Parameters parameters, ParameterKey key) {
        Assert.hasLength(parameters.getString(key),
                "Missing value of required parameter: " + parameters.getQualifiedName(key));
    }

}