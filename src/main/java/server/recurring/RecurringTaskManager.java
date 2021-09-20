package server.recurring;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class RecurringTaskManager {

    private static final RecurringTaskManager INSTANCE = new RecurringTaskManager();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private RecurringTaskManager() {
    }

    public static RecurringTaskManager getInstance() {
        return INSTANCE;
    }

    public void schedule(RecurringTask task) {
        if (task instanceof FixedRateRecurringTask) {
            FixedRateRecurringTask fixedRateTask = (FixedRateRecurringTask) task;
            executor.scheduleAtFixedRate(
                    fixedRateTask::execute,
                    fixedRateTask.getInitialDelay(),
                    fixedRateTask.getPeriod(),
                    fixedRateTask.getTimeUnit()
            );
        } else {
            throw new IllegalArgumentException("Unsupported recurring task: " + task.getClass());
        }
    }
}
