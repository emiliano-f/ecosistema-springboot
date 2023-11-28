package semillero.ecosistema.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class ImageUtil {

    private final int maxFileSize = 5 * 1024 * 1024; // 5MB

    private final List<String> allowedImageTypes = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp");

    public boolean isValidImage(MultipartFile file) {
        return isImage(file) && isSizeAcceptable(file);
    }

    private boolean isImage(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image != null && allowedImageTypes.contains(getFileExtension(file));
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isSizeAcceptable(MultipartFile file) {
        return file.getSize() <= maxFileSize;
    }

    private String getFileExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();

        if (originalFileName != null) {
            int lastDotIndex = originalFileName.lastIndexOf(".");
            if (lastDotIndex != -1) {
                return originalFileName.substring(lastDotIndex + 1).toLowerCase();
            }
        }

        return "";
    }
}
