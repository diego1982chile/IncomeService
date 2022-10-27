package cl.dsoto.incomes.services;

import cl.dsoto.incomes.entities.House;
import cl.dsoto.incomes.entities.Year;
import cl.dsoto.incomes.repositories.HouseRepository;
import cl.dsoto.incomes.repositories.YearRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
public class HouseService {

    @PersistenceContext
    private EntityManager entityManager;
    private HouseRepository houseRepository;

    @PostConstruct
    private void init() {
        // Instantiate Spring Data factory
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        // Get an implemetation of PersonRepository from factory
        this.houseRepository = factory.getRepository(HouseRepository.class);
    }

    public List<House> getHouses() {
        return houseRepository.findAll();
    }
}
