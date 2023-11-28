package semillero.ecosistema.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import semillero.ecosistema.utils.ImageUtil;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ImageUtil imageUtil;

    /**
     * Sube una imagen a Cloudinary con el nombre y la carpeta especificados.
     * @param image El archivo de imagen a ser cargado.
     * @param name El nombre que se asignará a la imagen en Cloudinary.
     * @param folder La carpeta en la que se almacenará la imagen en Cloudinary.
     * @return La URL de la imagen cargada en Cloudinary.
     * @throws IOException Si la imagen es inválida o su tamaño excede el límite permitido.
     */
    public String uploadImage(MultipartFile image, String name, String folder) throws IOException {
        if (!imageUtil.isValidImage(image)) {
            throw new IOException("Invalid image or image size exceeds the allowed limit.");
        }

        String publicId = folder + "/" + name;

        Map params = ObjectUtils.asMap(
                "public_id", publicId,
                "overwrite", true,
                "resource_type", "image"
        );

        return cloudinary.uploader().upload(image.getBytes(), params).get("url").toString();
    }

    /**
     * Elimina una imagen de Cloudinary según el nombre y la carpeta especificados.
     * @param name El nombre de la imagen a ser eliminada.
     * @param folder La carpeta en la que se encuentra la imagen en Cloudinary.
     * @return Un mapa con información sobre la eliminación de la imagen.
     * @throws IOException Si ocurre un error durante la eliminación de la imagen.
     */
    public Map deleteImage(String name, String folder) throws IOException {
        String publicId = folder + "/" + name;

        return cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
    }
}
