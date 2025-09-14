package vr_backend_assessment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vr_backend_assessment.domain.Cartao;
import vr_backend_assessment.domain.Transacao;
import vr_backend_assessment.dto.TransacaoRequest;
import vr_backend_assessment.exception.CartaoInexistenteException;
import vr_backend_assessment.exception.SaldoInsuficienteException;
import vr_backend_assessment.exception.SenhaInvalidaException;
import vr_backend_assessment.repository.CartaoRepository;
import vr_backend_assessment.repository.TransacaoRepository;

@Service
public class TransacaoService {

    private final CartaoRepository cartaoRepository;
    private final TransacaoRepository transacaoRepository;

    public TransacaoService(CartaoRepository cartaoRepository, TransacaoRepository transacaoRepository) {
        this.cartaoRepository = cartaoRepository;
        this.transacaoRepository = transacaoRepository;
    }

    @Transactional
    public void processarTransacao(TransacaoRequest request) {
        // verify card exists or persist transaction with CARTAO_INEXISTENTE and throw
        Cartao cartao = cartaoRepository.findById(request.getNumeroCartao())
                .orElseGet(() -> {
                    Transacao t = new Transacao(request.getNumeroCartao(), request.getValor(), "CARTAO_INEXISTENTE");
                    transacaoRepository.save(t);
                    throw new CartaoInexistenteException(request.getNumeroCartao());
                });

        // validate password or persist and throw
        java.util.Optional.of(cartao)
                .filter(c -> c.getSenha().equals(request.getSenhaCartao()))
                .orElseGet(() -> {
                    Transacao t = new Transacao(request.getNumeroCartao(), request.getValor(), "SENHA_INVALIDA");
                    transacaoRepository.save(t);
                    throw new SenhaInvalidaException();
                });

        // attempt to debit using repository atomic update; if 0 rows affected -> insufficient
        int rows = cartaoRepository.debitSaldoIfHasValue(request.getNumeroCartao(), request.getValor());

        java.util.Optional.of(rows)
                .filter(r -> r > 0)
                .map(r -> {
                    Transacao t = new Transacao(request.getNumeroCartao(), request.getValor(), "OK");
                    transacaoRepository.save(t);
                    return t;
                })
                .orElseGet(() -> {
                    Transacao t = new Transacao(request.getNumeroCartao(), request.getValor(), "SALDO_INSUFICIENTE");
                    transacaoRepository.save(t);
                    throw new SaldoInsuficienteException();
                });
    }
}
