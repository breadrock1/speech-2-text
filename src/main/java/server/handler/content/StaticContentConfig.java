package server.handler.content;

import java.io.File;

public class StaticContentConfig {
    public final File contentDirectory;

    public StaticContentConfig(File contentDirectory) {
        this.contentDirectory = contentDirectory;
    }
}
