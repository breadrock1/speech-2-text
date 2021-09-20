package server.handler.context;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import server.handler.HandlerException;
import server.handler.validator.RequestValidator;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class Request {

    private final HttpExchange httpExchange;
    private final Gson gson;

    public Request(HttpExchange httpExchange, Gson gson) {
        this.httpExchange = httpExchange;
        this.gson = gson;
    }

    public URI getURI() {
        return httpExchange.getRequestURI();
    }

    public InputStream getBodyStream() {
        return httpExchange.getRequestBody();
    }

    public byte[] readBody() throws IOException {
        int size = Integer.parseInt(httpExchange.getRequestHeaders().getFirst("Content-Length"));
        byte[] data = new byte[size];

        try (InputStream bodyInput = getBodyStream()) {
            int readCount;
            int pos = 0;
            while (pos < size && (readCount = bodyInput.read(data, pos, data.length - pos)) > 0) {
                pos += readCount;
            }
        }

        return data;
    }

    public String readBodyAsString() throws IOException {
        return new String(readBody());
    }

    public <T> T readBodyAsValidJson(Class<T> jsonClass) throws IOException, HandlerException {
        T body = gson.fromJson(readBodyAsString(), jsonClass);
        new RequestValidator().validate(body);
        return body;
    }

    @Nullable
    public String getHeader(String name) {
        return httpExchange.getRequestHeaders().getFirst(name);
    }

}
