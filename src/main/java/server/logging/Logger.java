package server.logging;

public interface Logger {

    void info(String message);

    void info(String message, Object... args);

    void error(String message, Object... args);

    void error(Throwable e, String message, Object... args);

    void error(Throwable e);

}
