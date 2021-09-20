package server.http;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import server.http.annotation.Body;
import server.http.annotation.Header;
import server.http.annotation.Path;
import server.http.annotation.Query;
import server.http.filter.CorsFilter;
import server.http.filter.PreflightRequestFilter;
import server.http.provider.BodyHttpHandlerDataProvider;
import server.http.provider.HeaderHandlerDataProvider;
import server.http.provider.HttpHandlerDataProvider;
import server.http.provider.HttpHandlerDataProviderManager;
import server.http.provider.PathHttpHandlerDataProvider;
import server.http.provider.QueryHttpHandlerDataProvider;
import server.http.verifier.RequestVerifier;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SummaryHttpServer {
    private final HttpServer httpServer;

    private final HttpHandlerDataProviderManager httpHandlerDataProviderManager = new HttpHandlerDataProviderManager();
    private final List<Filter> filters = new ArrayList<>();

    private SummaryHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    private SummaryHttpServer init() {
        registerHttpHandlerDataProvider(Query.class, new QueryHttpHandlerDataProvider());
        registerHttpHandlerDataProvider(Path.class, new PathHttpHandlerDataProvider());
        registerHttpHandlerDataProvider(Body.class, new BodyHttpHandlerDataProvider());
        registerHttpHandlerDataProvider(Header.class, new HeaderHandlerDataProvider());
        filters.add(new CorsFilter());
        filters.add(new PreflightRequestFilter());
        return this;
    }

    public SummaryHttpServer addRequestVerifier(RequestVerifier verifier) {
        filters.add(new RequestVerifierFilter(verifier));
        return this;
    }

    public <T extends Annotation> SummaryHttpServer registerHttpHandlerDataProvider(
            Class<T> annotationType,
            HttpHandlerDataProvider<T> provider
    ) {
        httpHandlerDataProviderManager.register(annotationType, provider);
        return this;
    }

    public SummaryHttpServer registerHandler(Class<?> handlerClass) {
        new HandlerClassWalker((httpMethod, path, method) -> createContext(httpMethod, path, handlerClass, method))
                .walk(handlerClass);
        return this;
    }

    public SummaryHttpServer registerHandlers(List<Class<?>> handlerClassList) {
        handlerClassList.forEach(this::registerHandler);
        return this;
    }

    public void start() {
        httpServer.start();
    }

    private void createContext(String httpMethod, String path, Class<?> handlerClass, Method handlerMethod) {
        HttpMethodHandler handler = new HttpMethodHandler(
                httpMethod, handlerClass, handlerMethod, httpHandlerDataProviderManager
        );
        HttpContext context = httpServer.createContext(path, handler);
        context.getFilters().addAll(filters);
        AttributeHelper.putHandlerMethod(context, handlerMethod);
        AttributeHelper.putHttpMethods(context, Collections.singletonList(httpMethod));
    }

    public static SummaryHttpServer create(InetSocketAddress address) throws IOException {
        return new SummaryHttpServer(HttpServer.create(address, 0)).init();
    }
}
