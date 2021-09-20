package server.http.filter;

import com.google.gson.Gson;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import server.handler.context.Response;
import server.http.AttributeHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PreflightRequestFilter extends Filter {

    @Override
    public void doFilter(HttpExchange httpExchange, Chain chain) throws IOException {
        if (httpExchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            handlePreflightRequest(httpExchange);
        } else {
            chain.doFilter(httpExchange);
        }
    }

    @Override
    public String description() {
        return "Preflight request filter";
    }

    private void handlePreflightRequest(final HttpExchange httpExchange) throws IOException {
        List<String> methods = new ArrayList<>(AttributeHelper.getHttpMethods(httpExchange));
        if (!methods.contains("OPTIONS")) {
            methods.add("OPTIONS");
        }
        httpExchange.getResponseHeaders().put("Access-Control-Allow-Methods", methods);

        List<String> headers = httpExchange.getRequestHeaders().get("Access-Control-Request-Headers");
        if (headers != null) {
            httpExchange.getResponseHeaders().put("Access-Control-Allow-Headers", headers);
        }
        new Response(httpExchange, new Gson()).setString(204, "");
    }
}
