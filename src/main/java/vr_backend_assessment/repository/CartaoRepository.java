// new file
package vr_backend_assessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vr_backend_assessment.domain.Cartao;

import java.math.BigDecimal;

@Repository
public interface CartaoRepository extends JpaRepository<Cartao, String> {

    @Modifying
    @Query("update Cartao c set c.saldo = c.saldo - :valor where c.numeroCartao = :numero and c.saldo >= :valor")
    int debitSaldoIfHasValue(@Param("numero") String numeroCartao, @Param("valor") BigDecimal valor);
}

