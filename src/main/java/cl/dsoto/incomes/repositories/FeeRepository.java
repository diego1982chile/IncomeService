package cl.dsoto.incomes.repositories;

import cl.dsoto.incomes.entities.Fee;
import cl.dsoto.incomes.entities.Neighbor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by root on 13-10-22.
 */
public interface FeeRepository extends JpaRepository<Fee, Long> {

    @Query("SELECT f FROM Fee f WHERE f.year.year = :year order by f.month.month")
    List<Fee> findByYearOrderByMonth(@Param("year") int year);

    @Query("SELECT f FROM Fee f WHERE f.year.year = :year and f.month.month = :month and f.house.id = :houseId")
    List<Fee> findByYearMonthAndHouse(@Param("year") int year, @Param("month") int month, @Param("houseId") long houseId);

    @Modifying
    @Query("delete from Fee f where f.house.id = :houseId")
    void deleteByHouseId(@Param("houseId") long houseId);

    Fee findById(long id);

    @Query("SELECT f FROM Fee f WHERE f.house.number = :house AND f.payment is null order by f.year.year, f.month.month")
    List<Fee> findByHouseOrderByMonth(@Param("house") int house);

    @Query("SELECT f FROM Fee f WHERE f.house.number = :house AND f.payment is null AND f.year.year <= :year order by f.year.year, f.month.month")
    List<Fee> findUnpaid(@Param("house") int house, @Param("year") int year);

    @Query("SELECT f FROM Fee f WHERE f.payment.number = :paymentNumber order by f.year.year, f.month.month")
    List<Fee> findByPayment(@Param("paymentNumber") int paymentNumber);
}
