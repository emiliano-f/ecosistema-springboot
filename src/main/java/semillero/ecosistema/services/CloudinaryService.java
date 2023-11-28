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

    public Map deleteImage(String name, String folder) throws IOException {
        String publicId = folder + "/" + name;

        return cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
    }
}
