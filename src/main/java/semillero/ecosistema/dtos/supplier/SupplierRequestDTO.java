package semillero.ecosistema.dtos.supplier;

import lombok.Data;

@Data
public class SupplierRequestDTO {
    private Long id;
    private String name;
    private String description;
    private String phone;
    private String email;
    private String facebook;
    private String instagram;
    private Long countryId;
    private Long provinceId;
    private String city;
    private Long categoryId;
    private Long userId;
}
