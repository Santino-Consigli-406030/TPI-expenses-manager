package ar.edu.utn.frc.tup.lc.iv.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Model class for the bill record.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillRecordModel extends AuditModel {
    /**
     * The start date of the bill record.
     */
    private LocalDate start;

    /**
     * The end date of the bill record.
     */
    private LocalDate end;

    /**
     * The list of bill expense owners associated with the bill record.
     */
    private List<BillExpenseOwnerModel> billExpenseOwner;
}
