package server.response.transcribe;

import doc.annotation.Description;
import server.response.GenericResponse;
import server.transcribe.TranscribeContent;
import server.transcribe.TranscribeResult;
import server.transcribe.TranscriptionConfig;
import server.transcribe.TranscriptionInput;

import javax.annotation.Nullable;
import java.util.List;

public class TranscribeGetResponse extends GenericResponse {

    @Description("URL звукового файла для проигрывания")
    final String audioUrl;

    @Description("Входные парметры")
    final TranscriptionConfig config;

    @Nullable
    @Description("Список элементов, если результат успешен")
    final List<TranscribeContent.Entry> entries;

    @Nullable
    @Description("Текст ошибки, если результат неуспешен")
    final String errorText;

    private TranscribeGetResponse(
            @Nullable List<TranscribeContent.Entry> entries,
            @Nullable String errorText,
            TranscriptionInput input
    ) {
        this.entries = entries;
        this.audioUrl = input.getAudioUrl();
        this.config = input.getConfig();
        this.errorText =errorText;
    }

    public static TranscribeGetResponse from(TranscribeResult result) {
        if (result.content.errorText != null) {
            return new TranscribeGetResponse(null, result.content.errorText, result.transcriptionInput);
        }
        return new TranscribeGetResponse(result.content.entries, null, result.transcriptionInput);
    }
}
