package ar.edu.utn.frc.tup.lc.iv.models;

import ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Model class for the bill expense installment.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BillExpenseInstallmentModel extends AuditModel {
    /**
     * The type of expense.
     */
    private ExpenseType expenseType;

    /**
     * The installment details of the expense.
     */
    private ExpenseInstallmentModel expenseInstallment;

    /**
     * A description of the expense.
     */
    private String description;

    /**
     * The amount of the expense.
     */
    private BigDecimal amount;
}
