package cl.dsoto.incomes.dtos;

import lombok.Builder;
import lombok.Data;

/**
 * Created by root on 23-11-22.
 */
@Data
@Builder
public class FeeDTO {

    private Long id;

    private int amount;

    private int payment;
}
