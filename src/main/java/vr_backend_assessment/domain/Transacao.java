package vr_backend_assessment.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "transacoes")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cartao", length = 19)
    private String numeroCartao;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Column(name = "status", nullable = false)
    private String status;

    public Transacao() {}

    public Transacao(String numeroCartao, BigDecimal valor, String status) {
        this.numeroCartao = numeroCartao;
        this.valor = valor;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getStatus() {
        return status;
    }
}

