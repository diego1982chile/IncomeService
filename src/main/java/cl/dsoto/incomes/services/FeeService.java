package cl.dsoto.incomes.services;

import cl.dsoto.incomes.dtos.FeeDTO;
import cl.dsoto.incomes.entities.Fee;
import cl.dsoto.incomes.entities.House;
import cl.dsoto.incomes.entities.Payment;
import cl.dsoto.incomes.entities.Year;
import cl.dsoto.incomes.repositories.FeeRepository;
import cl.dsoto.incomes.repositories.PaymentRepository;
import cl.dsoto.incomes.repositories.YearRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
public class FeeService {

    @PersistenceContext
    private EntityManager entityManager;

    private FeeRepository feeRepository;

    private PaymentRepository paymentRepository;

    @PostConstruct
    private void init() {
        // Instantiate Spring Data factory
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        // Get an implemetation of PersonRepository from factory
        this.feeRepository = factory.getRepository(FeeRepository.class);
        this.paymentRepository = factory.getRepository(PaymentRepository.class);
    }

    public List<Map<String, Object>> getFees(int year) {

        List<Fee> feesEntities = feeRepository.findByYearOrderByMonth(year);
        List<Map<String, Object>> fees = new ArrayList<>();

        Map<House, List<Fee>> feesPerHouse = feesEntities.stream()
                .filter(f -> f.getHouse() != null)
                .collect(groupingBy(Fee::getHouse));

        List<House> houses = feesPerHouse.keySet().stream()
                .sorted((e1, e2) -> e1.getNumber() - e2.getNumber())
                .collect(Collectors.toList());

        for (House house : houses) {
            Map<String, Object> feesMap = new LinkedHashMap<>();
            feesMap.put("house", "CASA " + String.valueOf(house.getNumber()));
            for (Fee fee : feesPerHouse.get(house)) {
                FeeDTO feeDTO = FeeDTO.builder()
                        .id(fee.getId())
                        .amount(fee.getAmount())
                        .payment(fee.getPayments().stream().mapToInt(Payment::getAmount).sum())
                        .build();
                feesMap.put(fee.getMonth().getName().substring(0, 3).toUpperCase(), feeDTO);
            }
            fees.add(feesMap);
        }

        return fees;
    }

    public Fee getFeeById(int id) {
        return feeRepository.findById(id);
    }

    @Transactional
    public Fee saveFee(Fee fee) {
        if(fee.isPersisted()) {
            Fee previous = feeRepository.findById(fee.getId());
            fee.getPayments().forEach(e -> {
                if(!e.isPersisted()) {
                    e.setDatetime(LocalDateTime.now());
                    previous.getPayments().add(e);
                }
            });
            return feeRepository.save(previous);
        }
        else {
            return feeRepository.save(fee);
        }
    }
}
