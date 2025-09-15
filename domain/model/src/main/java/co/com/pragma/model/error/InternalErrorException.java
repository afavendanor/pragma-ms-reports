package co.com.pragma.model.error;


import java.io.Serial;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Excepción personalizada
 */
public class InternalErrorException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3508567824775716466L;

    private final List<FieldError> fieldErrors;

    public InternalErrorException(ResponseCode responseCode, String... params) {
        super(MessageFormat.format(responseCode.getMessage(), (Object[]) params));
        this.fieldErrors = new ArrayList<>();
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

}
