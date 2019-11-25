package org.superbiz.moviefun;

import java.io.InputStream;
import java.nio.file.Path;

public class Blob {
    public final Path path;
    public final String name;
    public final InputStream inputStream;
    public final String contentType;

    public Blob(String name, InputStream inputStream, String contentType, Path path) {
        this.name = name;
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getContentType() {
        return contentType;
    }
}
