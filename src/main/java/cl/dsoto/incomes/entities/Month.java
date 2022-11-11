package cl.dsoto.incomes.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by root on 12-10-22.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "month")
public class Month extends AbstractPersistableEntity<Long> {

    /**
     * Identificador o llave primaria de la entidad persistente
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private int month;

    @Column(unique = true)
    private String name;

}
