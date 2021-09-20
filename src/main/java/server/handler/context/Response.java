package server.handler.context;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import server.logging.Logger;
import server.logging.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Response {

    private final Logger logger = LoggerFactory.createFor(Response.class);
    private final HttpExchange httpExchange;
    private final Gson gson;

    public Response(HttpExchange httpExchange, Gson gson) {
        this.httpExchange = httpExchange;
        this.gson = gson;
    }

    public void setString(int httpCode, String response) throws IOException {
        httpExchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        setBinary(httpCode, response.getBytes(StandardCharsets.UTF_8));
    }

    public void setJson(int httpCode, Object json) throws IOException {
        httpExchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        setBinary(httpCode, gson.toJson(json).getBytes(StandardCharsets.UTF_8));
    }

    public void setBinary(int httpCode, byte[] response) throws IOException {
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(httpCode, response.length);
            outputStream.write(response);
            outputStream.flush();
        }
        printResponseInfo(httpExchange);
    }

    private void printResponseInfo(final HttpExchange httpExchange) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder()
                .append("Response for: ")
                .append(httpExchange.getRequestMethod())
                .append(' ')
                .append(decode(httpExchange.getRequestURI())).append('\n')
                .append("HTTP code: ").append(httpExchange.getResponseCode()).append('\n')
                .append("Headers: ").append('\n');

        for (Map.Entry<String, List<String>> entry : httpExchange.getResponseHeaders().entrySet()) {
            builder.append("    ").append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }

        logger.info(builder.toString());
    }

    private static String decode(URI uri) throws UnsupportedEncodingException {
        return URLDecoder.decode(uri.toString(), "UTF-8");
    }

}
