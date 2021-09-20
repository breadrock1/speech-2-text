package server.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import server.handler.context.Request;
import server.handler.context.Response;
import server.http.verifier.RequestVerifier;
import server.verifier.VerificationException;

import java.io.IOException;
import java.lang.reflect.Method;

class RequestVerifierFilter extends Filter {

    private final RequestVerifier verifier;

    RequestVerifierFilter(RequestVerifier verifier) {
        this.verifier = verifier;
    }

    @Override
    public void doFilter(HttpExchange httpExchange, Chain chain) throws IOException {
        Gson gson = new Gson();
        Request request = new Request(httpExchange, gson);
        Method method = AttributeHelper.getHandlerMethod(httpExchange);
        try {
            verifier.verify(request, method);
            chain.doFilter(httpExchange);
        } catch (VerificationException e) {
            new Response(httpExchange, gson).setJson(e.getHttpCode(), e.getErrorResponse());
        }
    }

    @Override
    public String description() {
        return "Wrapper for " + verifier.getClass().getName();
    }
}
