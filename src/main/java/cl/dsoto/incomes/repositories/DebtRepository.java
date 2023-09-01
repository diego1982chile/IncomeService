package cl.dsoto.incomes.repositories;

import cl.dsoto.incomes.entities.Debt;
import cl.dsoto.incomes.entities.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by root on 13-10-22.
 */
public interface DebtRepository extends JpaRepository<Debt, Long> {

    Debt findById(long id);

    @Query("SELECT d FROM Debt d WHERE d.payment.number = :paymentNumber")
    List<Debt> findByPayment(@Param("paymentNumber") int paymentNumber);
}
