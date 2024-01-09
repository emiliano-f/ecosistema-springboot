package semillero.ecosistema.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import semillero.ecosistema.dtos.supplier.SupplierFeedbackDTO;
import semillero.ecosistema.dtos.supplier.SupplierRequestDTO;
import semillero.ecosistema.exceptions.GeocodingException;
import semillero.ecosistema.exceptions.MaxSuppliersReachedException;
import semillero.ecosistema.services.SupplierService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService service;

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error Interno del Servidor.\"}");
        }
    }

    @GetMapping("/allNames")
    public ResponseEntity<?> getAllAcceptedNames() {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.findAllAcceptedNames());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error Interno del Servidor.\"}");
        }
    }

    @GetMapping("/allAccepted")
    public ResponseEntity<?> getAllAccepted() {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.findAllAccepted());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error Interno del Servidor.\"}");
        }
    }

    @GetMapping("/searchByName")
    public ResponseEntity<?> getAllAcceptedByName(@RequestParam(name = "name", required = true) String name) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.findAllAcceptedByName(name));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"La consulta no puede estar vacía.\"}");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Proveedor no encontrados con nombre " + name + ".\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error Interno del Servidor.\"}");
        }
    }

    @GetMapping("/searchByCategory")
    public ResponseEntity<?> getAllAcceptedByCategory(@RequestParam(name = "category", required = true) String category) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.findAllAcceptedByCategory(category));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Categoria no encontrada.\"}");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Proveedor no encontrados con categoria " + category + ".\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error Interno del Servidor.\"}");
        }
    }

    @GetMapping("/searchByLocation")
    public ResponseEntity<?> getAllAcceptedByLocation(
            @RequestParam(name = "lat", required = false) Double latitude,
            @RequestParam(name = "lng", required = false) Double longitude
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.findAllAcceptedByLocation(latitude, longitude));
        } catch (GeocodingException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("{\"error\": \"Error al obtener Proveedores por ubicación.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error Interno del Servidor.\"}");
        }
    }

    @GetMapping("/me/{userId}")
    @PreAuthorize("hasAuthority('USUARIO_REGULAR')")
    public ResponseEntity<?> getAllByUserId(@PathVariable Long userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.findAllByUserId(userId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Usuario no encontrado.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error Interno del Servidor.\"}");
        }
    }

    @GetMapping("/me/feedback/{userId}")
    @PreAuthorize("hasAuthority('USUARIO_REGULAR')")
    public ResponseEntity<?> getAllFeedbackByUserId(@PathVariable Long userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.findAllFeedbackByUserId(userId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Usuario no encontrado.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error Interno del Servidor.\"}");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Proveedor no encontrado.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error Interno del Servidor.\"}");
        }
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> getStatistics() {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.findStatistics());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error Interno del Servidor.\"}");
        }
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('USUARIO_REGULAR')")
    public ResponseEntity<?> save(
            @Valid @RequestPart(name = "supplier") SupplierRequestDTO dto,
            @RequestParam(name = "images") List<MultipartFile> images
    ) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(service.save(dto, images));
        } catch (MaxSuppliersReachedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("{\"error\": \"El Usuario ya tiene 3 Proveedores.\"}");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Las imágenes no son válidas.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Error al crear el Proveedor.\"}");
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('USUARIO_REGULAR')")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestPart(name = "supplier") SupplierRequestDTO dto,
            @RequestParam(name = "images") List<MultipartFile> images
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.update(id, dto, images));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Proveedor no encontrado.\"}");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Las imágenes no son válidas.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Error al actualizar el Proveedor.\"}");
        }
    }

    @PutMapping("/feedback/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> provideFeedback(@PathVariable Long id, @RequestBody SupplierFeedbackDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.provideFeedback(id, dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Proveedor no encontrado.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Error al proporcionar feedback al Proveedor.\"}");
        }
    }
}
