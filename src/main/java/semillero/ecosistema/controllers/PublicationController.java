package semillero.ecosistema.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import semillero.ecosistema.dtos.PublicationDTO;
import semillero.ecosistema.entities.Publication;
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
            @Valid @RequestPart(name = "publication") PublicationDTO dto,
            @RequestParam(name = "images") List<MultipartFile> images
    ) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(publicationService.createPublication(dto, images));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Las imágenes no son válidas.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Error al crear la Publicación.\"}");
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('USUARIO_REGULAR')")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestPart(name = "supplier") PublicationDTO dto,
            @RequestParam(name = "images") List<MultipartFile> images
    ) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(publicationService.updatePublication(id, dto, images));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Las imágenes no son válidas.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Error al actualizar el Proveedor.\"}");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            publicationService.deletePublication(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (PublicationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Publicación no encontrada con ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ocurrió un error interno en el servidor."));
        }
    }
    @GetMapping("/all-active")
    public ResponseEntity<?> getAllActivePublications() {
        try {
            List<Publication> activePublications = publicationService.getAllActivePublications();
            return ResponseEntity.ok(activePublications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Ocurrió un error al procesar la respuesta.") {
                    });
        }
    }
    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> getAllPublications() {
        try {
            List<Publication> allPublications = publicationService.getAllPublications();
            return ResponseEntity.ok(allPublications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Ocurrió un error al procesar la respuesta."));
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getPublicationById(@PathVariable Long id) {
        try {
            Publication publication = publicationService.getPublicationById(id);
            return ResponseEntity.status(HttpStatus.OK).body(publication);
        } catch (PublicationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Publicación no encontrada con ID: " + id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}