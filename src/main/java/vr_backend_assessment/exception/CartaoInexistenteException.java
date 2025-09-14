package vr_backend_assessment.exception;

public class CartaoInexistenteException extends RuntimeException {
    public CartaoInexistenteException(String numero) {
        super("CARTAO_INEXISTENTE");
    }
}

