package ar.edu.utn.frc.tup.lc.iv.services.interfaces.billExpense;

import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.response.BillExpenseDto;

/**
 * Service class for managing bill expenses.
 */
public interface IBillExpenseService {
    /**
     * Generates a bill expense report for the given period.
     *
     * @param periodDto the period for which the bill expense report
     *                  is to be generated
     * @return the generated bill expense report
     */
    BillExpenseDto generateBillExpense(PeriodDto periodDto);
}
