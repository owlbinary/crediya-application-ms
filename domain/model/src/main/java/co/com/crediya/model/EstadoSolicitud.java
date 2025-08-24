package co.com.crediya.model;

public enum EstadoSolicitud {
    PENDIENTE_REVISION("Solicitud pendiente de revisión"),
    APROBADO("Solicitud aprobada"),
    RECHAZADO("Solicitud rechazada"),
    REVISION_MANUAL("Solicitud en revisión manual");
    
    private final String descripcion;
    
    EstadoSolicitud(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}