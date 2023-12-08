package semillero.ecosistema.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "publication")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Title may not be null")
    @NotBlank(message = "Title may not be blank")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    @NotNull(message = "Description may not be null")
    @NotBlank(message = "Description may not be blank")
    @Size(max = 2500, message = "Description must be less than 2500 characters")
    private String description;

    @NotEmpty(message = "Images list cannot be empty")
    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PublicationImage> images;

    @NotNull(message = "User cannot be null")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", nullable = false)
    private User userCreator;

    @Column(nullable = false)
    private boolean deleted;

    private Integer visualizationsAmount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate dateOfCreation;

    @PrePersist
    private void onPersist() {
        dateOfCreation = LocalDate.now();
        deleted = false;
    }

}