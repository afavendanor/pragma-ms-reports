package co.com.pragma.model.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public class FieldError implements Serializable {

    @Serial
    private static final long serialVersionUID = 3708567824775716466L;

    private final String field;
    private final String error;
}