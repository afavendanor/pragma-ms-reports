package co.com.pragma.model.error;

public enum ResponseCode {
    MRPO000("Ocurrió un error inesperado, por favor intenta mas tarde."),
    MRPO001("Operación exitosa."),
    MRPO002("Campos no son validos."),
    MRPO003("La entidad a registrar ya existe en la app."),
    MRPO004("No se encontraron registros con los datos ingresados."),
    MRPO005("Identificación no es válida o no existe en el token.");

    private final String message;

    ResponseCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}