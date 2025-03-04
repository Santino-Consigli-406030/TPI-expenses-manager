package ar.edu.utn.frc.tup.lc.iv.services.interfaces.billExpense;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseQuery;

import java.util.Map;
/**
 * Service class for managing installment Bill Record.
 */
public interface IBillExpInstallmentService {
    /**
     * Retrieves the installment and expense type for a given ID.
     *
     * @param id the ID of the installment
     * @return a map containing the installment ID and the corresponding
     * expense type
     */
    Map<Integer, DtoExpenseQuery> getInstallmentAndExpenseType(Integer id);
}
