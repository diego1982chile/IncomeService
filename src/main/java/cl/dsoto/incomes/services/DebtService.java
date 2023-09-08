package cl.dsoto.incomes.services;

import cl.dsoto.incomes.dtos.FeeDTO;
import cl.dsoto.incomes.entities.Debt;
import cl.dsoto.incomes.entities.Fee;
import cl.dsoto.incomes.entities.House;
import cl.dsoto.incomes.entities.Payment;
import cl.dsoto.incomes.repositories.DebtRepository;
import cl.dsoto.incomes.repositories.FeeRepository;
import cl.dsoto.incomes.repositories.HouseRepository;
import cl.dsoto.incomes.repositories.PaymentRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;
import static java.util.stream.Collectors.groupingBy;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
public class DebtService {

    @PersistenceContext
    private EntityManager entityManager;

    private DebtRepository debtRepository;

    private HouseRepository houseRepository;

    private PaymentRepository paymentRepository;

    @PostConstruct
    private void init() {
        // Instantiate Spring Data factory
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        // Get an implemetation of PersonRepository from factory
        this.debtRepository = factory.getRepository(DebtRepository.class);
        this.paymentRepository = factory.getRepository(PaymentRepository.class);
        this.houseRepository = factory.getRepository(HouseRepository.class);
    }

    public Debt getDebtById(int id) {
        return debtRepository.findById(id);
    }

    public List<Debt> getDebtByHouse(int houseNumber) {
        List<House> houses = houseRepository.findByNumber(houseNumber);
        List<Debt> debts = new ArrayList<>();

        if (!houses.isEmpty()) {
            houses.forEach(h -> debts.addAll(h.getDebts()));
            return debts.stream().filter(d -> Objects.isNull(d.getPayment())).collect(Collectors.toList());
        }

        return debts;
    }

    public List<Debt> getDebtsByPayment(int id) {
        Payment payment = paymentRepository.findById(id);
        return debtRepository.findByPayment(payment.getNumber());
    }

}
