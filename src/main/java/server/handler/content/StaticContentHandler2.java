package server.handler.content;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import server.handler.HandlerException;
import server.http.ResponseData;
import server.http.annotation.HandleGet;
import server.http.annotation.Path;
import server.http.annotation.SummaryHttpHandler;
import server.logging.Logger;
import server.user.auth.Unauthorized;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@SummaryHttpHandler(path = StaticContentHandler2.HANDLER_PATH)
public class StaticContentHandler2 {

    static final String HANDLER_PATH = "/";

    @Inject
    Logger logger;

    @Inject
    StaticContentConfig config;

    private static final MimetypesFileTypeMap MIME_TYPE_MAP = new MimetypesFileTypeMap();

    @HandleGet
    @Unauthorized
    ResponseData get(@Path String path) throws IOException {
        String filename = path.substring(HANDLER_PATH.length());
        if (StringUtils.isEmpty(filename)) {
            filename = "main";
        }
        if (StringUtils.isEmpty(FilenameUtils.getExtension(filename))) {
            filename += ".html";
        }
        File file = new File(config.contentDirectory, filename);
        if (file.exists()) {
            String mime = MIME_TYPE_MAP.getContentType(file);
            if (mime.equalsIgnoreCase("text/html")) {
                mime += "; charset=utf-8";
            }
            logger.info("Trying to read static file '%s'. MIME is '%s'", file, mime);

            return ResponseData.newBuilder()
                    .header("Content-Type", mime)
                    .body(readFileContent(file))
                    .build();
        } else {
            return ResponseData.newBuilder()
                    .body("Not found")
                    .httpCode(404)
                    .build();
        }
    }

    private byte[] readFileContent(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }
}
