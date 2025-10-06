package br.com.alura.AluraFake.exception;

public class NotFoundException extends RuntimeException {
    private final String field;

    public NotFoundException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() { return field; }
}
