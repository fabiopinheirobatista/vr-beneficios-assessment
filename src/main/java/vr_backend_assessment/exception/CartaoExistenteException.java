// new file
package vr_backend_assessment.exception;

import vr_backend_assessment.dto.CartaoRequest;

public class CartaoExistenteException extends RuntimeException {
    private final CartaoRequest request;

    public CartaoExistenteException(CartaoRequest request) {
        super("Cartao ja existe");
        this.request = request;
    }

    public CartaoRequest getRequest() {
        return request;
    }
}

