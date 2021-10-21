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

    public static final int REVISION = 55;

    public static void main(String[] args) throws IOException {
        final ServerConfig config = new ServerConfigReader(new File(args[0])).read();

        LoggerFactory.init(config.loggerType, Collections.singleton(Response.class));
        Logger logger = LoggerFactory.createFor(DemoServer.class);
        logger.info("Start");

        final DependencyContainer dependencyContainer = new DependencyContainer();
        final StaticContentConfig staticContentConfig = new StaticContentConfig(new File(config.staticContentDir));
        dependencyContainer.put(staticContentConfig);
        dependencyContainer.addFactory(Logger.class, LoggerFactory::createFor);

        final AccessTokenManager accessTokenManager = AccessTokenManager.withBase64EncodedKey(config.authSecretKey);
        dependencyContainer.put(accessTokenManager);

        final Firestore firestore = initFirestore(logger);
        final UserManager userManager = new UserManager(firestore);
        final UserSettingsManager userSettingsManager = new UserSettingsManager(firestore);
        final ConferenceManager conferenceManager = ConferenceManager.createInstance(firestore);
        final SpeechpadManager speechpadManager = new SpeechpadManager(firestore);
        final TranscribeManager transcribeManager = new TranscribeManager();
        final DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
        dependencyContainer.put(transcribeManager.init());
        dependencyContainer.put(FileItemFactory.class, diskFileItemFactory);
        dependencyContainer.put(conferenceManager);
        dependencyContainer.put(speechpadManager);
        dependencyContainer.put(userManager);
        dependencyContainer.put(userSettingsManager);

        final InetSocketAddress inetSocketAddress = new InetSocketAddress(config.host, config.port);
        final LocaleHandlerDataProvider localeHandlerDataProvider = new LocaleHandlerDataProvider();
        final AuthHttpRequestVerifier authHttpRequestVerifier = new AuthHttpRequestVerifier(accessTokenManager);
        final AuthorizedUserProvider authUserProvider = new AuthorizedUserProvider(userManager, accessTokenManager);
        SummaryHttpServer.create(inetSocketAddress)
                .addRequestVerifier(authHttpRequestVerifier)
                .registerHttpHandlerDataProvider(Inject.class, dependencyContainer)
                .registerHttpHandlerDataProvider(AuthorizedUser.class, authUserProvider)
                .registerHttpHandlerDataProvider(CurrentLocale.class, localeHandlerDataProvider)
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

        final CleanUpTask cleanUpTask = new CleanUpTask(conferenceManager);
        RecurringTaskManager.getInstance().schedule(cleanUpTask);
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
