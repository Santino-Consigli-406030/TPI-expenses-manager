package ar.edu.utn.frc.tup.lc.iv.dtos.common;

import ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data transfer object for expense owner visualizers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseOwnerVisualizerDTO {
    /**
     * The unique identifier for the expense owner visualizer.
     */
    private Integer id;

    /**
     * The identifier of the associated expense.
     */
    private Integer expenseId;

    /**
     * The description of the expense.
     */
    private String description;

    /**
     * The identifier of the provider associated with the expense.
     */
    private Integer providerId;

    /**
     * The description of the provider associated with the expense.
     */
    private String providerDescription;

    /**
     * The date of the expense.
     */
    private LocalDate expenseDate;

    /**
     * The unique identifier for the file associated with the expense.
     */
    private UUID fileId;

    /**
     * The invoice number of the expense.
     */
    private String invoiceNumber;

    /**
     * The type of the expense.
     */
    private ExpenseType expenseType;

    /**
     * The category of the expense.
     */
    private ExpenseCategoryDTO category;

    /**
     * The amount of the expense.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal amount;

    /**
     * The proportion of the expense distribution.
     */
    private BigDecimal proportion;

    /**
     * The number of installments for the expense.
     */
    private Integer installments;

    /**
     * Indicates whether the expense is enabled.
     */
    private Boolean enabled;
}
