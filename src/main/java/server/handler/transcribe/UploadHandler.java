package server.handler.transcribe;

import doc.annotation.Description;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import server.handler.HandlerException;
import server.http.annotation.Body;
import server.http.annotation.HandlePost;
import server.http.annotation.Header;
import server.http.annotation.SummaryHttpHandler;
import server.localization.CurrentLocale;
import server.localization.StringId;
import server.logging.Logger;
import server.response.transcribe.TranscribeUploadResponse;
import server.transcribe.TranscribeManager;
import server.transcribe.TranscriptionConfig;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import static server.response.ErrorResponse.errorResponse;
import static server.util.NumberUtils.safeParseInt;

@SummaryHttpHandler(path = "/2/transcribe/upload")
public class UploadHandler {

    @Inject
    TranscribeManager transcribeManager;

    @Inject
    FileItemFactory fileItemFactory;

    @Inject
    Logger logger;

    @Header("Content-type")
    String contentType;

    @Header("Content-Length")
    int contentLength;

    @Body
    InputStream bodyStream;

    @CurrentLocale
    Locale locale;

    @Description("Загрузка аудио до 32 мегабайт")
    @HandlePost
    TranscribeUploadResponse upload() throws HandlerException {
        logger.info("Handle transcribe upload");
        try {
            final List<FileItem> form = parseMultipartRequest(contentType, contentLength, bodyStream);
            return handleForm(form);
        } catch (FileUploadException e) {
            throw new HandlerException(400, errorResponse(StringId.TRANSCRIBE_UPLOAD_FAILED, locale));
        }
    }

    private List<FileItem> parseMultipartRequest(
            String contentType,
            @Nullable Integer contentLength,
            InputStream bodyStream
    ) throws FileUploadException {
        final ServletFileUpload up = new ServletFileUpload(fileItemFactory);
        up.setFileSizeMax(32 * 1024 * 1024); // 32 Mb
        return up.parseRequest(new RequestContext() {
            @Override
            public String getCharacterEncoding() {
                return "UTF-8";
            }

            @Override
            public String getContentType() {
                return contentType;
            }

            @Override
            public int getContentLength() {
                return (contentLength == null) ? -1 : contentLength;
            }

            @Override
            public InputStream getInputStream() {
                return bodyStream;
            }
        });
    }

    private TranscribeUploadResponse handleForm(List<FileItem> form) throws HandlerException {
        byte[] data = null;
        TranscriptionConfig.Builder configBuilder = TranscriptionConfig.newBuilder();

        for (FileItem fi : form) {
            switch (fi.getFieldName()) {
                case "file":
                    data = fi.get();
                    fi.delete();
                    break;
                case "service":
                    configBuilder.service(fi.getString());
                    break;
                case "diarizationEnabled":
                    configBuilder.diarizationEnabled(true);
                    break;
                case "minSpeakerCount":
                    configBuilder.minSpeakerCount(safeParseInt(fi.getString(), 0));
                    break;
                case "maxSpeakerCount":
                    configBuilder.maxSpeakerCount(safeParseInt(fi.getString(), 0));
                    break;
                case "model":
                    configBuilder.model(fi.getString());
                    break;
            }
        }

        if (data == null || data.length == 0) {
            throw new HandlerException(400, errorResponse(StringId.TRANSCRIBE_NO_FILE_SENT, locale));
        }
        String transcribeId = transcribeManager.start(data, configBuilder.build());
        return new TranscribeUploadResponse(transcribeId);
    }
}
