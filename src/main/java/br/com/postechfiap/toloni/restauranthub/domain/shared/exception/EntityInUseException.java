package br.com.postechfiap.toloni.restauranthub.domain.shared.exception;

public class EntityInUseException extends DomainException {
    public EntityInUseException(String entity, String id) {
        super(entity + " is in use and cannot be deleted: " + id);
    }
}
