package ar.edu.utn.frc.tup.lc.iv.models;

import ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Model class for the expense.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseModel {
    /**
     * The unique identifier for the expense.
     */
    private Integer id;

    /**
     * The description of the expense.
     */
    private String description;

    /**
     * The identifier of the provider associated with the expense.
     */
    private Integer providerId;

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
    private ExpenseCategoryModel category;

    /**
     * The amount of the expense.
     */
    private BigDecimal amount;

    /**
     * The number of installments for the expense.
     */
    private Integer installments;

    /**
     * The date and time when the expense was created.
     */
    private LocalDateTime createdDatetime;

    /**
     * The identifier of the user who created the expense.
     */
    private Integer createdUser;

    /**
     * The date and time when the expense was last updated.
     */
    private LocalDateTime lastUpdatedDatetime;

    /**
     * The identifier of the user who last updated the expense.
     */
    private Integer lastUpdatedUser;

    /**
     * Indicates whether the expense is a note credit.
     */
    private Boolean noteCredit;

    /**
     * Indicates whether the expense is enabled.
     */
    private Boolean enabled;

    /**
     * The list of distributions for the expense.
     */
    private List<ExpenseDistributionModel> distributions;

    /**
     * The list of installments for the expense.
     */
    private List<ExpenseInstallmentModel> installmentsList;
}
