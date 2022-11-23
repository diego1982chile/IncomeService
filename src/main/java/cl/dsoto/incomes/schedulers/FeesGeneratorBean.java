package cl.dsoto.incomes.schedulers;

import cl.dsoto.incomes.entities.Fee;
import cl.dsoto.incomes.entities.House;
import cl.dsoto.incomes.entities.Month;
import cl.dsoto.incomes.entities.Year;
import cl.dsoto.incomes.repositories.*;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by root on 22-11-22.
 */
@Startup
@Singleton
@Log4j
public class FeesGeneratorBean {

    @PersistenceContext
    private EntityManager entityManager;
    private YearRepository yearRepository;
    private MonthRepository monthRepository;
    private HouseRepository houseRepository;
    private FeeRepository feeRepository;

    @PostConstruct
    private void init() {
        // Instantiate Spring Data factory
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        // Get an implemetation of PersonRepository from factory
        this.yearRepository = factory.getRepository(YearRepository.class);
        this.monthRepository = factory.getRepository(MonthRepository.class);
        this.houseRepository = factory.getRepository(HouseRepository.class);
        this.houseRepository = factory.getRepository(HouseRepository.class);
        this.feeRepository = factory.getRepository(FeeRepository.class);
    }


    @Schedule(hour = "*", minute = "*/1", persistent = false)
    public void doPeriodicFeesGeneration() {
        log.info("doPeriodicFeesGeneration()");
        for (Year year : yearRepository.findAll()) {
            for (Month month : monthRepository.findAll()) {
                for (House house : houseRepository.findAll()) {
                    Fee fee = Fee.builder()
                            .amount(11000)
                            .year(year)
                            .month(month)
                            .house(house)
                            .build();

                    List<Fee> existing = feeRepository.findByYearMonthAndHouse(year.getYear(), month.getMonth(), house.getId());

                    if(existing.isEmpty()) {
                        feeRepository.save(fee);
                    }
                }
            }
        }
    }
}
