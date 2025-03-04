package ar.edu.utn.frc.tup.lc.iv.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model class for the bill expense owner.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class BillExpenseOwnerModel extends AuditModel {
    /**
     * The ID of the owner.
     */
    private Integer ownerId;

    /**
     * The size of the field.
     */
    private Integer fieldSize;

    /**
     * The list of bill expense fines associated with this bill expense owner.
     */
    private List<BillExpenseFineModel> billExpenseFines;

    /**
     * The list of bill expense installments associated with this bill expense owner.
     */
    private List<BillExpenseInstallmentModel> billExpenseInstallments;
}
