package cl.dsoto.incomes.repositories;

import cl.dsoto.incomes.entities.Fee;
import cl.dsoto.incomes.entities.Neighbor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by root on 13-10-22.
 */
public interface FeeRepository extends JpaRepository<Fee, Long> {

    @Query("SELECT f FROM Fee f WHERE f.year.year = :year order by f.month.month")
    List<Fee> findByYearOrderByMonth(@Param("year") int year);
}
