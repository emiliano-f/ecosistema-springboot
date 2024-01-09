package semillero.ecosistema.services;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import semillero.ecosistema.exceptions.GeocodingException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeocodingService {

    @Autowired
    private GeoApiContext context;

    /**
     * Realiza una consulta de geocodificación inversa para obtener la dirección asociada a unas coordenadas geográficas.
     *
     * @param lat La latitud de las coordenadas.
     * @param lng La longitud de las coordenadas.
     * @return Un array de resultados de geocodificación inversa.
     * @throws IOException          Si ocurre un error de lectura o escritura durante la consulta.
     * @throws InterruptedException Si la consulta es interrumpida.
     * @throws ApiException         Si la API de geocodificación arroja una excepción.
     */
    private GeocodingResult[] reverseGeocoding(Double lat, Double lng) throws IOException, InterruptedException, ApiException {
        LatLng latLng = new LatLng(lat, lng);
        return GeocodingApi.reverseGeocode(context, latLng).await();
    }

    /**
     * Obtiene información de ubicación (país, provincia y ciudad) basada en las coordenadas geográficas proporcionadas.
     *
     * @param lat La latitud de las coordenadas.
     * @param lng La longitud de las coordenadas.
     * @return Un mapa que contiene información de ubicación, donde las claves son "country", "province" y "city".
     * @throws GeocodingException Si ocurre un error durante el proceso de obtención de información de ubicación.
     */
    public Map<String, String> getLocation(Double lat, Double lng) throws GeocodingException {
        try {
            GeocodingResult[] results = reverseGeocoding(lat, lng);
            Map<String, String> location = new HashMap<>();

            if (results.length > 0) {
                AddressComponent[] addressComponent = results[0].addressComponents;

                for (AddressComponent component : addressComponent) {
                    for (AddressComponentType type : component.types) {
                        switch (type) {
                            case COUNTRY:
                                location.put("country", component.longName);
                                break;
                            case ADMINISTRATIVE_AREA_LEVEL_1:
                                location.put("province", component.longName.replace(" Province", ""));
                                break;
                            case LOCALITY:
                                location.put("city", component.longName);
                                break;
                        }
                    }
                }
            }

            return location;
        } catch (IOException | InterruptedException | ApiException e) {
            throw new GeocodingException("Error getting location information for coordinates " + lat + " and " + lng);
        }
    }
}
