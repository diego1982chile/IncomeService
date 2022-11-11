package cl.dsoto.incomes.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * Created by root on 12-10-22.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "house")
public class House extends AbstractPersistableEntity<Long> {

    /**
     * Identificador o llave primaria de la entidad persistente
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Rut del alumno
     */
    @Column(unique = true)
    private int number;


    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private List<Neighbor> neighbors;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private List<Debt> debts;

    public int getTotalDebt() {
        return getDebts().stream().mapToInt(Debt::getAmount).sum();
    }
}
