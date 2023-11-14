package semillero.ecosistema.security;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Getter
public class JwtProperties {
    private final String secretKey = "rzxlszyykpbgqcflzxsqcysyhljt";
    private final long validityInMs = 3600000; // 1h
}
