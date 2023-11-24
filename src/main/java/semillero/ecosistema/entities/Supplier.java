package semillero.ecosistema.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import semillero.ecosistema.enumerations.SupplierStatus;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Phone cannot be blank")
    @Column(name = "phone", nullable = false)
    private String phone;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "facebook", nullable = true)
    private String facebook;

    @Column(name = "instagram", nullable = true)
    private String instagram;

    @NotNull(message = "Country cannot be null")
    @ManyToOne(optional = false)
    @JoinColumn(name = "country_id")
    private Country country;

    @NotNull(message = "Province cannot be null")
    @ManyToOne(optional = false)
    @JoinColumn(name = "province_id")
    private Province province;

    @NotBlank(message = "City cannot be blank")
    @Column(name = "city", nullable = false)
    private String city;

    @NotEmpty(message = "Images list cannot be empty")
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<SupplierImage> images;

    @NotNull(message = "Category cannot be null")
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SupplierStatus status;

    @NotNull(message = "Deleted flag cannot be null")
    @Column(name = "deleted", nullable = false)
    @Convert(converter = org.hibernate.type.YesNoConverter.class)
    private Boolean deleted;

    @Column(name = "feedback", nullable = true, columnDefinition = "TEXT")
    private String feedback;

    @NotNull(message = "User cannot be null")
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull(message = "Creation date cannot be null")
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @PrePersist
    private void onPersist() {
        createdAt = LocalDate.now();
    }
}
