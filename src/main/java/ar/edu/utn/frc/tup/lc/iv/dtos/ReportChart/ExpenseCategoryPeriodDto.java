package ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data transfer object for the expense category period.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCategoryPeriodDto {
    /**
     * The category of the expense.
     */
    @JsonProperty("category")
    private String category;

    /**
     * The amount of the expense.
     */
    @JsonProperty("amount")
    private BigDecimal amount;
}
