package vr_backend_assessment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vr_backend_assessment.dto.CartaoRequest;
import vr_backend_assessment.service.CartaoService;
import vr_backend_assessment.domain.Cartao;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    private final CartaoService cartaoService;

    public CartaoController(CartaoService cartaoService) {
        this.cartaoService = cartaoService;
    }

    @PostMapping
    public ResponseEntity<CartaoRequest> criarCartao(@RequestBody CartaoRequest request) {
        Cartao cartao = cartaoService.createCartao(request);
        CartaoRequest resp = new CartaoRequest();
        resp.setNumeroCartao(cartao.getNumeroCartao());
        resp.setSenha(cartao.getSenha());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/{numeroCartao}")
    public ResponseEntity<BigDecimal> obterSaldo(@PathVariable String numeroCartao) {
        BigDecimal saldo = cartaoService.obterSaldo(numeroCartao);
        return ResponseEntity.ok(saldo);
    }
}

