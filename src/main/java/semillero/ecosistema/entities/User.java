package semillero.ecosistema.entities;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import semillero.ecosistema.enumerations.UserRol;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name may not be blank")
    @Column(name = "name", nullable = false)
    private String nombre;

    @NotBlank(message = "Last name may not be blank")
    @Column(name = "last_name", nullable = false)
    private String apellido;

    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Basic
    @Column(name = "deleted", nullable = false)
    @Convert(converter = org.hibernate.type.YesNoConverter.class)
    private boolean deleted;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRol rol;

    @NotBlank(message = "Phone may not be blank")
    @Column(name = "phone", nullable = false)
    private String telefono;
}
