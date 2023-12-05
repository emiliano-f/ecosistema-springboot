package semillero.ecosistema.dtos.supplier;

import lombok.Data;
import semillero.ecosistema.enumerations.SupplierStatus;

@Data
public class SupplierFeedbackDTO {
    private Long id;
    private String name;
    private SupplierStatus status;
    private String feedback;
}
