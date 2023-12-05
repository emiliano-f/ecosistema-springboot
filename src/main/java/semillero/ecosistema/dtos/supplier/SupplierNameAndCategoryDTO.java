package semillero.ecosistema.dtos.supplier;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SupplierNameAndCategoryDTO extends SupplierNameDTO {
    private String category;
}
