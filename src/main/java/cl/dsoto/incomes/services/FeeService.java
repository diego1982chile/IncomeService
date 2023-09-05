package cl.dsoto.incomes.services;

import cl.dsoto.incomes.dtos.AmountType;
import cl.dsoto.incomes.dtos.FeeDTO;
import cl.dsoto.incomes.entities.*;
import cl.dsoto.incomes.repositories.FeeRepository;
import cl.dsoto.incomes.repositories.MonthRepository;
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

    private MonthRepository monthRepository;

    @PostConstruct
    private void init() {
        // Instantiate Spring Data factory
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        // Get an implemetation of PersonRepository from factory
        this.feeRepository = factory.getRepository(FeeRepository.class);
        this.paymentRepository = factory.getRepository(PaymentRepository.class);
        this.monthRepository = factory.getRepository(MonthRepository.class);
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
            FeeDTO debtDTO = FeeDTO.builder()
                    .month(0)
                    .payment(house.getDebts().stream()
                            .filter(d -> Objects.isNull(d.getPayment())).mapToInt(Debt::getAmount).sum()*-1)
                            .amountType(AmountType.DEBT)
                    .build();
            feesMap.put("DEBT", debtDTO);
            for (Fee fee : feesPerHouse.get(house)) {
                FeeDTO feeDTO = FeeDTO.builder()
                        .id(fee.getId())
                        .amount(fee.getAmount())
                        .month(fee.getMonth().getMonth())
                        .payment(Objects.nonNull(fee.getPayment()) ? fee.getAmount() : 0)
                        .amountType(AmountType.FEE)
                        .build();
                feesMap.put(fee.getMonth().getName().substring(0, 3).toUpperCase(), feeDTO);
            }
            FeeDTO totalDTO = FeeDTO.builder()
                    .payment(feesMap.values().stream().filter(f -> f instanceof FeeDTO)
                            .mapToInt(e -> ((FeeDTO) e).getPayment()).sum())
                    .amountType(AmountType.TOTAL)
                    .build();
            feesMap.put("TOTAL", totalDTO);
            fees.add(feesMap);
        }

        Map<String, Object> totals = new LinkedHashMap<>();

        for (Map<String, Object> fee : fees) {

            for (Month month : monthRepository.findAllOrderByMonth()) {

                for (String s : fee.keySet()) {
                    if (s.equalsIgnoreCase("house")) {
                        totals.put("house", "TOTALES");
                        continue;
                    }

                    FeeDTO feeDTO = (FeeDTO) fee.get(s);

                    if (feeDTO.getMonth() == 0 && feeDTO.getAmountType().equals(AmountType.DEBT)) {
                        if (totals.containsKey(AmountType.DEBT.name())) {
                            FeeDTO totalDTO = (FeeDTO) totals.get(AmountType.DEBT.name());
                            totalDTO.setPayment(totalDTO.getPayment() + feeDTO.getPayment());
                        } else {
                            FeeDTO totalDTO = FeeDTO.builder()
                                    .payment(feeDTO.getPayment())
                                    .amountType(AmountType.DEBT)
                                    .build();
                            totals.put(AmountType.DEBT.name(), totalDTO);
                        }
                    }

                    if (month.getMonth() == feeDTO.getMonth()) {
                        if (totals.containsKey(month.getName().substring(0, 3).toUpperCase())) {
                            FeeDTO totalDTO = (FeeDTO) totals.get(month.getName().substring(0, 3).toUpperCase());
                            totalDTO.setPayment(totalDTO.getPayment() + feeDTO.getPayment());
                        }
                        else {
                            FeeDTO totalDTO = FeeDTO.builder()
                                    .payment(feeDTO.getPayment())
                                    .amountType(AmountType.TOTAL)
                                    .build();
                            totals.put(month.getName().substring(0, 3).toUpperCase(), totalDTO);
                        }
                    }
                }
            }

            FeeDTO feeDTO = (FeeDTO) fee.get("TOTAL");

            if (totals.containsKey(AmountType.TOTAL.name())) {
                FeeDTO totalDTO = (FeeDTO) totals.get(AmountType.TOTAL.name());
                totalDTO.setPayment(totalDTO.getPayment() + feeDTO.getPayment());
            } else {
                FeeDTO totalDTO = FeeDTO.builder()
                        .payment(feeDTO.getPayment())
                        .amountType(AmountType.TOTAL)
                        .build();
                totals.put(AmountType.TOTAL.name(), totalDTO);
            }
        }

        Map<String, Object> feesMap = new LinkedHashMap<>();

        for (String month : totals.keySet()) {
            feesMap.put(month, totals.get(month));
        }

        fees.add(feesMap);

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
        List<Payment> payments = paymentRepository.findAll();
        int correlative = payments.size();
        String number = fee.getHouse().getNumber().toString() + String.valueOf(correlative);

        return Integer.parseInt(number);
    }
}
