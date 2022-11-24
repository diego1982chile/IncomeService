package cl.dsoto.incomes.services;

import cl.dsoto.incomes.entities.House;
import cl.dsoto.incomes.entities.Payment;
import cl.dsoto.incomes.repositories.FeeRepository;
import cl.dsoto.incomes.repositories.HouseRepository;
import cl.dsoto.incomes.repositories.NeighborRepository;
import cl.dsoto.incomes.repositories.PaymentRepository;
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
public class PaymentService {

    @PersistenceContext
    private EntityManager entityManager;
    private PaymentRepository paymentRepository;

    @PostConstruct
    private void init() {
        // Instantiate Spring Data factory
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        // Get an implemetation of PersonRepository from factory
        this.paymentRepository = factory.getRepository(PaymentRepository.class);
    }

    public List<Payment> getPayments() {
        return paymentRepository.findAllOrderByDateTime();
    }

    public Payment getPaymentById(int id) {
        return paymentRepository.findById(id);
    }

    @Transactional
    public Payment savePayment(Payment payment) {
        if(payment.isPersisted()) {
            Payment previous = paymentRepository.findById(payment.getId());
            previous.setAmount(payment.getAmount());
            previous.setNeighbor(payment.getNeighbor());
            previous.setDatetime(payment.getDatetime());
            return paymentRepository.save(previous);
        }
        else {
            return paymentRepository.save(payment);
        }
    }

    @Transactional
    public void deleteHouse(long id) {
        paymentRepository.delete(id);
    }
}
