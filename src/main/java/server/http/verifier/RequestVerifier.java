package server.http.verifier;

import server.handler.context.Request;
import server.verifier.VerificationException;

import java.lang.reflect.Method;

public interface RequestVerifier {
    void verify(Request request, Method handlerMethod) throws VerificationException;
}
