package semillero.ecosistema.entities;

import lombok.Getter;
import lombok.Setter;
import semillero.ecosistema.enumerations.UserRol;

import javax.persistence.*;

@Entity
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;

    @Enumerated(EnumType.STRING)
    private String email;
    private boolean deleted;
    private UserRol rol;
    private String telefono;
}
