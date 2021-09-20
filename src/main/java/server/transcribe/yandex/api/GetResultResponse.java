package server.transcribe.yandex.api;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class GetResultResponse {

    public boolean done;

    public String id;

    @Nullable
    public Response response;

    public Error error;

    public static class Response {
        public List<Chunk> chunks = Collections.emptyList();
    }

    public static class Chunk {
        public int channelTag;

        public List<Alternative> alternatives = Collections.emptyList();
    }

    public static class Alternative {
        public String text;
    }

    public static class Error {
        public int code;
        public String message;

        @Override
        public String toString() {
            return String.format("[%d] %s", code, message);
        }
    }
}
