package cl.dsoto.incomes.repositories;

import cl.dsoto.incomes.entities.Month;
import cl.dsoto.incomes.entities.Year;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by root on 13-10-22.
 */
public interface MonthRepository extends JpaRepository<Month, Integer> {
}
