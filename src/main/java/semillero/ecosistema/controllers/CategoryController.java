package semillero.ecosistema.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semillero.ecosistema.entities.Category;
import semillero.ecosistema.services.CategoryService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("")
    public ResponseEntity<?> saveCategory(@Valid @RequestBody Category category) {
        try {
            Category savedCategory = categoryService.save(category);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedCategory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"An error occurred while processing the request.\"}");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@Valid @RequestBody Category category, @PathVariable Long id) {
        try {
            Category updateCategory = categoryService.update(id, category);
            return ResponseEntity.ok(updateCategory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"An error occurred while processing the request.\"}");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An error occurred while processing the request.\"}");
        }
    }

    @GetMapping("")
    public ResponseEntity<?> findAllCategories() {
        try {
            List<Category> categories = categoryService.findAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An error occurred while processing the request.\"}");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findCategoryById(@PathVariable Long id){
        try {
            Optional<Category> categoryById = categoryService.findById(id);
            if (categoryById.isPresent()) {
                return ResponseEntity.ok(categoryById.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
