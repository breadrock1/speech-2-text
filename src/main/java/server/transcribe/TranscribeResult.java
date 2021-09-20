package server.transcribe;

public class TranscribeResult {
    public static final TranscribeResult EMPTY = new TranscribeResult(
            new TranscriptionInput(
                    TranscriptionConfig.newBuilder()
                            .service("unknown")
                            .build()
            ),
            TranscribeContent.EMPTY
    );

    public final TranscriptionInput transcriptionInput;
    public final TranscribeContent content;

    public TranscribeResult(TranscriptionInput transcriptionInput, TranscribeContent content) {
        this.transcriptionInput = transcriptionInput;
        this.content = content;
    }
}
