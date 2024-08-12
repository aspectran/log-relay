package app.logrelay.appmon;

public interface AppMonEndpoint {

    void broadcast(String message);

    boolean isUsingGroup(String group);

}
