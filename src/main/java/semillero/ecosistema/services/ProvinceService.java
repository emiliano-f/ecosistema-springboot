package semillero.ecosistema.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import semillero.ecosistema.entities.Province;
import semillero.ecosistema.repositories.ProvinceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProvinceService {
    @Autowired
    private ProvinceRepository provinceRepository;

    @Transactional
    public Province save(Province province) throws Exception{
        try {
            return provinceRepository.save(province);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public Province update(Long id, Province province) throws Exception{
        try {
            Province provinceById =
                    provinceRepository.findById(id).orElseThrow(()->new Exception("Province with id " + id + "not found"));
            provinceById.setName(province.getName());
            return provinceRepository.save(provinceById);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public void delete(Long id) throws Exception{
        try {
            Province provinceById =
                    provinceRepository.findById(id).orElseThrow(()->new Exception("Province with ID " + id + " not found"));
            provinceRepository.delete(provinceById);
        }catch (Exception e){
            throw new Exception("Error deleting province with ID " + id + ": " + e.getMessage());
        }
    }

    public List<Province> findAllProvinces() throws Exception{
        try {
            return provinceRepository.findAll();
        }catch (Exception e){
            throw new Exception("Error trying to retrieve all provinces: " + e.getMessage());
        }
    }
    public List<Province> getProvincesByCountry(Long countryId) throws Exception {
        try{
            return provinceRepository.findByCountryId(countryId);
        }catch (Exception e){
            throw new Exception("Error trying to retrieve all provinces by country: " + e.getMessage());
        }
    }

    public Optional<Province> findById(Long id) throws Exception{
        try {
            return provinceRepository.findById(id);
        }catch (Exception e){
            throw new Exception("Error trying to retrieve the province by ID: " + id + ". " + e.getMessage());
        }
    }
}
