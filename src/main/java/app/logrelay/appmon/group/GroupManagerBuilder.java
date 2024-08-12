package app.logrelay.appmon.group;

import com.aspectran.utils.ResourceUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;

import java.io.IOException;

public abstract class GroupManagerBuilder {

    private static final String MEASUREMENT_CONFIG_FILE = "appmon/group-config.apon";

    @NonNull
    public static GroupManager build() throws IOException {
        GroupConfig groupConfig = new GroupConfig(ResourceUtils.getResourceAsReader(MEASUREMENT_CONFIG_FILE));
        return new GroupManager(groupConfig.getGroupInfoList());
    }

}
