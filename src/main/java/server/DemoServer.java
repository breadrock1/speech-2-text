package server;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import server.conference.CleanUpTask;
import server.conference.ConferenceManager;
import server.handler.DebugHandler;
import server.handler.conference.ConferenceHandler;
import server.handler.conference.DownloadHandler;
import server.handler.content.StaticContentConfig;
import server.handler.content.StaticContentHandler2;
import server.handler.context.Response;
import server.handler.speechpad.SpeechpadHandler;
import server.handler.transcribe.TranscribeHandler;
import server.handler.transcribe.UploadHandler;
import server.handler.user.UserHandler;
import server.handler.user.UserSettingsHandler;
import server.http.SummaryHttpServer;
import server.localization.CurrentLocale;
import server.localization.LocaleHandlerDataProvider;
import server.logging.Logger;
import server.logging.LoggerFactory;
import server.recurring.RecurringTaskManager;
import server.speechpad.SpeechpadManager;
import server.transcribe.TranscribeManager;
import server.user.UserManager;
import server.user.UserSettingsManager;
import server.user.auth.AccessTokenManager;
import server.user.auth.AuthHttpRequestVerifier;
import server.user.auth.AuthorizedUser;
import server.user.auth.AuthorizedUserProvider;
import server.util.GoogleCloudUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;

public class DemoServer {

    public static final int REVISION = 54;

    public static void main(String[] args) throws IOException {
        final ServerConfig config = new ServerConfigReader(new File(args[0])).read();

        LoggerFactory.init(config.loggerType, Collections.singleton(Response.class));
        Logger logger = LoggerFactory.createFor(DemoServer.class);
        logger.info("Start");

        DependencyContainer dependencyContainer = new DependencyContainer();
        dependencyContainer.put(new StaticContentConfig(new File(config.staticContentDir)));
        dependencyContainer.addFactory(Logger.class, LoggerFactory::createFor);

        AccessTokenManager accessTokenManager = AccessTokenManager.withBase64EncodedKey(config.authSecretKey);
        dependencyContainer.put(accessTokenManager);

        final Firestore firestore = initFirestore(logger);
        final ConferenceManager conferenceManager = ConferenceManager.createInstance(firestore);

        dependencyContainer.put(new TranscribeManager().init());
        dependencyContainer.put(FileItemFactory.class, new DiskFileItemFactory());
        dependencyContainer.put(conferenceManager);
        dependencyContainer.put(new SpeechpadManager());
        dependencyContainer.put(new UserManager(firestore));
        dependencyContainer.put(new UserSettingsManager(firestore));

        SummaryHttpServer.create(new InetSocketAddress(config.host, config.port))
                .addRequestVerifier(new AuthHttpRequestVerifier(accessTokenManager))
                .registerHttpHandlerDataProvider(Inject.class, dependencyContainer)
                .registerHttpHandlerDataProvider(AuthorizedUser.class, new AuthorizedUserProvider(new UserManager(firestore), accessTokenManager))
                .registerHttpHandlerDataProvider(CurrentLocale.class, new LocaleHandlerDataProvider())
                .registerHandler(StaticContentHandler2.class)
                .registerHandler(DebugHandler.class)
                .registerHandler(ConferenceHandler.class)
                .registerHandler(DownloadHandler.class)
                .registerHandler(UserHandler.class)
                .registerHandler(UserSettingsHandler.class)
                .registerHandler(SpeechpadHandler.class)
                .registerHandler(TranscribeHandler.class)
                .registerHandler(UploadHandler.class)
                .start();

        RecurringTaskManager.getInstance().schedule(new CleanUpTask(conferenceManager));
    }

    private static Firestore initFirestore(Logger logger) throws IOException {
        logger.info("Firestore initialization");
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setProjectId(GoogleCloudUtils.getProjectId())
                .build();
        FirebaseApp.initializeApp(options);
        logger.info("Firestore initialization done");
        return FirestoreClient.getFirestore();
    }

}
