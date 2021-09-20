package server.util;

import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class GoogleCloudUtils {

    private GoogleCloudUtils() {
    }

    public static String getProjectId() throws IOException {
        String filePath = Objects.requireNonNull(
                System.getenv("GOOGLE_APPLICATION_CREDENTIALS"),
                "No file with credentials"
        );
        try (FileInputStream fis = new FileInputStream(filePath)) {
            return ServiceAccountCredentials.fromStream(fis).getProjectId();
        }
    }
}
