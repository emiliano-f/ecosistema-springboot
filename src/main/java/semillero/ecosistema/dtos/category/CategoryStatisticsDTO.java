package semillero.ecosistema.dtos.category;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryStatisticsDTO {
    private String name;
    private Integer registered;
}
