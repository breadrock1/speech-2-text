package server.logging;

public class FakeLogger implements Logger {

    @Override
    public void info(String message) {
        // do nothing
    }

    @Override
    public void info(String message, Object... args) {
        // do nothing
    }

    @Override
    public void error(String message, Object... args) {
        // do nothing
    }

    @Override
    public void error(Throwable e, String message, Object... args) {
        // do nothing
    }

    @Override
    public void error(Throwable e) {
        // do nothing
    }
}
