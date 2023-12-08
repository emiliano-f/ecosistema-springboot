package semillero.ecosistema.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "publication_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicationImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Path cannot be blank")
    @Column(name = "path", nullable = false)
    private String path;
    
    @NotNull(message = "Publication cannot be null")
    @ManyToOne(optional = false)
    @JoinColumn(name = "publication_id")
    @JsonBackReference
    private Publication publication;

}