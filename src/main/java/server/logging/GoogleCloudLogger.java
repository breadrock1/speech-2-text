package server.logging;

import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Severity;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Collections;

public class GoogleCloudLogger implements Logger {

    private final String name;

    private final Logging logging = LoggingOptions.getDefaultInstance().getService();

    public GoogleCloudLogger(String name) {
        this.name = name.isEmpty() ? "ANONYMOUS" : name;
    }

    @Override
    public void info(String message) {
        logWithSeverity(message, Severity.INFO);
    }

    @Override
    public void info(String message, Object... args) {
        logWithSeverity(String.format(message, args), Severity.INFO);
    }

    @Override
    public void error(String message, Object... args) {
        logWithSeverity(String.format(message, args), Severity.ERROR);
    }

    @Override
    public void error(Throwable e, String message, Object... args) {
        logWithSeverity(message + "\n" + ExceptionUtils.getStackTrace(e), Severity.ERROR);
    }

    @Override
    public void error(Throwable e) {
        logWithSeverity(e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e), Severity.ERROR);
    }

    private void logWithSeverity(String message, Severity severity) {
        LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of(message))
                .setSeverity(severity)
                .setLogName(name)
                .setResource(MonitoredResource.newBuilder("global").build())
                .build();
        logging.write(Collections.singleton(entry));
    }

}
