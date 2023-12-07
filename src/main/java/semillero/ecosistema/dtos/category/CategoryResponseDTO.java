package semillero.ecosistema.dtos.category;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryResponseDTO extends CategoryDTO {
    private String image;
}
