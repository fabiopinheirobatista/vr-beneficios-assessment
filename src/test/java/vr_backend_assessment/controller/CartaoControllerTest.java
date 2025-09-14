package vr_backend_assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import vr_backend_assessment.dto.CartaoRequest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@SpringBootTest
@AutoConfigureMockMvc
class CartaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar um novo cartão com sucesso")
    void criarCartao_sucesso() throws Exception {
        CartaoRequest request = new CartaoRequest();
        request.setNumeroCartao("6549873025634501");
        request.setSenha("1234");

        mockMvc.perform(MockMvcRequestBuilders.post("/cartoes")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.numeroCartao").value("6549873025634501"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.senha").value("1234"));
    }

    @Test
    @DisplayName("Não deve criar cartão já existente")
    void criarCartao_existente() throws Exception {
        CartaoRequest request = new CartaoRequest();
        request.setNumeroCartao("6549873025634502");
        request.setSenha("1234");

        // Cria o cartão
        mockMvc.perform(MockMvcRequestBuilders.post("/cartoes")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Tenta criar novamente
        mockMvc.perform(MockMvcRequestBuilders.post("/cartoes")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("$.numeroCartao").value("6549873025634502"));
    }

    @Test
    @DisplayName("Deve consultar saldo de cartão existente")
    void consultarSaldo_sucesso() throws Exception {
        CartaoRequest request = new CartaoRequest();
        request.setNumeroCartao("6549873025634503");
        request.setSenha("1234");

        // Cria o cartão
        mockMvc.perform(MockMvcRequestBuilders.post("/cartoes")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Consulta saldo
        mockMvc.perform(MockMvcRequestBuilders.get("/cartoes/6549873025634503")
                .with(httpBasic("username", "password")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("500.00"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao consultar saldo de cartão inexistente")
    void consultarSaldo_cartaoInexistente() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cartoes/9999999999999999")
                .with(httpBasic("username", "password")))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve exigir autenticação para criar cartão")
    void criarCartao_semAutenticacao() throws Exception {
        CartaoRequest request = new CartaoRequest();
        request.setNumeroCartao("6549873025634504");
        request.setSenha("1234");

        mockMvc.perform(MockMvcRequestBuilders.post("/cartoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}

