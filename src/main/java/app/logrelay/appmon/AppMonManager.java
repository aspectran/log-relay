package app.logrelay.appmon;

import app.logrelay.appmon.endpoint.EndpointInfo;
import app.logrelay.appmon.endpoint.EndpointManager;
import app.logrelay.appmon.endpoint.EndpointManagerBuilder;
import app.logrelay.appmon.group.GroupInfo;
import app.logrelay.appmon.group.GroupManager;
import app.logrelay.appmon.group.GroupManagerBuilder;
import app.logrelay.appmon.logtail.LogtailInfo;
import app.logrelay.appmon.logtail.LogtailManager;
import app.logrelay.appmon.logtail.LogtailManagerBuilder;
import app.logrelay.appmon.status.StatusInfo;
import app.logrelay.appmon.status.StatusManager;
import app.logrelay.appmon.status.StatusManagerBuilder;
import com.aspectran.core.activity.InstantActivitySupport;
import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.component.bean.annotation.Bean;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Initialize;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.utils.Assert;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.utils.annotation.jsr305.Nullable;
import com.aspectran.utils.security.InvalidPBTokenException;
import com.aspectran.utils.security.TimeLimitedPBTokenIssuer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>Created: 4/3/24</p>
 */
@Component
@Bean("AppMonManager")
public class AppMonManager extends InstantActivitySupport {

    private EndpointManager endpointManager;

    private GroupManager groupManager;

    private LogtailManager logtailManager;

    private StatusManager statusManager;

    private final List<AppMonEndpoint> endpoints = new ArrayList<>();

    public AppMonManager() {
    }

    @Initialize
    public void init() throws Exception {
        Assert.state(this.endpointManager == null, "AppMonManager is already initialized");
        this.endpointManager = EndpointManagerBuilder.build();
        this.groupManager = GroupManagerBuilder.build();
        this.logtailManager = LogtailManagerBuilder.build(this);
        this.statusManager = StatusManagerBuilder.build(this);
    }

    @Override
    @NonNull
    public ActivityContext getActivityContext() {
        return super.getActivityContext();
    }

    @Override
    @NonNull
    public ApplicationAdapter getApplicationAdapter() {
        return super.getApplicationAdapter();
    }

    public void putEndpoint(AppMonEndpoint endpoint) {
        synchronized (endpoints) {
            if (!endpoints.contains(endpoint)) {
                endpoints.add(endpoint);
            }
        }
    }

    public EndpointInfo getResidentEndpointInfo() {
        EndpointInfo endpointInfo = endpointManager.getResidentEndpointInfo();
        if (endpointInfo == null) {
            throw new IllegalStateException("Resident EndpointInfo not found");
        }
        return endpointInfo.copy();
    }

    public List<EndpointInfo> getAvailableEndpointInfoList(String token) {
        List<EndpointInfo> endpointInfoList = new ArrayList<>();
        for (EndpointInfo endpointInfo : endpointManager.getEndpointInfoList()) {
            EndpointInfo info = endpointInfo.copy();
            String url = info.getUrl();
            if (!url.endsWith("/")) {
                url += "/";
            }
            url += token;
            info.setUrl(url);
            endpointInfoList.add(info);
        }
        return endpointInfoList;
    }

    public String[] getVerifiedGroupNames(String[] joinGroups) {
        List<GroupInfo> groups = getGroupInfoList(joinGroups);
        if (!groups.isEmpty()) {
            return GroupManager.extractGroupNames(groups);
        } else {
            return new String[0];
        }
    }

    public List<GroupInfo> getGroupInfoList(String[] joinGroups) {
        return groupManager.getGroupInfoList(joinGroups);
    }

    public List<LogtailInfo> getLogtailInfoList(String[] joinGroups) {
        return logtailManager.getLogtailInfoList(joinGroups);
    }

    public List<StatusInfo> getStatusInfoList(String[] joinGroups) {
        return statusManager.getStatusInfoList(joinGroups);
    }

    public synchronized boolean join(@NonNull AppMonSession session) {
        if (session.isValid()) {
            logtailManager.join(session);
            statusManager.join(session);
            return true;
        } else {
            return false;
        }
    }

    public List<String> getLastMessages(@NonNull AppMonSession session) {
        List<String> messages = new ArrayList<>();
        logtailManager.collectLastMessages(session, messages);
        return messages;
    }

    public synchronized void release(AppMonSession session) {
        String[] unusedGroups = getUnusedGroups(session);
        logtailManager.release(unusedGroups);
        statusManager.release(unusedGroups);
        session.removeJoinedGroups();
    }

    public void broadcast(String message) {
        for (AppMonEndpoint endpoint : endpoints) {
            endpoint.broadcast(message);
        }
    }

    public void broadcast(AppMonSession session, String message) {
        for (AppMonEndpoint endpoint : endpoints) {
            endpoint.broadcast(session, message);
        }
    }

    @Nullable
    private String[] getUnusedGroups(AppMonSession session) {
        String[] joinedGroups = getJoinedGroups(session);
        if (joinedGroups == null || joinedGroups.length == 0) {
            return null;
        }
        List<String> unusedGroups = new ArrayList<>(joinedGroups.length);
        for (String name : joinedGroups) {
            boolean using = false;
            for (AppMonEndpoint endpoint : endpoints) {
                if (endpoint.isUsingGroup(name)) {
                    using = true;
                    break;
                }
            }
            if (!using) {
                unusedGroups.add(name);
            }
        }
        if (!unusedGroups.isEmpty()) {
            return unusedGroups.toArray(new String[0]);
        } else {
            return null;
        }
    }

    @Nullable
    private String[] getJoinedGroups(@NonNull AppMonSession session) {
        String[] savedGroups = session.getJoinedGroups();
        if (savedGroups == null) {
            return null;
        }
        Set<String> joinedGroups = new HashSet<>();
        for (String name : savedGroups) {
            if (groupManager.containsGroup(name)) {
                joinedGroups.add(name);
            }
        }
        if (!joinedGroups.isEmpty()) {
            return joinedGroups.toArray(new String[0]);
        } else {
            return null;
        }
    }

    public String issueToken() {
        return TimeLimitedPBTokenIssuer.getToken();
    }

    public void validateToken(String token) throws InvalidPBTokenException {
        TimeLimitedPBTokenIssuer.validate(token);
    }

}
