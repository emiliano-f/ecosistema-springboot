package semillero.ecosistema.enumerations;

public enum SupplierStatus {
    REVISION_NICIAL,        // Cuando se crea un proveedor
    ACEPTADO,               // Cuando el Administrador ha aprobado el proveedor
    DENEGADO,               // Cuando el Administrador ha rechazado el proveedor
    REQUIERE_CAMBIOS,       // Cuando el Administrador solicita modificaciones
    CAMBIOS_REALIZADOS      // Cuando el Administrador ha editado el proveedor después de recibir feedback
}
