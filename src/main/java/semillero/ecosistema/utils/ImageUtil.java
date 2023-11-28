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

    /**
     * Verifica si un archivo es una imagen válida, teniendo en cuenta el tipo y el tamaño permitido.
     * @param file El archivo a ser verificado.
     * @return true si el archivo es una imagen válida, false de lo contrario.
     */
    public boolean isValidImage(MultipartFile file) {
        return isImage(file) && isSizeAcceptable(file);
    }

    /**
     * Verifica si el archivo es de tipo imagen y su extensión está permitida.
     * @param file El archivo a ser verificado.
     * @return true si el archivo es de tipo imagen y su extensión está permitida, false de lo contrario.
     */
    private boolean isImage(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image != null && allowedImageTypes.contains(getFileExtension(file));
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Verifica si el tamaño del archivo es aceptable según el límite establecido.
     * @param file El archivo a ser verificado.
     * @return true si el tamaño del archivo es aceptable, false de lo contrario.
     */
    private boolean isSizeAcceptable(MultipartFile file) {
        return file.getSize() <= maxFileSize;
    }

    /**
     * Obtiene la extensión del archivo.
     * @param file El archivo del cual se obtendrá la extensión.
     * @return La extensión del archivo en minúsculas, o una cadena vacía si no tiene extensión.
     */
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
