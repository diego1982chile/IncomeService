package cl.dsoto.incomes.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by root on 12-10-22.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "year")
public class Year {

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
    private int year;

}
