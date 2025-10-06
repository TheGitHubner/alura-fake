package br.com.alura.AluraFake.exception;

public class FieldValidationException extends RuntimeException {
    private final String field;

    public FieldValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
