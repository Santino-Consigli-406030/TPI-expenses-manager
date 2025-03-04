package ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.response;


import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data transfer object for bill expenses.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class BillExpenseDto extends PeriodDto {
    /**
     * The unique identifier for the bill expense.
     */
    @JsonProperty("bill_expense_id")
    private Integer id;

    /**
     * The list of owners associated with the bill expense.
     */
    @JsonProperty("owners")
    private List<BillOwnerDto> owners;
}
