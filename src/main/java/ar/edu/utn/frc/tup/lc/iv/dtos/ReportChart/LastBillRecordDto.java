package ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart;

import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseKPI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data transfer object for the last bill record.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class LastBillRecordDto extends PeriodDto {
    /**
     * The unique identifier of the last bill record.
     */
    private Integer id;

    /**
     * The list of expense KPIs associated with the last bill record.
     */
    private List<DtoExpenseKPI> bills;

    /**
     * The fine amount associated with the last bill record.
     */
    private BigDecimal fineAmount;

    /**
     * The pending amount associated with the last bill record.
     */
    private BigDecimal pendingAmount;

}
