package server.transcribe.yandex;

import server.transcribe.TranscribeContent;
import server.transcribe.yandex.api.GetResultResponse;

import java.util.ArrayList;
import java.util.List;

class ResultConverter {

    public TranscribeContent convert(List<GetResultResponse.Chunk> chunks) {
        List<TranscribeContent.Entry> entries = new ArrayList<>(chunks.size());
        for (GetResultResponse.Chunk chunk : chunks) {
            for (GetResultResponse.Alternative alternative : chunk.alternatives) {
                entries.add(new TranscribeContent.Entry(-1, -1, alternative.text.trim()));
            }
        }

        return TranscribeContent.withEntries(entries);
    }
}
