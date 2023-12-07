package semillero.ecosistema.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "category_image")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CategoryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Path cannot be blank")
    @Column(name = "path", nullable = false)
    private String path;

    @NotNull(message = "Category cannot be null")
    @OneToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;
}
