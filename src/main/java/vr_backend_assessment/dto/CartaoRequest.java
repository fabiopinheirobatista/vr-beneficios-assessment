package vr_backend_assessment.dto;

public class CartaoRequest {
    private String numeroCartao;
    private String senha;

    public CartaoRequest() {}

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}

