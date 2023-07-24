package cl.dsoto.incomes.services;

import cl.dsoto.incomes.entities.*;
import cl.dsoto.incomes.repositories.*;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
public class DatabaseService {

    @PersistenceContext
    private EntityManager entityManager;
    private YearRepository yearRepository;
    private MonthRepository monthRepository;
    private HouseRepository houseRepository;
    private NeighborRepository neighborRepository;
    private PaymentRepository paymentRepository;
    private FeeRepository feeRepository;

    private String[] monthNames = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    //private int[] houseNumbers = {25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37};
    private int[] houseNumbers = {32};

    @PostConstruct
    private void init() {
        // Instantiate Spring Data factory
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        // Get an implemetation of PersonRepository from factory
        this.yearRepository = factory.getRepository(YearRepository.class);
        this.monthRepository = factory.getRepository(MonthRepository.class);
        this.houseRepository = factory.getRepository(HouseRepository.class);
        this.houseRepository = factory.getRepository(HouseRepository.class);
        this.neighborRepository = factory.getRepository(NeighborRepository.class);
        this.paymentRepository = factory.getRepository(PaymentRepository.class);
        this.feeRepository = factory.getRepository(FeeRepository.class);
    }

    @Transactional
    public void removeData() {
        feeRepository.deleteAll();
        paymentRepository.deleteAll();
        neighborRepository.deleteAll();
        houseRepository.deleteAll();
        monthRepository.deleteAll();
        yearRepository.deleteAll();
    }

    @Transactional
    public void loadData() {

        int start = LocalDate.now().getYear() - 1;
        int end = LocalDate.now().getYear();

        List<Year> years = new ArrayList<>();
        List<Month> months = new ArrayList<>();
        List<House> houses = new ArrayList<>();


        for (int i = start; i <= end; ++i) {
            Year year = Year.builder().year(i).build();
            years.add(year);
        }

        for(int j = 1; j <= monthNames.length; ++j) {
            Month month = Month.builder().month(j).name(monthNames[j-1]).build();
            months.add(month);
        }

        for(int j = 1; j <= houseNumbers.length; ++j) {
            House house = House.builder()
                    .number(houseNumbers[j-1])
                    .neighbors(new ArrayList<>())
                    .debts(new ArrayList<>())
                    .build();
            Neighbor neighbor1 = Neighbor.builder()
                    .name("Diego")
                    .lastname("Soto")
                    .email("diego.abelardo.soto@gmail.com")
                    .phone("+56956582261")
                    .build();
            Neighbor neighbor2 = Neighbor.builder()
                    .name("Francisca")
                    .lastname("Herrera")
                    .email("fca.herrer@gmail.com")
                    .phone("+56940929147")
                    .build();
            Debt debt = Debt.builder().amount(50000).build();
            house.getNeighbors().add(neighbor1);
            house.getNeighbors().add(neighbor2);
            house.getDebts().add(debt);
            //neighborRepository.save(neighbor1);
            //neighborRepository.save(neighbor2);
            houses.add(house);
        }

        yearRepository.save(years);
        monthRepository.save(months);
        houseRepository.save(houses);

        for (Year year : years) {
            for (Month month : months) {
                for (House house : houses) {
                    Fee fee = Fee.builder()
                            .amount(11000)
                            .year(year)
                            .month(month)
                            .house(house)
                            .build();
                    feeRepository.save(fee);
                }
            }
        }
    }
}
