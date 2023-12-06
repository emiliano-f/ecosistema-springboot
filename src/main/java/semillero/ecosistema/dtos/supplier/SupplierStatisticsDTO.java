package semillero.ecosistema.dtos.supplier;

import lombok.Data;
import semillero.ecosistema.dtos.category.CategoryStatisticsDTO;

import java.util.List;

@Data
public class SupplierStatisticsDTO {
    private Integer approved;
    private Integer inReview;
    private Integer denied;
    private List<CategoryStatisticsDTO> categories;
}
