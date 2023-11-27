package semillero.ecosistema.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import semillero.ecosistema.dtos.supplier.SupplierRequestDTO;
import semillero.ecosistema.entities.*;
import semillero.ecosistema.enumerations.SupplierStatus;
import semillero.ecosistema.exceptions.MaxSuppliersReachedException;
import semillero.ecosistema.mappers.SupplierMapper;
import semillero.ecosistema.repositories.*;

import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

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

    public List<Supplier> findAll() throws Exception {
        try {
            return supplierRepository.findAll();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<Supplier> findAllAccepted() throws Exception {
        try {
            return supplierRepository.findAllByStatusAndDeleted(SupplierStatus.ACEPTADO, false);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<Supplier> findAllByName(String name) throws Exception {
        try {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("The query cannot be empty.");
            }

            List<Supplier> suppliers = supplierRepository.findAllByNameContainingIgnoreCase(name);

            if (suppliers.isEmpty()) {
                throw new EntityNotFoundException("Supplier not found with name: " + name);
            }

            return suppliers;
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

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
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public Supplier save(SupplierRequestDTO dto) throws Exception {
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

            // Establecer valores por defecto
            supplier.setDeleted(false);
            supplier.setStatus(SupplierStatus.REVISION_NICIAL);

            return supplierRepository.save(supplier);
        } catch (MaxSuppliersReachedException e) {
            throw new MaxSuppliersReachedException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public Supplier update(Long id, SupplierRequestDTO dto) throws Exception {
        try {
            Supplier supplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + id));

            // Actualizar estado
            supplier.setStatus(SupplierStatus.REVISION_NICIAL);

            //  Actualizar los campos del proveedor con la información proporcionada en el DTO
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

            return supplierRepository.save(supplier);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
