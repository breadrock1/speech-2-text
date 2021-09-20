package server.logging;

class SystemOutLogger implements Logger {

    private final String name;

    SystemOutLogger(String name) {
        this.name = name;
    }

    @Override
    public void info(final String message) {
        printTag();
        System.out.println(message);
    }

    @Override
    public void info(final String message, final Object... args) {
        printTag();
        System.out.format(message, args);
        System.out.println();
    }

    @Override
    public void error(final String message, final Object... args) {
        printTag();
        System.err.format(message, args);
        System.err.println();
    }

    @Override
    public void error(final Throwable e, final String message, final Object... args) {
        printTag();
        System.err.format(message, args);
        System.err.println();
        e.printStackTrace(System.err);
    }

    @Override
    public void error(final Throwable e) {
        printTag();
        System.err.println(e.getMessage());
        e.printStackTrace(System.err);
    }

    private void printTag() {
        System.out.print("[" + name + "] ");
    }
}
