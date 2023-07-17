package cl.dsoto.incomes.services;

import cl.dsoto.incomes.dtos.FeeDTO;
import cl.dsoto.incomes.entities.*;
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

import static java.util.Collections.EMPTY_LIST;
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
            feesMap.put("house", "CASA-" + String.valueOf(house.getNumber()));
            feesMap.put("DEBT", house.getDebts().stream().mapToInt(Debt::getAmount).sum()*-1);
            for (Fee fee : feesPerHouse.get(house)) {
                FeeDTO feeDTO = FeeDTO.builder()
                        .id(fee.getId())
                        .amount(fee.getAmount())
                        .payment(Objects.nonNull(fee.getPayment()) ? fee.getPayment().getAmount() : 0)
                        .build();
                feesMap.put(fee.getMonth().getName().substring(0, 3).toUpperCase(), feeDTO);
            }
            feesMap.put("TOTAL", feesMap.values().stream().filter(f -> f instanceof FeeDTO)
                    .mapToInt(e -> ((FeeDTO) e).getAmount()).sum());
            fees.add(feesMap);
        }

        return fees;
    }

    public Fee getFeeById(int id) {
        return feeRepository.findById(id);
    }

    public List<Fee> getFeeByHouse(int houseNumber) {
        return feeRepository.findByHouseOrderByMonth(houseNumber);
    }

    public List<Fee> getUnpaidFees(int id) {
        Fee fee = feeRepository.findById(id);
        if (Objects.isNull(fee)) {
            return EMPTY_LIST;
        }
        List<Fee> fees = feeRepository.findUnpaid(fee.getHouse().getNumber(), fee.getYear().getYear());
        List<Fee> toRemove = new ArrayList<>();
        for (Fee f : fees) {
            if (f.getYear().getYear() == fee.getYear().getYear() &&
                    f.getMonth().getMonth() > fee.getMonth().getMonth()) {
                toRemove.add(f);
            }
        }
        fees.removeAll(toRemove);
        return fees;
    }

    public List<Fee> getFeesByPayment(int id) {
        Payment payment = paymentRepository.findById(id);
        List<Fee> fees = feeRepository.findByPayment(payment.getNumber());
        return fees;
    }

    @Transactional
    public Fee saveFee(Fee fee) {
        if(fee.isPersisted()) {
            Fee previous = feeRepository.findById(fee.getId());

            return feeRepository.save(previous);
        }
        else {
            return feeRepository.save(fee);
        }
    }

    public int generatePaymentNumber(int feeId) {
        Fee fee = feeRepository.findById(feeId);
        int correlative = 1;
        String number = fee.getHouse().getNumber().toString() +
                String.valueOf(fee.getYear().getYear()).substring(Math.max(String.valueOf(fee.getYear().getYear()).length() - 2, 0)) +
                String.format("%02d", fee.getMonth().getMonth()) +
                String.valueOf(correlative);

        return Integer.parseInt(number);
    }
}
