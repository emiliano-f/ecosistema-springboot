package semillero.ecosistema.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import semillero.ecosistema.entities.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationDTO {
    private Long id;
    private String title;
    private String description;
    private User userCreator;
}
