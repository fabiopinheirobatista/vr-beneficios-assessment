package vr_backend_assessment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vr_backend_assessment.domain.Cartao;
import vr_backend_assessment.dto.CartaoRequest;
import vr_backend_assessment.exception.CartaoExistenteException;
import vr_backend_assessment.exception.CartaoNaoEncontradoException;
import vr_backend_assessment.repository.CartaoRepository;

import java.math.BigDecimal;

@Service
public class CartaoService {

    private final CartaoRepository repository;

    public CartaoService(CartaoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Cartao createCartao(CartaoRequest request) {
        repository.findById(request.getNumeroCartao()).ifPresent(c -> { throw new CartaoExistenteException(request); });
        Cartao cartao = new Cartao(request.getNumeroCartao(), request.getSenha());
        return repository.save(cartao);
    }

    @Transactional(readOnly = true)
    public BigDecimal obterSaldo(String numeroCartao) {
        return repository.findById(numeroCartao).map(Cartao::getSaldo).orElseThrow(() -> new CartaoNaoEncontradoException(numeroCartao));
    }
}

