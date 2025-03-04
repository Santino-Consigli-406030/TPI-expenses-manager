package ar.edu.utn.frc.tup.lc.iv.dtos.common;

import ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Data transfer object for expense responses.
 */
@Data
@Schema(description = "Expense Response Data")
public class DtoResponseExpense {
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;

    /**
     * The unique identifier for the file associated with the expense.
     */
    private UUID fileId;

    /**
     * The invoice number associated with the expense.
     */
    private String invoiceNumber;

    /**
     * The type of the expense.
     */
    private ExpenseType expenseType;

    /**
     * The category of the expense.
     */
    private DtoCategory dtoCategory;

    /**
     * The list of expense distributions.
     */
    private List<DtoDistribution> dtoDistributionList;

    /**
     * The list of installments for the expense.
     */
    private List<DtoInstallment> dtoInstallmentList;
}
