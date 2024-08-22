package kr.co.ultari.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/tui-editor")
public class FileApiController {

    @Value("${ultari.attach.path:attach}")
    private String uploadDir;

    //private final String uploadDir = Paths.get("D:", "tui-editor", "upload").toString();

    @PostMapping(value = "/image-upload")
    public String uploadEditorImage(@RequestParam final MultipartFile image) throws IOException {
        if (image.isEmpty()) {
            return "";
        }

        if(!checkImageType(image)) return "";

        String orgFilename = image.getOriginalFilename();
        //String base64 = Base64.getEncoder().encodeToString(image.getBytes());
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String extension = orgFilename.substring(orgFilename.lastIndexOf(".") + 1);

        String saveFilename = uuid + "." + extension;
        String fileFullPath = Paths.get(uploadDir, saveFilename).toString();
        log.debug(fileFullPath);
        File dir = new File(uploadDir);
        if (dir.exists() == false) {
            dir.mkdirs();
        }

        try {
            File uploadFile = new File(fileFullPath);
            image.transferTo(uploadFile);
            //return base64;
            return saveFilename;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/image-print", produces = { MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public byte[] printEditorImage(final HttpServletResponse response, @RequestParam final String filename) {
        log.debug(filename);
        if(filename.indexOf("..") > -1 || filename.indexOf("../") > -1 || filename.indexOf("..\\") > -1) throw new RuntimeException();

        String fileFullPath = Paths.get(uploadDir, filename).toString();
        log.debug(fileFullPath);
        File uploadedFile = new File(fileFullPath);
        if (!uploadedFile.exists()) {
            throw new RuntimeException();
        }

        try {
            byte[] imageBytes = Files.readAllBytes(uploadedFile.toPath());
            return imageBytes;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkImageType(MultipartFile file) {
        Tika tika = new Tika();
        try {
            List<String> notValidTypeList = Arrays.asList("image/jpeg", "image/pjpeg", "image/png", "image/gif", "image/bmp", "image/x-windows-bmp");
            InputStream inputStream = file.getInputStream();
            String mimeType = tika.detect(inputStream);
            log.debug("FileName : " + file.getOriginalFilename() + " MimeType : " + mimeType);

            boolean isValid = notValidTypeList.stream().anyMatch(notValidType -> notValidType.equalsIgnoreCase(mimeType));

            return isValid;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
