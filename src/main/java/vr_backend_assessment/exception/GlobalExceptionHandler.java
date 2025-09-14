package vr_backend_assessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CartaoExistenteException.class)
    public ResponseEntity<Object> handleCartaoExistente(CartaoExistenteException ex) {
        return ResponseEntity.unprocessableEntity().body(ex.getRequest());
    }

    @ExceptionHandler(CartaoNaoEncontradoException.class)
    public ResponseEntity<Object> handleCartaoNaoEncontrado(CartaoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler({SenhaInvalidaException.class, SaldoInsuficienteException.class, CartaoInexistenteException.class})
    public ResponseEntity<String> handleUnprocessable(RuntimeException ex) {
        return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }
}

