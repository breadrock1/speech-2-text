package server.conference;

import server.logging.Logger;
import server.logging.LoggerFactory;
import server.recurring.FixedRateRecurringTask;

import java.util.concurrent.TimeUnit;

public class CleanUpTask implements FixedRateRecurringTask {

    private static final long SPEAKER_ACTIVITY_TIMEOUT_MS = 3000L;

    private final ConferenceManager conferenceManager;
    private final Logger logger = LoggerFactory.createFor(CleanUpTask.class);

    public CleanUpTask(ConferenceManager conferenceManager) {
        this.conferenceManager = conferenceManager;
    }

    @Override
    public long getPeriod() {
        return SPEAKER_ACTIVITY_TIMEOUT_MS;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    @Override
    public void execute() {
        final long now = System.currentTimeMillis();
        for (Conference conference : conferenceManager.getAllConferences()) {
            for (Participant speaker : conference.getAllSpeakers(Participant.State.ACTIVE)) {
                final long lastActivity = speaker.getLastActivityTimestamp();
                if (lastActivity > 0 && now - lastActivity > SPEAKER_ACTIVITY_TIMEOUT_MS) {
                    speaker.deactivate();
                    logger.info("Speaker %s has been removed from conference %s", speaker.id, conference.getId());
                }
            }
        }
    }
}
