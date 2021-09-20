package client;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import server.response.conference.ConferenceCreateResponse;
import server.response.conference.ConferenceParticipantTranscript;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.util.List;

public class ConferenceDemoClient {

    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String RESET = "\033[0m";

    private static final int BYTES_PER_BUFFER = 6400;
    private static final ConferenceService SERVICE = new Retrofit.Builder()
            .baseUrl("http://localhost:8080/")
//            .baseUrl("https://s2t-service-iburvpjgoa-lz.a.run.app")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ConferenceService.class);

    public static void main(String[] args) throws LineUnavailableException, IOException {
        AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        if (!AudioSystem.isLineSupported(targetInfo)) {
            System.out.println("Microphone not supported");
            System.exit(0);
        }
        TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
        targetDataLine.open(audioFormat);

        ConferenceCreateResponse createResponse = createConference();
        final String conferenceId = createResponse.conferenceId;
        final String participantId = createResponse.hostId;

        System.out.println(YELLOW);
        System.out.println("Start speaking...Press Ctrl-C to stop");
        targetDataLine.start();
        byte[] data = new byte[BYTES_PER_BUFFER];
        while (targetDataLine.isOpen()) {
            int numBytesRead = targetDataLine.read(data, 0, data.length);
            if ((numBytesRead <= 0) && (targetDataLine.isOpen())) {
                continue;
            }
            List<ConferenceParticipantTranscript> transcripts = chunk(conferenceId, participantId, data.clone());
            printTranscripts(transcripts);
            if (exitResponse(transcripts)) {
                break;
            }
        }

        finish(conferenceId);
        System.out.print(RESET);
        System.out.println("DONE");
    }

    private static void printTranscripts(final List<ConferenceParticipantTranscript> transcripts) {
        for (ConferenceParticipantTranscript transcript : transcripts) {
            if (transcript.isFinal) {
                System.out.print(GREEN);
            } else {
                System.out.print(RED);
            }
            System.out.print("\033[2K\r");
            System.out.printf(
                    "%s: %s [confidence: %.2f]\n",
                    transcript.participantName,
                    transcript.value,
                    transcript.confidence
            );
        }
    }

    private static boolean exitResponse(final List<ConferenceParticipantTranscript> transcripts) {
        for (ConferenceParticipantTranscript transcript : transcripts) {
            if (transcript.isFinal) {
                switch (transcript.value.toLowerCase().trim()) {
                    case "стоп":
                    case "выход":
                        return true;
                }
            }
        }
        return false;
    }

    private static ConferenceCreateResponse createConference() throws IOException {
        return execute(SERVICE.create("MyConf", "John"));
    }

    private static List<ConferenceParticipantTranscript> chunk(
            final String conferenceId,
            final String participantId,
            final byte[] data
    ) throws IOException {
        RequestBody body = RequestBody.create(MediaType.get("audio/wav"), data);
        return execute(SERVICE.chunk(conferenceId, participantId, body)).transcripts;
    }

    private static void finish(final String conferenceId) throws IOException {
        execute(SERVICE.finish(conferenceId));
    }

    @SuppressWarnings("ConstantConditions")
    private static <T> T execute(Call<T> call) throws IOException {
        Response<T> response = call.execute();
        if (!response.isSuccessful()) {
            System.err.println(response.errorBody().string());
            System.exit(1);
        }
        return response.body();
    }
}
