package cl.dsoto.incomes.repositories;

import cl.dsoto.incomes.entities.House;
import cl.dsoto.incomes.entities.Year;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by root on 13-10-22.
 */
public interface HouseRepository extends JpaRepository<House, Integer> {
}
