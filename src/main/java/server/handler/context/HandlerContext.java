package server.handler.context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;

public class HandlerContext {

    private final Request request;
    private final Response response;

    private HandlerContext(HttpExchange httpExchange) {
        final Gson gson = new GsonBuilder().create();
        request = new Request(httpExchange, gson);
        response = new Response(httpExchange, gson);
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    public static HandlerContext fromHttpExchange(HttpExchange httpExchange) {
        return new HandlerContext(httpExchange);
    }
}
