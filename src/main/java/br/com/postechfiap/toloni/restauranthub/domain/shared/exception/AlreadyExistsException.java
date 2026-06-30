package br.com.postechfiap.toloni.restauranthub.domain.shared.exception;

public class AlreadyExistsException extends DomainException {
    public AlreadyExistsException(String field, String value) {
        super(field + " already exists: " + value);
    }
}
