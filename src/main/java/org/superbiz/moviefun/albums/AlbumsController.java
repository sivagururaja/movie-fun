package org.superbiz.moviefun.albums;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.Blob;
import org.superbiz.moviefun.BlobStore;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;
    private final BlobStore fileStore;

    public AlbumsController(AlbumsBean albumsBean, BlobStore fileStore) {
        this.albumsBean = albumsBean;
        this.fileStore = fileStore;
    }


    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        saveUploadToFile(uploadedFile, getCoverFile(albumId));

        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {

        Optional<Blob> blob = fileStore.get("" + albumId);
        byte[] asByteArray = getAsByteArray(blob.get());
        HttpHeaders headers = createImageHttpHeaders(blob.get(), asByteArray);

        return new HttpEntity<>(asByteArray, headers);
    }


    private void saveUploadToFile(@RequestParam("file") MultipartFile uploadedFile, File targetFile) throws IOException {
        Blob blob = new Blob(targetFile.getName(), uploadedFile.getInputStream(), null, targetFile.toPath());
        fileStore.put(blob);
    }

    private HttpHeaders createImageHttpHeaders(Blob blob, byte[] bytes) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(blob.getContentType()));
        headers.setContentLength(bytes.length);
        return headers;
    }

    private byte[] getAsByteArray(Blob blob) throws IOException {
        return IOUtils.toByteArray(blob.getInputStream());
    }

    private File getCoverFile(@PathVariable long albumId) {
        String coverFileName = format("covers/%d", albumId);
        return new File(coverFileName);
    }
}
