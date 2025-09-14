package vr_backend_assessment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vr_backend_assessment.domain.Cartao;
import vr_backend_assessment.domain.Transacao;
import vr_backend_assessment.dto.TransacaoRequest;
import vr_backend_assessment.exception.CartaoInexistenteException;
import vr_backend_assessment.exception.SaldoInsuficienteException;
import vr_backend_assessment.exception.SenhaInvalidaException;
import vr_backend_assessment.repository.CartaoRepository;
import vr_backend_assessment.repository.TransacaoRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransacaoServiceTest {

    @Mock
    private CartaoRepository cartaoRepository;
    @Mock
    private TransacaoRepository transacaoRepository;
    @InjectMocks
    private TransacaoService transacaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve aprovar transação válida")
    void processarTransacao_sucesso() {
        Cartao cartao = new Cartao("5555", "1234");
        TransacaoRequest req = new TransacaoRequest();
        req.setNumeroCartao("5555");
        req.setSenhaCartao("1234");
        req.setValor(new BigDecimal("10.00"));
        when(cartaoRepository.findById("5555")).thenReturn(Optional.of(cartao));
        when(cartaoRepository.debitSaldoIfHasValue("5555", new BigDecimal("10.00"))).thenReturn(1);
        assertDoesNotThrow(() -> transacaoService.processarTransacao(req));
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se cartão não existir")
    void processarTransacao_cartaoInexistente() {
        TransacaoRequest req = new TransacaoRequest();
        req.setNumeroCartao("9999");
        req.setSenhaCartao("1234");
        req.setValor(new BigDecimal("10.00"));
        when(cartaoRepository.findById("9999")).thenReturn(Optional.empty());
        assertThrows(CartaoInexistenteException.class, () -> transacaoService.processarTransacao(req));
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se senha inválida")
    void processarTransacao_senhaInvalida() {
        Cartao cartao = new Cartao("6666", "1234");
        TransacaoRequest req = new TransacaoRequest();
        req.setNumeroCartao("6666");
        req.setSenhaCartao("9999");
        req.setValor(new BigDecimal("10.00"));
        when(cartaoRepository.findById("6666")).thenReturn(Optional.of(cartao));
        assertThrows(SenhaInvalidaException.class, () -> transacaoService.processarTransacao(req));
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se saldo insuficiente")
    void processarTransacao_saldoInsuficiente() {
        Cartao cartao = new Cartao("7777", "1234");
        TransacaoRequest req = new TransacaoRequest();
        req.setNumeroCartao("7777");
        req.setSenhaCartao("1234");
        req.setValor(new BigDecimal("1000.00"));
        when(cartaoRepository.findById("7777")).thenReturn(Optional.of(cartao));
        when(cartaoRepository.debitSaldoIfHasValue("7777", new BigDecimal("1000.00"))).thenReturn(0);
        assertThrows(SaldoInsuficienteException.class, () -> transacaoService.processarTransacao(req));
        verify(transacaoRepository).save(any(Transacao.class));
    }
}

