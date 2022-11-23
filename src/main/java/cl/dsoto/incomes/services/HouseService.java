package cl.dsoto.incomes.services;

import cl.dsoto.incomes.entities.House;
import cl.dsoto.incomes.entities.Year;
import cl.dsoto.incomes.repositories.FeeRepository;
import cl.dsoto.incomes.repositories.HouseRepository;
import cl.dsoto.incomes.repositories.NeighborRepository;
import cl.dsoto.incomes.repositories.YearRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
public class HouseService {

    @PersistenceContext
    private EntityManager entityManager;
    private HouseRepository houseRepository;
    private NeighborRepository neighborRepository;
    private FeeRepository feeRepository;

    @PostConstruct
    private void init() {
        // Instantiate Spring Data factory
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        // Get an implemetation of PersonRepository from factory
        this.houseRepository = factory.getRepository(HouseRepository.class);
        this.neighborRepository = factory.getRepository(NeighborRepository.class);
        this.feeRepository = factory.getRepository(FeeRepository.class);
    }

    public List<House> getHouses() {
        return houseRepository.findAllOrderByNumber();
    }

    public House getHouseById(int id) {
        return houseRepository.findById(id);
    }

    @Transactional
    public House saveHouse(House house) {
        if(house.isPersisted()) {
            House previous = houseRepository.findById(house.getId());
            previous.setDebts(house.getDebts());
            previous.setNumber(house.getNumber());
            previous.setNeighbors(house.getNeighbors());
            return houseRepository.save(previous);
        }
        else {
            return houseRepository.save(house);
        }
    }

    @Transactional
    public void deleteHouse(long id) {
        feeRepository.deleteByHouseId(id);
        houseRepository.delete(id);

    }
}
