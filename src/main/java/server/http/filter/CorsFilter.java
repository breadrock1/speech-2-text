package server.http.filter;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class CorsFilter extends Filter {

    @Override
    public void doFilter(HttpExchange httpExchange, Chain chain) throws IOException {
        String origin = httpExchange.getRequestHeaders().getFirst("Origin");
        if (origin != null) {
            httpExchange.getResponseHeaders().set("Access-Control-Allow-Origin", origin);
        }
        chain.doFilter(httpExchange);
    }

    @Override
    public String description() {
        return "CORS filter";
    }
}
