package semillero.ecosistema.dtos.supplier;

import lombok.Data;
import semillero.ecosistema.dtos.category.CategoryDTO;
import semillero.ecosistema.dtos.country.CountryDTO;
import semillero.ecosistema.dtos.province.ProvinceDTO;
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
    private CountryDTO country;
    private ProvinceDTO province;
    private String city;
    private List<String> images;
    private CategoryDTO category;
    private SupplierStatus status;
    private String feedback;
}
