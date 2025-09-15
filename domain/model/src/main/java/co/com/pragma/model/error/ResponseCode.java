package co.com.pragma.model.error;

public enum ResponseCode {
    MSSO000("Ocurrió un error inesperado, por favor intenta mas tarde."),
    MSSO001("Operación exitosa."),
    MSSO002("Campos no son validos."),
    MSSO003("La entidad a registrar ya existe en la app."),
    MSSO004("No se encontraron registros con los datos ingresados."),
    MSSO005("Identificación no es válida o no existe en el token."),
    MSSO006("Solo puede crear sus propias solicitudes."),
    MSSO007("Error obteniendo información del token."),
    MSSO008("Error obteniendo lista de solicitudes."),
    MSSO009("Error obteniendo los usuarios del servicio de user."),
    MSSO010("Error estado no està parametrizado.");

    private final String message;

    ResponseCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}