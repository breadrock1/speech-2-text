package server.recurring;

import java.util.concurrent.TimeUnit;

public interface FixedRateRecurringTask extends RecurringTask {

    default long getInitialDelay() {
        return getPeriod();
    }

    long getPeriod();

    TimeUnit getTimeUnit();
}
