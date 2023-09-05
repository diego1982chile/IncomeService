package cl.dsoto.incomes.repositories;

import cl.dsoto.incomes.entities.Month;
import cl.dsoto.incomes.entities.Year;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by root on 13-10-22.
 */
public interface MonthRepository extends JpaRepository<Month, Long> {

    @Query("SELECT m FROM Month m order by m.month")
    List<Month> findAllOrderByMonth();
}
