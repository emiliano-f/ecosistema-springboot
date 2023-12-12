package semillero.ecosistema.dtos.publication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class PublicationRequestDTO implements Serializable {
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    @Size(max = 2500)
    private String description;

    @NotNull
    private Long userId;


}
