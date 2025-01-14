package semillero.ecosistema.configuration;

import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeocodingConfig {

    @Value("${google.maps.api.key}")
    private String apiKey;

    @Bean
    public GeoApiContext context() {
        return new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }
}
