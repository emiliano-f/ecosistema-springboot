package semillero.ecosistema.entities;

import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import semillero.ecosistema.enumerations.UserRol;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private boolean deleted;
    private UserRol rol;
    private String telefono;
}
