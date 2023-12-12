package semillero.ecosistema.dtos.supplier;

import semillero.ecosistema.enumerations.SupplierStatus;

public record SupplierStatusCount(SupplierStatus status, long count) {
}
