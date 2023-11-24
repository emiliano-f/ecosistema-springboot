package semillero.ecosistema.mappers;

import org.springframework.stereotype.Component;
import semillero.ecosistema.dtos.supplier.SupplierRequestDTO;
import semillero.ecosistema.entities.Supplier;

@Component
public class SupplierMapper {

    public Supplier toEntity(SupplierRequestDTO source) throws Exception {
        try {
            Supplier supplier = new Supplier();

            supplier.setName(source.getName());
            supplier.setDescription(source.getDescription());
            supplier.setPhone(source.getPhone());
            supplier.setEmail(source.getEmail());
            supplier.setFacebook(source.getFacebook());
            supplier.setInstagram(source.getInstagram());
            supplier.setCity(source.getCity());

            return supplier;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
