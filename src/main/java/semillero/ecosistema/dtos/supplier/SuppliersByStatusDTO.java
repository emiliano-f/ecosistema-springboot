package semillero.ecosistema.dtos.supplier;

import lombok.Data;

import java.util.List;

@Data
public class SuppliersByStatusDTO {
    private List<SupplierNameAndCategoryDTO> newSuppliers;
    private List<SupplierNameAndCategoryDTO> approvedSuppliers;
    private List<SupplierNameAndCategoryDTO> reviewSuppliers;
    private List<SupplierNameAndCategoryDTO> deniedSuppliers;
}
