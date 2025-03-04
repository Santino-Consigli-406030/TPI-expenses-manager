package ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data transfer object for the expense year.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseYearDto {
    /**
     * The year of the expense.
     */
    @JsonProperty("year")
    private Integer year;

    /**
     * The month of the expense.
     */
    @JsonProperty("month")
    private Integer month;

    /**
     * The amount of the expense.
     */
    @JsonProperty("amount")
    private BigDecimal amount;

    /**
     * The type of the expense.
     */
    @JsonProperty("expense_type")
    private String expenseType;

    /**
     * The identifier of the provider associated with the expense.
     */
    @JsonProperty("providerId")
    private Integer providerId;

    /**
     * The identifier of the category associated with the expense.
     */
    @JsonProperty("categoryId")
    private Integer categoryId;
}
