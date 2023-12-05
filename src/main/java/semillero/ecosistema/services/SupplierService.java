package semillero.ecosistema.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import semillero.ecosistema.dtos.supplier.*;
import semillero.ecosistema.entities.*;
import semillero.ecosistema.enumerations.SupplierStatus;
import semillero.ecosistema.exceptions.MaxSuppliersReachedException;
import semillero.ecosistema.mappers.SupplierMapper;
import semillero.ecosistema.repositories.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierImageRepository supplierImageRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    private final String CLOUDINARY_FOLDER = "proveedores";

    private final SupplierMapper supplierMapper = SupplierMapper.getInstance();

    /**
     * Obtiene proveedores clasificados por su estado.
     *
     * @return Un DTO que contiene listas de proveedores clasificados por estado.
     * @throws Exception Si ocurre algún error durante el proceso de obtención.
     */
    public SuppliersByStatusDTO findAll() throws Exception {
        try {
            List<Supplier> newSuppliers = supplierRepository.findAllByStatus(SupplierStatus.REVISION_INICIAL);
            List<Supplier> reviewSuppliers = supplierRepository.findAllByStatus(SupplierStatus.REQUIERE_CAMBIOS);
            List<Supplier> approvedSuppliers = supplierRepository.findAllByStatus(SupplierStatus.ACEPTADO);
            List<Supplier> deniedSuppliers = supplierRepository.findAllByStatus(SupplierStatus.DENEGADO);

            SuppliersByStatusDTO suppliers = new SuppliersByStatusDTO();
            suppliers.setNewSuppliers(supplierMapper.toNameAndCategoryDTOsList(newSuppliers));
            suppliers.setReviewSuppliers(supplierMapper.toNameAndCategoryDTOsList(reviewSuppliers));
            suppliers.setApprovedSuppliers(supplierMapper.toNameAndCategoryDTOsList(approvedSuppliers));
            suppliers.setDeniedSuppliers(supplierMapper.toNameAndCategoryDTOsList(deniedSuppliers));

            return suppliers;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Obtiene el estado y comentarios de proveedores asociados a un usuario específico.
     *
     * @param userId El ID del usuario para el cual se obtendrán los proveedores.
     * @return Una lista de DTOs que representan el estado y comentarios de proveedores asociados al usuario.
     * @throws EntityNotFoundException Si no se encuentra el usuario con el ID especificado.
     * @throws Exception Si ocurre algún error durante el proceso de obtención.
     */
    public List<SupplierFeedbackDTO> findAllByUserId(Long userId) throws Exception {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

            List<Supplier> suppliers = supplierRepository.findAllByUser(user);
            return supplierMapper.toFeedbackDTOsList(suppliers);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Obtiene una lista de nombres de proveedores que han sido aceptados y no han sido eliminados.
     *
     * @return Lista de proveedores.
     * @throws Exception Si ocurre algún error durante el proceso de obtención.
     */
    public List<SupplierNameDTO> findAllAcceptedNames() throws Exception {
        try {
            List<Supplier> suppliers = supplierRepository.findAllByStatusAndDeleted(SupplierStatus.ACEPTADO, false);
            return supplierMapper.toNameDTOsList(suppliers);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Obtiene una lista de proveedores que han sido aceptados y no han sido eliminados.
     *
     * @return Lista de proveedores.
     * @throws Exception Si ocurre algún error durante el proceso de obtención.
     */
    public List<SupplierDTO> findAllAccepted() throws Exception {
        try {
            List<Supplier> suppliers = supplierRepository.findAllByStatusAndDeleted(SupplierStatus.ACEPTADO, false);
            return supplierMapper.toDTOsList(suppliers);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Obtiene una lista de proveedores aceptados y no han sido eliminados que contienen el nombre especificado
     * (ignorando mayúsculas y minúsculas).
     *
     * @param name El nombre a ser buscado.
     * @return Lista de proveedores.
     * @throws IllegalArgumentException Si el nombre proporcionado es nulo o vacío.
     * @throws EntityNotFoundException  Si no se encuentra ningún proveedor aceptado con el nombre especificado.
     * @throws Exception                Si ocurre algún otro error durante la búsqueda.
     */
    public List<SupplierDTO> findAllAcceptedByName(String name) throws Exception {
        try {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("The query cannot be empty.");
            }

            List<Supplier> suppliers = supplierRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeleted(name, SupplierStatus.ACEPTADO, false);

            if (suppliers.isEmpty()) {
                throw new EntityNotFoundException("Supplier not found with name: " + name);
            }

            return supplierMapper.toDTOsList(suppliers);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Obtiene una lista de proveedores aceptados y no han sido eliminados que pertenecen a una categoría especifica
     * (ignorando mayúsculas y minúsculas).
     *
     * @param categoryName El nombre de la categoría a ser buscada.
     * @return Lista de proveedores.
     * @throws IllegalArgumentException Si el nombre de la categoría proporcionado es nulo o vacío.
     * @throws EntityNotFoundException  Si no se encuentra la categoría con el nombre especificado
     *                                  o no hay proveedores aceptados en esa categoría.
     * @throws Exception                Si ocurre algún otro error durante la búsqueda.
     */
    public List<SupplierDTO> findAllAcceptedByCategory(String categoryName) throws Exception {
        try {
            Category category = categoryRepository.findByNameContainingIgnoreCase(categoryName);

            if (category == null) {
                throw new IllegalArgumentException("Category not found with name: " + categoryName);
            }

            List<Supplier> suppliers = supplierRepository
                    .findAllByCategoryAndStatusAndDeleted(category, SupplierStatus.ACEPTADO, false);

            if (suppliers.isEmpty()) {
                throw new EntityNotFoundException("Supplier not found with category: " + categoryName);
            }

            return supplierMapper.toDTOsList(suppliers);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Guarda un nuevo proveedor en la base de datos utilizando la información proporcionada en el DTO y las imágenes.
     *
     * @param dto    El DTO que contiene la información del proveedor.
     * @param images Lista de archivos de imágenes asociadas al proveedor.
     * @return El proveedor guardado en la base de datos.
     * @throws MaxSuppliersReachedException Si el usuario ya tiene el máximo permitido de proveedores (3).
     * @throws IOException                  Si ocurre un error durante la manipulación de imágenes.
     * @throws Exception                    Si ocurre algún otro error durante el proceso de guardado.
     */
    @Transactional
    public SupplierDTO save(SupplierRequestDTO dto, List<MultipartFile> images) throws Exception {
        try {
            Supplier supplier = supplierMapper.toEntity(dto);

            // Validar que el Usuario no tenga más de 3 Proveedores
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + dto.getUserId()));
            Long numberOfSuppliers = supplierRepository.countByUser(user);
            if (numberOfSuppliers >= 3) {
                throw new MaxSuppliersReachedException("The user already has the maximum number of providers allowed (3)");
            }

            // Establecer relaciones
            Country country = countryRepository.findById(dto.getCountryId())
                    .orElseThrow(() -> new IllegalArgumentException("Country not found with id: " + dto.getCountryId()));
            Province province = provinceRepository.findById(dto.getProvinceId())
                    .orElseThrow(() -> new IllegalArgumentException("Province not found with id: " + dto.getProvinceId()));
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + dto.getCategoryId()));

            // Asignar relaciones a la entidad Supplier
            supplier.setCountry(country);
            supplier.setProvince(province);
            supplier.setCategory(category);
            supplier.setUser(user);

            // Guardar imágenes en Cloudinary. Establecer relacion con SupplierImage
            List<SupplierImage> supplierImages = uploadSupplierImages(images, supplier);
            supplier.setImages(supplierImages);

            // Establecer valores por defecto
            supplier.setDeleted(false);
            supplier.setStatus(SupplierStatus.REVISION_INICIAL);
            supplier.setFeedback("Revisión inicial");

            return supplierMapper.toDTO(supplierRepository.save(supplier));
        } catch (MaxSuppliersReachedException e) {
            throw new MaxSuppliersReachedException(e.getMessage());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Actualiza un proveedor existente en la base de datos con la información proporcionada en el DTO y las imágenes.
     *
     * @param id     El ID del proveedor a ser actualizado.
     * @param dto    El DTO que contiene la información actualizada del proveedor.
     * @param images Lista de archivos de imágenes actualizadas asociadas al proveedor.
     * @return El proveedor actualizado en la base de datos.
     * @throws EntityNotFoundException Si no se encuentra el proveedor con el ID especificado.
     * @throws IOException             Si ocurre un error durante la manipulación de imágenes.
     * @throws Exception               Si ocurre algún otro error durante el proceso de actualización.
     */
    @Transactional
    public SupplierDTO update(Long id, SupplierRequestDTO dto, List<MultipartFile> images) throws Exception {
        try {
            Supplier supplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + id));

            // Actualizar estado
            supplier.setStatus(SupplierStatus.REVISION_INICIAL);
            supplier.setFeedback("Revisión inicial");

            // Actualizar los campos del proveedor con la información proporcionada en el DTO
            supplier.setName(dto.getName());
            supplier.setDescription(dto.getDescription());
            supplier.setShortDescription(dto.getShortDescription());
            supplier.setPhone(dto.getPhone());
            supplier.setEmail(dto.getEmail());
            supplier.setFacebook(dto.getFacebook());
            supplier.setInstagram(dto.getInstagram());
            supplier.setCity(dto.getCity());

            // Establecer relaciones
            Country country = countryRepository.findById(dto.getCountryId())
                    .orElseThrow(() -> new IllegalArgumentException("Country not found with id: " + dto.getCountryId()));
            Province province = provinceRepository.findById(dto.getProvinceId())
                    .orElseThrow(() -> new IllegalArgumentException("Province not found with id: " + dto.getProvinceId()));
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + dto.getCategoryId()));
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + dto.getUserId()));

            // Actualizar relaciones a la entidad Supplier
            supplier.setCountry(country);
            supplier.setProvince(province);
            supplier.setCategory(category);
            supplier.setUser(user);

            // Eliminar imágenes anteriores y cargar nuevas en Cloudinary. Actualizar relacion con SupplierImage
            for (SupplierImage oldImage : supplier.getImages()) {
                supplierImageRepository.delete(oldImage);
                cloudinaryService.deleteImage(oldImage.getName(), CLOUDINARY_FOLDER);
            }
            List<SupplierImage> supplierImages = uploadSupplierImages(images, supplier);
            supplier.setImages(supplierImages);

            return supplierMapper.toDTO(supplierRepository.save(supplier));
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Carga las imágenes del proveedor en Cloudinary y crea entidades SupplierImage asociadas.
     *
     * @param images   Lista de archivos de imágenes a ser cargados.
     * @param supplier El proveedor al que se asociarán las imágenes.
     * @return Lista de entidades SupplierImage creadas.
     * @throws IOException Si ocurre un error durante la manipulación de imágenes.
     */
    private List<SupplierImage> uploadSupplierImages(List<MultipartFile> images, Supplier supplier) throws IOException {
        List<SupplierImage> supplierImages = new ArrayList<>();

        for (MultipartFile image : images) {
            String name = UUID.randomUUID().toString();
            String path = cloudinaryService.uploadImage(image, name, CLOUDINARY_FOLDER);

            SupplierImage supplierImage = new SupplierImage();
            supplierImage.setName(name);
            supplierImage.setPath(path);
            supplierImage.setSupplier(supplier);

            supplierImages.add(supplierImage);
        }

        return supplierImages;
    }

    /**
     * Proporciona comentarios a un proveedor y actualiza su estado.
     *
     * @param id  El ID del proveedor al que se le proporcionarán comentarios.
     * @param dto El DTO que contiene la información de los comentarios y estado a ser proporcionados.
     * @return El proveedor actualizado en la base de datos.
     * @throws EntityNotFoundException Si no se encuentra el proveedor con el ID especificado.
     * @throws Exception               Si ocurre un error durante la actualización del proveedor.
     */
    @Transactional
    public SupplierDTO provideFeedback(Long id, SupplierFeedbackDTO dto) throws Exception {
        try {
            Supplier supplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + id));

            supplier.setStatus(dto.getStatus());
            supplier.setFeedback(dto.getFeedback());

            return supplierMapper.toDTO(supplierRepository.save(supplier));
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
