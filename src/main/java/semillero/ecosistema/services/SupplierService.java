package semillero.ecosistema.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import semillero.ecosistema.dtos.supplier.SupplierRequestDTO;
import semillero.ecosistema.entities.*;
import semillero.ecosistema.enumerations.SupplierStatus;
import semillero.ecosistema.mappers.SupplierMapper;
import semillero.ecosistema.repositories.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Transactional
    public Supplier save(SupplierRequestDTO dto) throws Exception {
        try {
            Supplier supplier = supplierMapper.toEntity(dto);

            // Establecer valores por defecto
            supplier.setDeleted(false);
            supplier.setStatus(SupplierStatus.REVISION_NICIAL);

            // Establecer relaciones
            Country country = countryRepository.findById(dto.getCountryId())
                    .orElseThrow(() -> new IllegalArgumentException("Country not found with id: " + dto.getCountryId()));
            Province province = provinceRepository.findById(dto.getProvinceId())
                    .orElseThrow(() -> new IllegalArgumentException("Province not found with id: " + dto.getProvinceId()));
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + dto.getCategoryId()));
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + dto.getUserId()));

            // Asignar relaciones a la entidad Supplier
            supplier.setCountry(country);
            supplier.setProvince(province);
            supplier.setCategory(category);
            supplier.setUser(user);

            return supplierRepository.save(supplier);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public Supplier update(Long id, SupplierRequestDTO dto) throws Exception {
        try {
            Supplier supplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Supplier not found with id: " + id));

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
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
