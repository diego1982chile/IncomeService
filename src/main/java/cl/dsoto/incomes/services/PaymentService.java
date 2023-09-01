package cl.dsoto.incomes.services;

import cl.dsoto.incomes.entities.Debt;
import cl.dsoto.incomes.entities.Fee;
import cl.dsoto.incomes.entities.House;
import cl.dsoto.incomes.entities.Payment;
import cl.dsoto.incomes.repositories.*;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
public class PaymentService {

    @PersistenceContext
    private EntityManager entityManager;

    private PaymentRepository paymentRepository;
    private FeeRepository feeRepository;
    private DebtRepository debtRepository;

    @PostConstruct
    private void init() {
        // Instantiate Spring Data factory
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        // Get an implemetation of PersonRepository from factory
        this.paymentRepository = factory.getRepository(PaymentRepository.class);
        this.feeRepository = factory.getRepository(FeeRepository.class);
        this.debtRepository = factory.getRepository(DebtRepository.class);
    }

    public List<Payment> getPayments() {
        return paymentRepository.findAllOrderByDateTime();
    }

    public List<Payment> findPaymentsByFee(long feeId) {
        Fee fee = feeRepository.findById(feeId);
        return Collections.singletonList(fee.getPayment());
    }

    public Payment getPaymentById(int id) {
        return paymentRepository.findById(id);
    }

    @Transactional
    public Payment savePayment(Payment payment, List<Integer> fees, List<Integer> debts) {
        if(payment.isPersisted()) {
            Payment previous = paymentRepository.findById(payment.getId());
            previous.setAmount(payment.getAmount());
            previous.setNeighbor(payment.getNeighbor());
            previous.setDatetime(payment.getDatetime());
            updateFees(previous, fees);
            updateDebts(previous, debts);
            return paymentRepository.save(previous);
        }
        else {
            payment.setDatetime(LocalDateTime.now());
            paymentRepository.saveAndFlush(payment);
            updateFees(payment, fees);
            updateDebts(payment, debts);
            return paymentRepository.findByNumber(payment.getNumber());
        }

    }

    private void updateFees(Payment payment, List<Integer> fees) {

        List<Fee> toRemove = feeRepository.findByPayment(payment.getNumber());

        //feeRepository.delete(toRemove);

        fees.stream().filter(Objects::nonNull).forEach(f -> {
            Fee fee = feeRepository.findById(f);
            fee.setPayment(payment);
            feeRepository.save(fee);
        });
    }

    private void updateDebts(Payment payment, List<Integer> debts) {

        debts.stream().filter(Objects::nonNull).forEach(d -> {
            Debt debt = debtRepository.findById(d);
            debt.setPayment(payment);
            debtRepository.save(debt);
        });
    }

    @Transactional
    public void deletePayment(long id) {
        Payment payment = paymentRepository.findById(id);
        List<Fee> fees = feeRepository.findByPayment(payment.getNumber());
        for (Fee fee : fees) {
            fee.setPayment(null);
            feeRepository.save(fee);
        }
        List<Debt> debts = debtRepository.findByPayment(payment.getNumber());
        for (Debt debt : debts) {
            debt.setPayment(null);
            debtRepository.save(debt);
        }
        paymentRepository.delete(id);
    }
}
