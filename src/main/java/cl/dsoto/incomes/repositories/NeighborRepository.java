package cl.dsoto.incomes.repositories;

import cl.dsoto.incomes.entities.House;
import cl.dsoto.incomes.entities.Neighbor;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by root on 13-10-22.
 */
public interface NeighborRepository extends JpaRepository<Neighbor, Integer> {
}
