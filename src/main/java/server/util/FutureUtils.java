package server.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FutureUtils {
    private FutureUtils() {
    }

    public static <T> T getFuture(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
