package cl.dsoto.incomes.repositories;

import cl.dsoto.incomes.entities.House;
import cl.dsoto.incomes.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by root on 13-10-22.
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findById(long id);

    @Query("SELECT p FROM Payment p order by p.datetime asc")
    List<Payment> findAllOrderByDateTime();
}
