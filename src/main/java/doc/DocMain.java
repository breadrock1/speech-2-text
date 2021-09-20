package doc;

import server.handler.conference.ConferenceHandler;
import server.handler.conference.DownloadHandler;
import server.handler.speechpad.SpeechpadHandler;
import server.handler.transcribe.TranscribeHandler;
import server.handler.transcribe.UploadHandler;
import server.handler.user.UserHandler;
import server.handler.user.UserSettingsHandler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class DocMain {

    private static final List<Class<?>> HANDLERS = Arrays.asList(
            ConferenceHandler.class,
            DownloadHandler.class,
            UserHandler.class,
            UserSettingsHandler.class,
            SpeechpadHandler.class,
            TranscribeHandler.class,
            UploadHandler.class
    );

    public static void main(String[] args) throws IOException {
        String html = new DocGenerator()
                .setBaseUrl("https://dpforge.com/")
                .addGroup("Авторизация и пользователь", "/\\d+/user/.*")
                .addGroup("Расшифровка файлов", "/\\d+/transcribe/.*")
                .addGroup("Конференции", "/\\d+/conference/.*")
                .addGroup("Speechpad", "/\\d+/speechpad/.*")
                .generate(HANDLERS);
        File outputDir = new File(args[0]);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new RuntimeException("Fail to create directory:" + outputDir);
        }
        Files.write(Paths.get(args[0], args[1]), html.getBytes(StandardCharsets.UTF_8));
    }
}
