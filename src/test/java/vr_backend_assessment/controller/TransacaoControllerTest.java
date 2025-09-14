package vr_backend_assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import vr_backend_assessment.dto.CartaoRequest;
import vr_backend_assessment.dto.TransacaoRequest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@SpringBootTest
@AutoConfigureMockMvc
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String criarCartao(String numero, String senha) throws Exception {
        CartaoRequest request = new CartaoRequest();
        request.setNumeroCartao(numero);
        request.setSenha(senha);
        mockMvc.perform(MockMvcRequestBuilders.post("/cartoes")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        return numero;
    }

    @Test
    @DisplayName("Deve aprovar transação com saldo suficiente e senha correta")
    void transacao_aprovada() throws Exception {
        String numero = "6549873025634601";
        String senha = "1234";
        criarCartao(numero, senha);

        TransacaoRequest transacao = new TransacaoRequest();
        transacao.setNumeroCartao(numero);
        transacao.setSenhaCartao(senha);
        transacao.setValor(new java.math.BigDecimal("10.00"));

        mockMvc.perform(MockMvcRequestBuilders.post("/transacoes")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transacao)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("OK"));
    }

    @Test
    @DisplayName("Deve recusar transação por saldo insuficiente")
    void transacao_saldoInsuficiente() throws Exception {
        String numero = "6549873025634602";
        String senha = "1234";
        criarCartao(numero, senha);

        TransacaoRequest transacao = new TransacaoRequest();
        transacao.setNumeroCartao(numero);
        transacao.setSenhaCartao(senha);
        transacao.setValor(new java.math.BigDecimal("600.00"));

        mockMvc.perform(MockMvcRequestBuilders.post("/transacoes")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transacao)))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.content().string("SALDO_INSUFICIENTE"));
    }

    @Test
    @DisplayName("Deve recusar transação por senha inválida")
    void transacao_senhaInvalida() throws Exception {
        String numero = "6549873025634603";
        String senha = "1234";
        criarCartao(numero, senha);

        TransacaoRequest transacao = new TransacaoRequest();
        transacao.setNumeroCartao(numero);
        transacao.setSenhaCartao("9999");
        transacao.setValor(new java.math.BigDecimal("10.00"));

        mockMvc.perform(MockMvcRequestBuilders.post("/transacoes")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transacao)))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.content().string("SENHA_INVALIDA"));
    }

    @Test
    @DisplayName("Deve recusar transação por cartão inexistente")
    void transacao_cartaoInexistente() throws Exception {
        TransacaoRequest transacao = new TransacaoRequest();
        transacao.setNumeroCartao("9999999999999999");
        transacao.setSenhaCartao("1234");
        transacao.setValor(new java.math.BigDecimal("10.00"));

        mockMvc.perform(MockMvcRequestBuilders.post("/transacoes")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transacao)))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.content().string("CARTAO_INEXISTENTE"));
    }

    @Test
    @DisplayName("Deve exigir autenticação para transação")
    void transacao_semAutenticacao() throws Exception {
        TransacaoRequest transacao = new TransacaoRequest();
        transacao.setNumeroCartao("6549873025634604");
        transacao.setSenhaCartao("1234");
        transacao.setValor(new java.math.BigDecimal("10.00"));

        mockMvc.perform(MockMvcRequestBuilders.post("/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transacao)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}

