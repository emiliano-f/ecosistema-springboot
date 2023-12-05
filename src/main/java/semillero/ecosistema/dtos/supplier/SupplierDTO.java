package semillero.ecosistema.dtos.supplier;

import lombok.Data;
import semillero.ecosistema.enumerations.SupplierStatus;

import java.util.List;

@Data
public class SupplierDTO {
    private Long id;
    private String name;
    private String description;
    private String shortDescription;
    private String phone;
    private String email;
    private String facebook;
    private String instagram;
    private String country;
    private String province;
    private String city;
    private List<String> images;
    private String category;
    private SupplierStatus status;
}
