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
import semillero.ecosistema.dtos.publication.PublicationRequestDTO;
import semillero.ecosistema.exceptions.PublicationNotFoundException;

import semillero.ecosistema.services.PublicationService;
import semillero.ecosistema.responses.ErrorResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/publication")
public class PublicationController {

    @Autowired
    private PublicationService publicationService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> save(
            @Valid @RequestPart(name = "publication") PublicationRequestDTO dto,
            @RequestParam(name = "images") List<MultipartFile> images
    ) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(publicationService.save(dto, images));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Las imágenes no son válidas.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Error al crear el Proveedor.\"}");
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestPart(name = "publication") PublicationRequestDTO dto,
            @RequestParam(name = "images") List<MultipartFile> images
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(publicationService.update(id, dto, images));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Publicación no encontrada.\"}");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Las imágenes no son válidas.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Error al actualizar la publicación.\"}");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            publicationService.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("La publicación ha sido borrada exitosamente.");
        } catch (PublicationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Publicación no encontrada con ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ocurrió un error interno en el servidor."));
        }
    }

    @PutMapping("/setDeleted/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> deleteToTrue(@PathVariable Long id) {
        try {
            publicationService.deleteToTrue(id);
            return ResponseEntity.status(HttpStatus.OK).body("El estado de la publicación se ha cambiado con éxito.");
        } catch (PublicationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Publicación no encontrada con ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ocurrió un error interno en el servidor."));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> getAllPublications() {
        try {
            return ResponseEntity.ok(publicationService.getAllPublications());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Ocurrió un error al procesar la respuesta."));
        }
    }
    @GetMapping("/all-active")
    @PreAuthorize("hasAuthority('USUARIO_REGULAR')")
    public ResponseEntity<?> getAllActivePublications() {
        try {
            return ResponseEntity.ok(publicationService.getAllActivePublications());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ocurrió un error al procesar la respuesta.") {
                    });
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_REGULAR')")
    public ResponseEntity<?> getPublicationById(@PathVariable Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(publicationService.getPublicationById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Publicación no encontrada con ID: " + id));
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/increment/{id}")
    @PreAuthorize("hasAuthority('USUARIO_REGULAR')")
    public ResponseEntity<?> incrementById(@PathVariable Long id) {
        try {
            publicationService.increment(id);
            return ResponseEntity.status(HttpStatus.OK).body("Visualizaciones actualizadas con éxito");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Publicación no encontrada con ID: " + id));
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}