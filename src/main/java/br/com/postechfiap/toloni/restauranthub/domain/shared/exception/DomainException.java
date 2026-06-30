package br.com.postechfiap.toloni.restauranthub.domain.shared.exception;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
