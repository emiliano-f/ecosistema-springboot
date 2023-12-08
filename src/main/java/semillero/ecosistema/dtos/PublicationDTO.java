package semillero.ecosistema.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PublicationDTO implements Serializable {
    private Long id;
    private String title;
    private String description;
    private Long userId;
    private List<String> imageUrls;
}