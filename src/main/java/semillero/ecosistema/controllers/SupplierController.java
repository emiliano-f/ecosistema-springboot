package semillero.ecosistema.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import semillero.ecosistema.dtos.supplier.SupplierRequestDTO;
import semillero.ecosistema.exceptions.MaxSuppliersReachedException;
import semillero.ecosistema.services.SupplierService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService service;

    @GetMapping("/allAccepted")
    public ResponseEntity<?> getAllAccepted()  {
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
    public ResponseEntity<?> getAllAcceptedByCategory(@RequestParam(name = "category", required = true) String category)  {
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

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //@PreAuthorize("hasAuthority('USUARIO_REGULAR')")
    public ResponseEntity<?> save(
            @RequestPart(name = "supplier") SupplierRequestDTO dto,
            @RequestParam(name = "images") List<MultipartFile> images
    ) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(service.save(dto, images));
        } catch (MaxSuppliersReachedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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
    //@PreAuthorize("hasAuthority('USUARIO_REGULAR')")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestPart(name = "supplier") SupplierRequestDTO dto,
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
}
