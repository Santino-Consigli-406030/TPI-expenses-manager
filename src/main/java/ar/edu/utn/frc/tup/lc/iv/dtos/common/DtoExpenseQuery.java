package ar.edu.utn.frc.tup.lc.iv.dtos.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Data transfer object for expenses.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoExpenseQuery {
    /**
     * The unique identifier for the expense.
     */
    private int id;

    /**
     * A brief description of the expense.
     */
    private String description;

    /**
     * The category of the expense.
     */
    private String category;

    /**
     * The unique identifier for the category.
     */
    private Integer categoryId;

    /**
     * The provider of the expense.
     */
    private String provider;

    /**
     * The unique identifier for the provider.
     */
    private Integer providerId;

    /**
     * The amount of the expense.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal amount;

    /**
     * The type of the expense.
     */
    private String expenseType;

    /**
     * The date of the expense.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;

    /**
     * The unique identifier for the file associated with the expense.
     */
    private String fileId;

    /**
     * The list of expense distributions.
     */
    private List<DtoExpenseDistributionQuery> distributionList;

    /**
     * The list of expense installments.
     */
    private List<DtoExpenseInstallment> installmentList;

    /**
     * The invoice number associated with the expense.
     */
    private String invoiceNumber;
}
