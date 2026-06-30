package br.com.postechfiap.toloni.restauranthub.domain.shared.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String entityName, Object id) {
        super(entityName + " not found with id: " + id);
    }
}
