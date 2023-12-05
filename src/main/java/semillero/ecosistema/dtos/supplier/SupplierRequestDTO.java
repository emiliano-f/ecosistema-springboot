package semillero.ecosistema.dtos.supplier;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupplierRequestDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 300)
    private String description;

    @NotBlank
    @Size(max = 50)
    private String shortDescription;

    @NotBlank
    private String phone;

    @Email
    @NotBlank
    private String email;

    private String facebook;

    private String instagram;

    @NotNull
    private Long countryId;

    @NotNull
    private Long provinceId;

    @NotBlank
    private String city;

    @NotNull
    private Long categoryId;

    @NotNull
    private Long userId;
}
