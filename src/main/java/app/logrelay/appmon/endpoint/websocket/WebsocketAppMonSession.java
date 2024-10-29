package app.logrelay.appmon.endpoint.websocket;

import app.logrelay.appmon.AppMonSession;
import com.aspectran.utils.Assert;
import com.aspectran.utils.annotation.jsr305.Nullable;
import jakarta.websocket.Session;

public class WebsocketAppMonSession implements AppMonSession {

    private static final String JOINED_GROUPS_PROPERTY = "appmon/JoinedGroups";

    private final Session session;

    public WebsocketAppMonSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public String[] getJoinedGroups() {
        return (String[])session.getUserProperties().get(JOINED_GROUPS_PROPERTY);
    }

    @Override
    public void saveJoinedGroups(String[] joinGroups) {
        Assert.notEmpty(joinGroups, "joinGroups must not be null or empty");
        session.getUserProperties().put(JOINED_GROUPS_PROPERTY, joinGroups);

    }

    @Override
    public void removeJoinedGroups() {
        session.getUserProperties().remove(JOINED_GROUPS_PROPERTY);
    }

    @Override
    public boolean isTwoWay() {
        return true;
    }

    @Override
    public boolean isValid() {
        return session.isOpen();
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other || session == other) {
            return true;
        }
        if (other instanceof WebsocketAppMonSession that) {
            return session.equals(that.getSession());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return session.hashCode();
    }

}
