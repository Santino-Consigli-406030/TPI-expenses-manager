package ar.edu.utn.frc.tup.lc.iv.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Model class for the bill expense fine.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class BillExpenseFineModel extends AuditModel {
    /**
     * The unique identifier for the fine.
     */
    private Integer fineId;

    /**
     * The unique identifier for the plot.
     */
    private Integer plotId;

    /**
     * A description of the fine.
     */
    private String description;

    /**
     * The amount of the fine.
     */
    private BigDecimal amount;
}
