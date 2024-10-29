package app.logrelay.appmon;

public interface AppMonSession {

    String[] getJoinedGroups();

    void saveJoinedGroups(String[] joinGroups);

    void removeJoinedGroups();

    boolean isTwoWay();

    boolean isValid();

}
