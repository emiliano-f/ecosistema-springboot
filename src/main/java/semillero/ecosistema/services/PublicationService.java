package semillero.ecosistema.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import semillero.ecosistema.dtos.publication.PublicationDTO;
import semillero.ecosistema.dtos.publication.PublicationRequestDTO;
import semillero.ecosistema.dtos.publication.PublicationStatisticsDTO;
import semillero.ecosistema.entities.Publication;
import semillero.ecosistema.entities.PublicationImage;
import semillero.ecosistema.entities.User;
import semillero.ecosistema.exceptions.PublicationNotFoundException;
import semillero.ecosistema.mappers.PublicationMapper;
import semillero.ecosistema.repositories.PublicationImageRepository;
import semillero.ecosistema.repositories.PublicationRepository;
import semillero.ecosistema.repositories.UserRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PublicationService {
    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PublicationImageRepository publicationImageRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private PublicationMapper publicationMapper;
    private final String CLOUDINARY_FOLDER = "publicaciones";


    @Transactional
    public PublicationDTO save(PublicationRequestDTO publicationRequestDTO, List<MultipartFile> images) throws Exception {
        try {
            Publication publication = publicationMapper.toEntity(publicationRequestDTO);

            User user = userRepository.findById(publicationRequestDTO.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + publicationRequestDTO.getUserId()));

            List<PublicationImage> publicationImages = uploadPublicationImages(images, publication);

            publication.setTitle(publicationRequestDTO.getTitle());
            publication.setDescription(publicationRequestDTO.getDescription());
            publication.setDateOfCreation(LocalDate.now());
            publication.setDeleted(false);
            publication.setVisualizationsAmount(0);
            publication.setImages(publicationImages);
            publication.setUserCreator(user);
            publicationRepository.save(publication);

            return publicationMapper.toDTO(publication);

        } catch (Exception e) {
            throw new Exception("Error al crear la publicación", e);
        }
    }

    @Transactional
    public PublicationDTO update(Long id, PublicationRequestDTO publicationRequestDTO, List<MultipartFile> images) throws Exception {
        try {
            Publication publication = publicationRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Publicación no encontrada con el id: " + id));

            publication.setTitle(publicationRequestDTO.getTitle());
            publication.setDescription(publicationRequestDTO.getDescription());

            for (PublicationImage oldImage : publication.getImages()) {
                cloudinaryService.deleteImage(oldImage.getName(), CLOUDINARY_FOLDER);
                publicationImageRepository.delete(oldImage);
            }
            List<PublicationImage> publicationImages = uploadPublicationImages(images, publication);
            publication.setImages(publicationImages);

            publicationRepository.save(publication);

            return publicationMapper.toDTO(publication);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public void delete(Long id) throws Exception {
        try {
            Publication existingPublication = publicationRepository.findById(id)
                    .orElseThrow(() -> new PublicationNotFoundException("Publicación no encontrada con ID: " + id));

            List<PublicationImage> images = existingPublication.getImages();
            for (PublicationImage image : images) {
                String name = image.getName();
                String folder = "imágenes" + existingPublication.getUserCreator().getUsername();
                cloudinaryService.deleteImage(name, folder);
            }
            publicationRepository.delete(existingPublication);
        } catch (PublicationNotFoundException e) {
            throw new PublicationNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    @Transactional
    public void deleteToTrue(Long id) throws Exception {
        try {
            Publication existingPublication = publicationRepository.findById(id)
                    .orElseThrow(() -> new PublicationNotFoundException("Publicación no encontrada con ID: " + id));

            existingPublication.setDeleted(true);
            publicationRepository.save(existingPublication);
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
     * @throws IOException Es lanzada si ocurre un error durante la manipulación de imágenes.
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
     * Obtiene una publicación por su ID e incrementa la cantidad de visualizaciones.
     *
     * @param id Id de la publicación a ser obtenida.
     * @return La publicación encontrada.
     * @throws Exception Si no se encuentra la publicación con el ID especificado.
     */
    @Transactional
    public PublicationDTO getPublicationById(Long id) throws Exception {
        try {
            Publication publication = publicationRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Publication not found with id: " + id));

            publication.setVisualizationsAmount(publication.getVisualizationsAmount()+1);
            publicationRepository.save(publication);

            return publicationMapper.toDTO(publication);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    /**
     * Obtiene todas las publicaciones activas.
     *
     * @return Lista de publicaciones activas.
     * @throws Exception Lanzada si ocurre un error durante la obtención.
     */
    @Transactional
    public List<PublicationDTO> getAllActivePublications() throws Exception {
        try {
            List<Publication> publications = publicationRepository.findAllByDeletedFalse();
            return publicationMapper.toDTOsList(publications);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    /**
     * Obtiene todas las publicaciones.
     *
     * @return Lista de todas las publicaciones.
     * @throws Exception Es lanzada si ocurre un error durante la obtención.
     */
    @Transactional
    public List<PublicationDTO> getAllPublications() throws Exception {
        try {
            List<Publication> publications = publicationRepository.findAll();
            return publicationMapper.toDTOsList(publications);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Incrementa la cantidad de visualizaciones de la publicación según su id en 1.
     * @param id Recibe de la publicación su id.
     */
    @Transactional
    public void increment(Long id) throws Exception {
        try {
            Publication publication = publicationRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Publication not found with id: " + id));

            publication.setVisualizationsAmount(publication.getVisualizationsAmount()+1);
            publicationRepository.save(publication);

        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    public List<PublicationStatisticsDTO> findStatistics() throws Exception {
        try {
            List<Publication> publications = publicationRepository.findAll();

            return publications.stream().map(publicationMapper::toStatisticsDTO).collect(Collectors.toList());

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}