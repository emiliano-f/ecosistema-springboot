package semillero.ecosistema.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semillero.ecosistema.entities.Country;
import semillero.ecosistema.entities.Province;
import semillero.ecosistema.services.CountryService;
import semillero.ecosistema.services.ProvinceService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/countries")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @Autowired
    private ProvinceService provinceService;

    @GetMapping("/{Id}")
    public ResponseEntity<?> getCountryById(@PathVariable("id") Long id) {
        try {
            Optional<Country> country = countryService.findById(id);
            if (country.isPresent()) {
                return ResponseEntity.ok(country.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllCountries() {
        try {
            List<Country> countries = countryService.findAllCountries();
            return ResponseEntity.ok(countries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An error occurred while processing the request.\"}");
        }
    }

    @GetMapping("/{countryId}/provinces")
    public ResponseEntity<?> getProvincesByCountry(@PathVariable Long countryId) {
        try {
            List<Province> provincesByIdCountry = provinceService.getProvincesByCountry(countryId);
            return ResponseEntity.ok(provincesByIdCountry);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An error occurred while processing the request.\"}");
        }
    }

    @PostMapping("")
    public ResponseEntity<?> save(@Valid @RequestBody Country country) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(countryService.save(country));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"An error occurred while processing the request.\"}");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @PathVariable("id") Long id, @RequestBody Country country) {
        try {
            return ResponseEntity.ok(countryService.update(id, country));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"An error occurred while processing the request.\"}");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            countryService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An error occurred while processing the request.\"}");
        }
    }
}
