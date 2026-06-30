package br.com.postechfiap.toloni.restauranthub.domain.shared.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
