package co.com.pragma.api.dto;
import co.com.pragma.model.error.FieldError;
import co.com.pragma.model.error.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@Schema(description = "Modelo de salida generica")
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class GenericResponseDTO<T> {

    @Schema(description = "Código interno de la respuesta")
    private final Integer responseCode;

    @Schema(description = "Mensaje adicional de la respuesta")
    private final String responseMessage;

    private final T data;

    @Schema(description = "Listado de errores de validación de campos")
    private final List<FieldError> fieldErrors;

    public GenericResponseDTO(HttpStatus status, ResponseCode responseCode, T data) {
        this.responseCode = status.value();
        this.responseMessage = responseCode.getMessage();
        this.data = data;
        this.fieldErrors = new ArrayList<>();
    }
}