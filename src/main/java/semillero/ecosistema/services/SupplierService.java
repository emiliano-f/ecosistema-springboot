package semillero.ecosistema.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import semillero.ecosistema.dtos.supplier.SupplierRequestDTO;
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
    private SupplierMapper supplierMapper;

    @Autowired
    private CloudinaryService cloudinaryService;

    private final String CLOUDINARY_FOLDER = "proveedores";

    /**
     * Obtiene una lista de proveedores que han sido aceptados y no han sido eliminados.
     * @return Lista de proveedores.
     * @throws Exception Si ocurre algún error durante el proceso de obtención.
     */
    public List<Supplier> findAllAccepted() throws Exception {
        try {
            return supplierRepository.findAllByStatusAndDeleted(SupplierStatus.ACEPTADO, false);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Obtiene una lista de proveedores aceptados y no han sido eliminados que contienen el nombre especificado
     * (ignorando mayúsculas y minúsculas).
     * @param name El nombre a ser buscado.
     * @return Lista de proveedores.
     * @throws IllegalArgumentException Si el nombre proporcionado es nulo o vacío.
     * @throws EntityNotFoundException Si no se encuentra ningún proveedor aceptado con el nombre especificado.
     * @throws Exception Si ocurre algún otro error durante la búsqueda.
     */
    public List<Supplier> findAllAcceptedByName(String name) throws Exception {
        try {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("The query cannot be empty.");
            }

            List<Supplier> suppliers = supplierRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeleted(name, SupplierStatus.ACEPTADO, false);

            if (suppliers.isEmpty()) {
                throw new EntityNotFoundException("Supplier not found with name: " + name);
            }

            return suppliers;
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
     * @param categoryName El nombre de la categoría a ser buscada.
     * @return Lista de proveedores.
     * @throws IllegalArgumentException Si el nombre de la categoría proporcionado es nulo o vacío.
     * @throws EntityNotFoundException Si no se encuentra la categoría con el nombre especificado
     * o no hay proveedores aceptados en esa categoría.
     * @throws Exception Si ocurre algún otro error durante la búsqueda.
     */
    public List<Supplier> findAllAcceptedByCategory(String categoryName) throws Exception {
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

            return suppliers;
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
     * @param dto El DTO que contiene la información del proveedor.
     * @param images Lista de archivos de imágenes asociadas al proveedor.
     * @return El proveedor guardado en la base de datos.
     * @throws MaxSuppliersReachedException Si el usuario ya tiene el máximo permitido de proveedores (3).
     * @throws IOException Si ocurre un error durante la manipulación de imágenes.
     * @throws Exception Si ocurre algún otro error durante el proceso de guardado.
     */
    @Transactional
    public Supplier save(SupplierRequestDTO dto, List<MultipartFile> images) throws Exception {
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
            supplier.setStatus(SupplierStatus.REVISION_NICIAL);

            return supplierRepository.save(supplier);
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
     * @param id El ID del proveedor a ser actualizado.
     * @param dto El DTO que contiene la información actualizada del proveedor.
     * @param images Lista de archivos de imágenes actualizadas asociadas al proveedor.
     * @return El proveedor actualizado en la base de datos.
     * @throws EntityNotFoundException Si no se encuentra el proveedor con el ID especificado.
     * @throws IOException Si ocurre un error durante la manipulación de imágenes.
     * @throws Exception Si ocurre algún otro error durante el proceso de actualización.
     */
    @Transactional
    public Supplier update(Long id, SupplierRequestDTO dto, List<MultipartFile> images) throws Exception {
        try {
            Supplier supplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + id));

            // Actualizar estado
            supplier.setStatus(SupplierStatus.REVISION_NICIAL);

            // Actualizar los campos del proveedor con la información proporcionada en el DTO
            supplier.setName(dto.getName());
            supplier.setDescription(dto.getDescription());
            supplier.setPhone(dto.getPhone());
            supplier.setEmail(dto.getEmail());
            supplier.setFacebook(dto.getFacebook());
            supplier.setInstagram(dto.getInstagram());
            supplier.setCity(dto.getCity());

            // Actualizar relaciones
            Country country = countryRepository.findById(dto.getCountryId())
                    .orElseThrow(() -> new IllegalArgumentException("Country not found with id: " + dto.getCountryId()));
            Province province = provinceRepository.findById(dto.getProvinceId())
                    .orElseThrow(() -> new IllegalArgumentException("Province not found with id: " + dto.getProvinceId()));
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + dto.getCategoryId()));
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + dto.getUserId()));

            // Eliminar imágenes anteriores y cargar nuevas en Cloudinary. Actualizar relacion con SupplierImage
            for (SupplierImage oldImage : supplier.getImages()) {
                cloudinaryService.deleteImage(oldImage.getName(), CLOUDINARY_FOLDER);
                supplierImageRepository.delete(oldImage);
            }
            List<SupplierImage> supplierImages = uploadSupplierImages(images, supplier);
            supplier.setImages(supplierImages);

            return supplierRepository.save(supplier);
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
     * @param images Lista de archivos de imágenes a ser cargados.
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
}
