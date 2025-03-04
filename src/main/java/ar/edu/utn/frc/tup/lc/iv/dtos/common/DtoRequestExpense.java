package ar.edu.utn.frc.tup.lc.iv.dtos.common;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Data transfer object for expense requests.
 */
@Data
public class DtoRequestExpense {
    /**
     * The unique identifier for the expense.
     */
    private Integer id;

    /**
     * A brief description of the expense.
     */
    private String description;

    /**
     * The unique identifier for the provider.
     */
    private Integer providerId;

    /**
     * The date of the expense.
     */
    private LocalDate expenseDate;

    /**
     * The invoice number associated with the expense.
     */
    private String invoiceNumber;

    /**
     * The type of the expense.
     */
    private String typeExpense;

    /**
     * The unique identifier for the category.
     */
    private Integer categoryId;

    /**
     * The amount of the expense.
     */
    private BigDecimal amount;

    /**
     * The number of installments for the expense.
     */
    private Integer installments;

    /**
     * The list of expense distributions.
     */
    private List<DtoDistribution> distributions;
}
