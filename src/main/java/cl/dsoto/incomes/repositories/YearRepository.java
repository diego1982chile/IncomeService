package cl.dsoto.incomes.repositories;

import cl.dsoto.incomes.entities.Fee;
import cl.dsoto.incomes.entities.Year;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.enterprise.context.RequestScoped;
import java.util.List;

/**
 * Created by root on 13-10-22.
 */
public interface YearRepository extends JpaRepository<Year, Integer> {

    @Query("SELECT y FROM Year y order by y.year desc")
    List<Year> findAllOrderByYearDesc();
}
