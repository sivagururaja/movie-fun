package org.superbiz.moviefun;

import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Path;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;

public class FileStore implements BlobStore {

    public FileStore() {}

    @Override
    public void put(Blob blob) throws IOException {
        blob.getPath().toFile().delete();
        blob.getPath().toFile().getParentFile().mkdirs();
        blob.getPath().toFile().createNewFile();

        IOUtils.write(IOUtils.toByteArray(blob.getInputStream()), new FileOutputStream(blob.getPath().toFile()));
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        try {
            Path filePath = getCoverFile(name).toPath();
            String contentType = new Tika().detect(filePath);

            return Optional.ofNullable(new Blob(name, getExistingCoverPath(name), contentType, filePath));
        } catch (FileNotFoundException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void deleteAll() {
        // ...
    }

    private File getCoverFile(String albumId) {
        String coverFileName = format("covers/%d", albumId);
        return new File(coverFileName);
    }

    private FileInputStream getExistingCoverPath(String albumId) throws FileNotFoundException {
        File coverFile = getCoverFile(albumId);
        FileInputStream inputStream;

        if (coverFile.exists()) {
            inputStream = new FileInputStream(coverFile);
        } else {
            inputStream = new FileInputStream(getSystemResource("default-cover.jpg").getFile());
        }

        return inputStream;
    }
}
