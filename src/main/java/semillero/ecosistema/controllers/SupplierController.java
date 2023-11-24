package semillero.ecosistema.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import semillero.ecosistema.dtos.supplier.SupplierRequestDTO;
import semillero.ecosistema.services.SupplierService;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService service;

    @PostMapping("")
    //@PreAuthorize("hasAuthority('USUARIO_REGULAR')")
    public ResponseEntity<?> save(@RequestBody SupplierRequestDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(service.save(dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Error al crear el Proveedor.\"}");
        }
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasAuthority('USUARIO_REGULAR')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SupplierRequestDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.update(id, dto));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Proveedor no encontrado.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Error al actualizar el Proveedor.\"}");
        }
    }
}
