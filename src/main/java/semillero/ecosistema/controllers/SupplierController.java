package semillero.ecosistema.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import semillero.ecosistema.dtos.supplier.SupplierRequestDTO;
import semillero.ecosistema.exceptions.MaxSuppliersReachedException;
import semillero.ecosistema.services.SupplierService;

@RestController
@RequestMapping("api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService service;

    @GetMapping("/searchByName")
    public ResponseEntity<?> getAllByName(@RequestParam(name = "name", required = true) String name) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.findAllByName(name));
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

    @PostMapping("")
    //@PreAuthorize("hasAuthority('USUARIO_REGULAR')")
    public ResponseEntity<?> save(@RequestBody SupplierRequestDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(service.save(dto));
        } catch (MaxSuppliersReachedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"El Usuario ya tiene 3 Proveedores.\"}");
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
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Proveedor no encontrado.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Error al actualizar el Proveedor.\"}");
        }
    }
}
