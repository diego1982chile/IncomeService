package cl.dsoto.incomes.services;

import cl.dsoto.incomes.entities.Fee;
import cl.dsoto.incomes.entities.House;
import cl.dsoto.incomes.entities.Payment;
import cl.dsoto.incomes.entities.Year;
import cl.dsoto.incomes.repositories.FeeRepository;
import cl.dsoto.incomes.repositories.YearRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
public class FeeService {

    @PersistenceContext
    private EntityManager entityManager;

    private FeeRepository feeRepository;

    @PostConstruct
    private void init() {
        // Instantiate Spring Data factory
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        // Get an implemetation of PersonRepository from factory
        this.feeRepository = factory.getRepository(FeeRepository.class);
    }

    public List<Map<String, String>> getFees(int year) {
        List<Fee> feesEntities = feeRepository.findByYearOrderByMonth(year);
        List<Map<String, String>> fees = new ArrayList<>();

        Map<House, List<Fee>> feesPerHouse = feesEntities.stream()
                .collect(groupingBy(Fee::getHouse));

        for (House house : feesPerHouse.keySet()) {
            Map<String, String> feesMap = new LinkedHashMap<>();
            feesMap.put("house", "CASA " + String.valueOf(house.getNumber()));
            for (Fee fee : feesPerHouse.get(house)) {
                feesMap.put(fee.getMonth().getName().substring(0, 3).toUpperCase(), String.valueOf(fee.getPayments().stream().mapToInt(Payment::getAmount).sum()));
            }
            fees.add(feesMap);
        }

        return fees;
    }
}
