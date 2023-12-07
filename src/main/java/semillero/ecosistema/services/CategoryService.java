package semillero.ecosistema.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import semillero.ecosistema.dtos.category.CategoryDTO;
import semillero.ecosistema.dtos.category.CategoryResponseDTO;
import semillero.ecosistema.entities.Category;
import semillero.ecosistema.entities.CategoryImage;
import semillero.ecosistema.mappers.CategoryMapper;
import semillero.ecosistema.repositories.CategoryImageRepository;
import semillero.ecosistema.repositories.CategoryRepository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryImageRepository categoryImageRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    private final CategoryMapper categoryMapper = CategoryMapper.getInstance();

    private final String CLOUDINARY_FOLDER = "categorias";

    /**
     * Obtiene todas las categorías.
     *
     * @return Una lista de DTOs que representan todas las categorías.
     * @throws Exception Si ocurre algún error durante el proceso de obtención.
     */
    public List<CategoryResponseDTO> findAll() throws Exception {
        try {
            List<Category> categories = categoryRepository.findAll();
            return categoryMapper.toResponseDTOsList(categories);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Obtiene una categoría por su ID.
     *
     * @param id Obtiene una categoría por su ID.
     * @return Un DTO que representa a la categoría.
     * @throws EntityNotFoundException Si no se encuentra la categoría con el ID especificado.
     * @throws Exception               Si ocurre un error durante el proceso de obtención.
     */
    public CategoryResponseDTO findById(Long id) throws Exception {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
            return categoryMapper.toResponseDTO(category);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Guarda una nueva categoría.
     *
     * @param dto   El DTO que contiene la información de la categoría a ser guardada.
     * @param image La imagen asociada a la categoría.
     * @return Un DTO que representa la categoría guardada.
     * @throws IOException Si ocurre un error de lectura o escritura de la imagen.
     * @throws Exception   Si ocurre algún otro error durante el proceso de guardado.
     */
    @Transactional
    public CategoryResponseDTO save(CategoryDTO dto, MultipartFile image) throws Exception {
        try {
            Category category = categoryMapper.toEntity(dto);

            CategoryImage categoryImage = uploadCategoryImage(image, category);
            category.setImage(categoryImage);

            return categoryMapper.toResponseDTO(categoryRepository.save(category));
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Actualiza una categoría existente.
     *
     * @param id    El ID de la categoría a ser actualizada.
     * @param dto   El DTO que contiene la información actualizada de la categoría.
     * @param image La nueva imagen asociada a la categoría.
     * @return Un DTO que representa la categoría actualizada.
     * @throws EntityNotFoundException Si no se encuentra la categoría con el ID especificado.
     * @throws IOException             Si ocurre un error de lectura o escritura de la nueva imagen.
     * @throws Exception               Si ocurre algún otro error durante el proceso de actualización.
     */
    @Transactional
    public CategoryResponseDTO update(Long id, CategoryDTO dto, MultipartFile image) throws Exception {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

            category.setName(dto.getName());

            // Eliminar imágen anterior y cargar nueva en Cloudinary. Actualizar relacion con CategoryImage
            cloudinaryService.deleteImage(category.getName(), CLOUDINARY_FOLDER);
            categoryImageRepository.delete(category.getImage());

            CategoryImage categoryImage = uploadCategoryImage(image, category);
            category.setImage(categoryImage);

            return categoryMapper.toResponseDTO(categoryRepository.save(category));
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Sube una imagen asociada a una categoría a Cloudinary.
     *
     * @param image    La imagen a ser subida.
     * @param category La categoría a la cual la imagen está asociada.
     * @return Una entidad CategoryImage que representa la imagen subida.
     * @throws IOException Si ocurre un error de lectura o escritura de la imagen.
     */
    private CategoryImage uploadCategoryImage(MultipartFile image, Category category) throws IOException {
        String name = UUID.randomUUID().toString();
        String path = cloudinaryService.uploadImage(image, name, CLOUDINARY_FOLDER);

        CategoryImage categoryImage = new CategoryImage();
        categoryImage.setName(name);
        categoryImage.setPath(path);
        categoryImage.setCategory(category);

        return categoryImage;
    }
}
