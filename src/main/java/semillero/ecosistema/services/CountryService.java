package semillero.ecosistema.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import semillero.ecosistema.entities.Country;
import semillero.ecosistema.repositories.CountryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    @Transactional
    public Country save(Country country) throws Exception{
        try {
            return countryRepository.save(country);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public Country update(Long id, Country country) throws Exception{
        try {
            Country countryById =
                    countryRepository.findById(id).orElseThrow(()->new Exception("Country with id " + id + "not found"));
                    countryById.setName(country.getName());
                    return countryRepository.save(countryById);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public void delete(Long id) throws Exception{
        try {
            Country countryById =
                    countryRepository.findById(id).orElseThrow(()->new Exception("Country with ID " + id + " not found"));
            countryRepository.delete(countryById);
        }catch (Exception e){
            throw new Exception("Error deleting country with ID " + id + ": " + e.getMessage());
        }
    }

    public List<Country> findAllCountries() throws Exception{
        try {
            return countryRepository.findAll();
        }catch (Exception e){
            throw new Exception("Error trying to retrieve all countries: " + e.getMessage());
        }
    }

    public Optional<Country> findById(Long id) throws Exception{
        try {
            return countryRepository.findById(id);
        }catch (Exception e){
            throw new Exception("Error trying to retrieve the country by ID: " + id + ". " + e.getMessage());
        }
    }
}
