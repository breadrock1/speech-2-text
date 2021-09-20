package server.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseData {

    final int httpCode;
    final Map<String, List<String>> headers;
    final Object body;

    private ResponseData(Builder builder) {
        httpCode = builder.httpCode;
        headers = builder.headers;
        body = builder.body;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, List<String>> headers = new HashMap<>();

        private int httpCode = 200;
        private Object body;

        public Builder httpCode(int httpCode) {
            this.httpCode = httpCode;
            return this;
        }

        public Builder header(String name, String value) {
            List<String> values = headers.computeIfAbsent(name, key -> new ArrayList<>());
            values.add(value);
            return this;
        }

        public Builder body(Object body) {
            this.body = body;
            return this;
        }

        public ResponseData build() {
            return new ResponseData(this);
        }
    }
}
