package semillero.ecosistema.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import semillero.ecosistema.dtos.PublicationDTO;
import semillero.ecosistema.entities.Publication;
import semillero.ecosistema.entities.PublicationImage;
import semillero.ecosistema.entities.User;
import semillero.ecosistema.exceptions.PublicationNotFoundException;
import semillero.ecosistema.mappers.PublicationMapper;
import semillero.ecosistema.repositories.PublicationImageRepository;
import semillero.ecosistema.repositories.PublicationRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PublicationService {
    @Autowired
    private PublicationRepository publicationRepository;
    @Autowired
    private PublicationImageRepository publicationImageRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private PublicationMapper publicationMapper;
    private final String CLOUDINARY_FOLDER = "publicaciones";

    /**
     * Crea una nueva publicación con la información proporcionada en el DTO y las imágenes adjuntas.
     *
     * @param publicationDTO DTO que contiene la información de la publicación.
     * @param images         Lista de archivos de imágenes asociadas a la publicación.
     * @return La publicación creada en la base de datos.
     * @throws Exception Si ocurre un error durante la creación de la publicación.
     */
    @Transactional
    public Publication createPublication(PublicationDTO publicationDTO, List<MultipartFile> images) throws Exception {
        try {
//            Publication publication = PublicationMapper.INSTANCE.toEntity(publicationDTO);
            Publication publication = publicationMapper.toEntity(publicationDTO);

            publication.setVisualizationsAmount(0);

            List<PublicationImage> publicationImages = uploadPublicationImages(images, publication);
            publication.setImages(publicationImages);

            return publicationRepository.save(publication);
        } catch (Exception e) {
            throw new Exception("Error al crear la publicación", e);
        }
    }

    /**
     * Actualiza una publicación existente con la información proporcionada en el DTO y las imágenes actualizadas.
     *
     * @param id                    ID de la publicación a ser actualizada.
     * @param updatedPublicationDTO DTO que contiene la información actualizada de la publicación.
     * @param images                Lista de archivos de imágenes actualizadas asociadas a la publicación.
     * @return La publicación actualizada en la base de datos.
     * @throws Exception Si no se encuentra la publicación con el ID especificado o si ocurre un error durante la actualización.
     */
    @Transactional
    public Publication updatePublication(Long id, PublicationDTO updatedPublicationDTO, List<MultipartFile> images) throws Exception {
        try {
            Publication existingPublication = publicationRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Publication not found with id: " + id));

//            Publication updatedPublication = PublicationMapper.INSTANCE.updateEntity(updatedPublicationDTO, existingPublication);
            Publication updatedPublication = publicationMapper.updateEntity(updatedPublicationDTO, existingPublication);

            existingPublication.setTitle(updatedPublication.getTitle());
            existingPublication.setDescription(updatedPublication.getDescription());

            for (PublicationImage oldImage : existingPublication.getImages()) {
                cloudinaryService.deleteImage(oldImage.getName(), CLOUDINARY_FOLDER);
                publicationImageRepository.delete(oldImage);
            }

            List<PublicationImage> publicationImages = uploadPublicationImages(images, existingPublication);
            existingPublication.setImages(publicationImages);

            return publicationRepository.save(existingPublication);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Elimina una publicación con el ID especificado, incluyendo la eliminación de imágenes asociadas.
     *
     * @param id ID de la publicación a ser eliminada.
     * @throws Exception Si no se encuentra la publicación con el ID especificado o si ocurre un error durante la eliminación.
     */
    @Transactional
    public void deletePublication(Long id) throws Exception {
        try {
            Publication existingPublication = publicationRepository.findById(id)
                    .orElseThrow(() -> new PublicationNotFoundException("Publicación no encontrada con ID: " + id));

            List<PublicationImage> images = existingPublication.getImages();
            for (PublicationImage image : images) {
                String name = image.getName();
                String folder = "imagenes" + existingPublication.getUserCreator().getUsername();
                cloudinaryService.deleteImage(name, folder);
            }
            publicationRepository.delete(existingPublication);
        } catch (PublicationNotFoundException e) {
            throw new PublicationNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Carga las imágenes de la publicación en Cloudinary y crea entidades PublicationImage asociadas.
     *
     * @param images      Lista de archivos de imágenes a ser cargados.
     * @param publication La publicación a la que se asociarán las imágenes.
     * @return Lista de entidades PublicationImage creadas.
     * @throws IOException Si ocurre un error durante la manipulación de imágenes.
     */
    private List<PublicationImage> uploadPublicationImages(List<MultipartFile> images, Publication publication) throws IOException {
        List<PublicationImage> publicationImages = new ArrayList<>();
        for (MultipartFile image : images) {
            String name = UUID.randomUUID().toString();
            String path = cloudinaryService.uploadImage(image, name, CLOUDINARY_FOLDER);

            PublicationImage publicationImage = new PublicationImage();
            publicationImage.setName(name);
            publicationImage.setPath(path);
            publicationImage.setPublication(publication);

            publicationImages.add(publicationImage);
        }
        return publicationImages;
    }

    /**
     * Incrementa las visualizaciones de una publicación si el usuario autenticado no es el creador de la publicación.
     *
     * @param id ID de la publicación a ser visualizada.
     * @throws Exception Si no se encuentra la publicación con el ID especificado.
     */
    @Transactional
    public void incrementVisualizationsIfNeeded(Long id) throws Exception {
        UserDetails userDetails = getCurrentUserDetails();
        String currentUsername = userDetails.getUsername();

        Publication publication = getPublicationById(id);
        User creator = publication.getUserCreator();

        if (!currentUsername.equals(creator.getUsername())) {
            int currentViews = publication.getVisualizationsAmount();
            publication.setVisualizationsAmount(currentViews + 1);
            publicationRepository.save(publication);
        }
    }

    /**
     * Obtiene los detalles del usuario autenticado.
     *
     * @return Detalles del usuario autenticado.
     * @throws IllegalStateException Si el usuario no está autenticado.
     */
    private UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        throw new IllegalStateException("User not authenticated.");
    }

    /**
     * Obtiene una publicación por su ID.
     *
     * @param id ID de la publicación a ser obtenida.
     * @return La publicación encontrada.
     * @throws Exception Si no se encuentra la publicación con el ID especificado.
     */
    @Transactional
    public Publication getPublicationById(Long id) throws Exception {
        try {
            return publicationRepository.findById(id)
                    .orElseThrow(() -> new PublicationNotFoundException("Publicación no encontrada con ID: " + id));
        } catch (PublicationNotFoundException e) {
            throw new PublicationNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Obtiene todas las publicaciones activas.
     *
     * @return Lista de publicaciones activas.
     * @throws Exception Si ocurre un error durante la obtención.
     */
    @Transactional
    public List<Publication> getAllActivePublications() throws Exception {
        try {
            return publicationRepository.findAllByDeletedFalse();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Obtiene todas las publicaciones.
     *
     * @return Lista de todas las publicaciones.
     * @throws Exception Si ocurre un error durante la obtención.
     */
    @Transactional
    public List<Publication> getAllPublications() throws Exception {
        try {
            return publicationRepository.findAll();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}