package ar.edu.utn.frc.tup.lc.iv.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data transfer object for expense KPIs.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoExpenseKPI {
    /**
     * The type of the expense.
     */
    private String expenseType;

    /**
     * The ID of the category associated with the expense.
     */
    private Integer categoryId;

    /**
     * A description of the expense.
     */
    private String description;

    /**
     * The amount of the expense.
     */
    private BigDecimal amount;

    /**
     * The ID of the provider associated with the expense.
     */
    private Integer providerId;
}
