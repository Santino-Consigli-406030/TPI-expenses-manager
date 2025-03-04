package ar.edu.utn.frc.tup.lc.iv.services.interfaces.billExpense;

import ar.edu.utn.frc.tup.lc.iv.models.BillRecordModel;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseModel;
/**
 * Service class for managing distribution for BillRecord.
 */
public interface IBillExpenseDistributionService {
    /**
     * Distributes a non-individual expense.
     *
     * @param billRecordModel the bill record model
     * @param expenseModel    the expense model
     * @param totalSize       the total size
     * @param createUser      the user who created the record
     */
    void distributeNonIndividualExpense(BillRecordModel billRecordModel, ExpenseModel expenseModel,
                                        Integer totalSize, Integer createUser);

    /**
     * Distributes an individual expense.
     *
     * @param billRecordModel the bill record model
     * @param expenseModel    the expense model
     * @param createUser      the user who created the record
     */
    void distributeIndividualExpense(BillRecordModel billRecordModel, ExpenseModel expenseModel,
                                     Integer createUser);
}
