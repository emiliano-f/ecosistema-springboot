package semillero.ecosistema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "semillero.ecosistema.security.SecurityConfig")
public class EcosistemaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcosistemaApplication.class, args);
	}

}
