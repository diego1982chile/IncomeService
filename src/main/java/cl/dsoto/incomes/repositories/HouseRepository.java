package cl.dsoto.incomes.repositories;

import cl.dsoto.incomes.entities.House;
import cl.dsoto.incomes.entities.Year;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by root on 13-10-22.
 */
public interface HouseRepository extends JpaRepository<House, Long> {

    House findById(long id);

    @Query("SELECT h FROM House h order by h.number asc")
    List<House> findAllOrderByNumber();
}
