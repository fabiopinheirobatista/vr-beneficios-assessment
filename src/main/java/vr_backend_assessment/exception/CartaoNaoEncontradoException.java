 package vr_backend_assessment.exception;

public class CartaoNaoEncontradoException extends RuntimeException {
    private final String numero;

    public CartaoNaoEncontradoException(String numero) {
        super("Cartao nao encontrado");
        this.numero = numero;
    }

    public String getNumero() {
        return numero;
    }
}

