package semillero.ecosistema.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import semillero.ecosistema.entities.Category;
import semillero.ecosistema.repositories.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public Category save(Category category) throws Exception {
        try {
            return categoryRepository.save(category);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public Category update(Long id, Category category) throws Exception {
        try {
            Category categoryById =
                    categoryRepository.findById(id).orElseThrow(() -> new Exception("Category with id " + id + "not found"));
            categoryById.setName(category.getName());
            return categoryRepository.save(categoryById);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public void delete(Long id) throws Exception {
        try {
            Category categoryById =
                    categoryRepository.findById(id).orElseThrow(() -> new Exception("Category with ID " + id + " not found"));
            categoryRepository.delete(categoryById);
        } catch (Exception e) {
            throw new Exception("Error deleting category with ID " + id + ": " + e.getMessage());
        }
    }

    public List<Category> findAllCategories() throws Exception {
        try {
            return categoryRepository.findAll();
        } catch (Exception e) {
            throw new Exception("Error trying to retrieve all categories: " + e.getMessage());
        }
    }

    public Optional<Category> findById(Long id) throws Exception {
        try {
            return categoryRepository.findById(id);
        } catch (Exception e) {
            throw new Exception("Error trying to retrieve the category by ID: " + id + ". " + e.getMessage());
        }
    }
}
