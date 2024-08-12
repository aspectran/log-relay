package app.logrelay.appmon.status;

public interface StatusCollector {

    void init();

    String collect();

}
