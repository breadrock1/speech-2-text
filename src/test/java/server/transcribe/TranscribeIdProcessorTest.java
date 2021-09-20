package server.transcribe;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class TranscribeIdProcessorTest {

    private final TranscribeIdProcessor processor = new TranscribeIdProcessor();

    @Test
    public void encodeDecode() {
        String id = processor.encode(new TranscribeId(TranscribeServiceName.GOOGLE, "qwe123"));
        TranscribeId transcribeId = processor.decode(id);
        assertThat(transcribeId, is(notNullValue()));
        assertThat(transcribeId.serviceName, is(TranscribeServiceName.GOOGLE));
        assertThat(transcribeId.internalId, is("qwe123"));
    }
}