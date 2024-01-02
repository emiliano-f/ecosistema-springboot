package semillero.ecosistema.dtos.publication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PublicationStatisticsDTO {
    private String title;
    private Integer visualizationsAmount;
    private LocalDate dateOfCreation;

}
