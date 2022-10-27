package cl.dsoto.incomes.services;

import cl.dsoto.incomes.entities.Year;
import cl.dsoto.incomes.repositories.YearRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
public class YearService {

    @PersistenceContext
    private EntityManager entityManager;
    private YearRepository yearRepository;

    @PostConstruct
    private void init() {
        // Instantiate Spring Data factory
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        // Get an implemetation of PersonRepository from factory
        this.yearRepository = factory.getRepository(YearRepository.class);
    }

    public List<Year> getYears() {
        return yearRepository.findAllOrderByYearDesc();
    }
}
