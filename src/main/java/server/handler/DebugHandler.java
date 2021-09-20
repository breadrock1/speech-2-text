package server.handler;

import server.DemoServer;
import server.user.auth.Unauthorized;
import server.http.annotation.HandleGet;
import server.http.annotation.SummaryHttpHandler;

@SummaryHttpHandler(path = "/")
public class DebugHandler {

    @HandleGet("/version")
    @Unauthorized
    String version() {
        return String.format("Hello! Revision = %d", DemoServer.REVISION);
    }
}
