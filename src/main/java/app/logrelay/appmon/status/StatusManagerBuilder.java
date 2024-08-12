package app.logrelay.appmon.status;

import app.logrelay.appmon.AppMonManager;
import com.aspectran.utils.Assert;
import com.aspectran.utils.ClassUtils;
import com.aspectran.utils.ResourceUtils;
import com.aspectran.utils.ToStringBuilder;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.apon.ParameterKey;
import com.aspectran.utils.apon.Parameters;
import com.aspectran.utils.logging.Logger;
import com.aspectran.utils.logging.LoggerFactory;

public abstract class StatusManagerBuilder {

    private static final String STATUS_CONFIG_FILE = "appmon/status-config.apon";

    private static final Logger logger = LoggerFactory.getLogger(StatusManagerBuilder.class);

    @NonNull
    public static StatusManager build(@NonNull AppMonManager appMonManager) throws Exception {
        StatusConfig statusConfig = new StatusConfig(ResourceUtils.getResourceAsReader(STATUS_CONFIG_FILE));
        StatusManager statusManager = new StatusManager(appMonManager);
        for (StatusInfo statusInfo : statusConfig.getStatusInfoList()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Create StatusService " + ToStringBuilder.toString(statusInfo));
            }
            validateRequiredParameter(statusInfo, StatusInfo.group);
            validateRequiredParameter(statusInfo, StatusInfo.name);
            validateRequiredParameter(statusInfo, StatusInfo.source);
            validateRequiredParameter(statusInfo, StatusInfo.collector);

            StatusCollector statusCollector = createStatusCollector(statusManager, statusInfo);
            StatusService service = new StatusService(statusManager, statusInfo, statusCollector);
            statusManager.addStatusService(statusInfo.getName(), service);
        }
        return statusManager;
    }

    private static void validateRequiredParameter(@NonNull Parameters parameters, ParameterKey key) {
        Assert.hasLength(parameters.getString(key),
                "Missing value of required parameter: " + parameters.getQualifiedName(key));
    }

    @NonNull
    private static StatusCollector createStatusCollector(@NonNull StatusManager manager,
                                                         @NonNull StatusInfo info)
            throws Exception {
        try {
            Class<StatusCollector> collectorType = ClassUtils.classForName(info.getCollector());
            Object[] args = { manager, info };
            Class<?>[] argTypes = { StatusManager.class, StatusInfo.class };
            return ClassUtils.createInstance(collectorType, args, argTypes);
        } catch (Exception e) {
            throw new Exception("Failed to create measure collector: " + info.getCollector(), e);
        }
    }

}
