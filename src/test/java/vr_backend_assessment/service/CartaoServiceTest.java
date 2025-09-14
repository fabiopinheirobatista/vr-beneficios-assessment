package vr_backend_assessment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vr_backend_assessment.domain.Cartao;
import vr_backend_assessment.dto.CartaoRequest;
import vr_backend_assessment.exception.CartaoExistenteException;
import vr_backend_assessment.exception.CartaoNaoEncontradoException;
import vr_backend_assessment.repository.CartaoRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartaoServiceTest {

    @Mock
    private CartaoRepository cartaoRepository;

    @InjectMocks
    private CartaoService cartaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve criar cartão se não existir")
    void createCartao_sucesso() {
        CartaoRequest request = new CartaoRequest();
        request.setNumeroCartao("1111");
        request.setSenha("1234");
        when(cartaoRepository.findById("1111")).thenReturn(Optional.empty());
        when(cartaoRepository.save(any(Cartao.class))).thenAnswer(i -> i.getArgument(0));
        Cartao cartao = cartaoService.createCartao(request);
        assertEquals("1111", cartao.getNumeroCartao());
        assertEquals("1234", cartao.getSenha());
        assertEquals(new BigDecimal("500.00"), cartao.getSaldo());
    }

    @Test
    @DisplayName("Deve lançar exceção se cartão já existir")
    void createCartao_existente() {
        CartaoRequest request = new CartaoRequest();
        request.setNumeroCartao("2222");
        request.setSenha("1234");
        when(cartaoRepository.findById("2222")).thenReturn(Optional.of(new Cartao("2222", "1234")));
        assertThrows(CartaoExistenteException.class, () -> cartaoService.createCartao(request));
    }

    @Test
    @DisplayName("Deve retornar saldo de cartão existente")
    void obterSaldo_sucesso() {
        Cartao cartao = new Cartao("3333", "1234");
        cartao.setSaldo(new BigDecimal("400.00"));
        when(cartaoRepository.findById("3333")).thenReturn(Optional.of(cartao));
        BigDecimal saldo = cartaoService.obterSaldo("3333");
        assertEquals(new BigDecimal("400.00"), saldo);
    }

    @Test
    @DisplayName("Deve lançar exceção se cartão não existir ao consultar saldo")
    void obterSaldo_cartaoInexistente() {
        when(cartaoRepository.findById("4444")).thenReturn(Optional.empty());
        assertThrows(CartaoNaoEncontradoException.class, () -> cartaoService.obterSaldo("4444"));
    }
}

