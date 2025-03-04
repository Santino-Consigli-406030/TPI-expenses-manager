package ar.edu.utn.frc.tup.lc.iv.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Data transfer object for expense distributions.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoExpenseDistributionQuery {
    /**
     * The ID of the owner.
     */
    private int ownerId;

    /**
     * The full name of the owner.
     */
    private String ownerFullName;

    /**
     * The amount of the expense.
     */
    private BigDecimal amount;

    /**
     * The proportion of the expense.
     */
    private BigDecimal proportion;
}
