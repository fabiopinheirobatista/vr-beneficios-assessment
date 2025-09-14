CREATE TABLE cartoes (
    numero_cartao VARCHAR(19) PRIMARY KEY,
    senha VARCHAR(255) NOT NULL,
    saldo DECIMAL(19,2) NOT NULL
);

CREATE TABLE transacoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_cartao VARCHAR(19),
    valor DECIMAL(19,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_transacao_cartao FOREIGN KEY (numero_cartao) REFERENCES cartoes(numero_cartao)
);

