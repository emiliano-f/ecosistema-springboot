package semillero.ecosistema.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semillero.ecosistema.entities.Province;
import semillero.ecosistema.services.ProvinceService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/provinces")
public class ProvinceController {

    @Autowired
    private ProvinceService provinceService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getProvinceById(@PathVariable("id") Long id) {
        try {
            Optional<Province> province = provinceService.findById(id);
            if (province.isPresent()) {
                return ResponseEntity.ok(province.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllProvinces() {
        try {
            List<Province> provinces = provinceService.findAllProvinces();
            return ResponseEntity.ok(provinces);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An error occurred while processing the request.\"}");
        }
    }

    @PostMapping("")
    public ResponseEntity<?> save(@Valid @RequestBody Province province) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(provinceService.save(province));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"An error occurred while processing the request.\"}");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Province province, @PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(provinceService.update(id, province));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"An error occurred while processing the request.\"}");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            provinceService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An error occurred while processing the request.\"}");
        }
    }
}
