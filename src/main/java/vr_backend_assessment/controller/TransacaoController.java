// new file
package vr_backend_assessment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vr_backend_assessment.dto.TransacaoRequest;
import vr_backend_assessment.service.TransacaoService;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping
    public ResponseEntity<String> realizarTransacao(@RequestBody TransacaoRequest request) {
        transacaoService.processarTransacao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }
}

