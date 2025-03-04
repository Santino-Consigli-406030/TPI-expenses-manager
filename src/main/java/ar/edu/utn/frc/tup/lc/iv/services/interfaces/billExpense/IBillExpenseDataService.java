package ar.edu.utn.frc.tup.lc.iv.services.interfaces.billExpense;

import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.models.BillExpenseOwnerModel;
import ar.edu.utn.frc.tup.lc.iv.models.BillRecordModel;

import java.util.List;
/**
 * Service class for managing data necessary for BillRecord Service.
 */
public interface IBillExpenseDataService {
    /**
     * Retrieves a bill record for a given period.
     *
     * @param periodDto the period for which to retrieve the bill record
     * @return the bill record for the specified period
     */
    BillRecordModel getBillRecord(PeriodDto periodDto);

    /**
     * Checks if a bill record exists for a given period.
     *
     * @param periodDto the period to check for an existing bill record
     * @return true if a bill record exists for the specified period,
     * false otherwise
     */
    boolean existBillRecordInPeriod(PeriodDto periodDto);

    /**
     * Retrieves a list of bill expense owners constrained by
     * a given period and user.
     *
     * @param periodDto  the period to retrieve the bill expense owners
     * @param createUser the user who created the bill expense owners
     * @return list of bill expense owners for the specified period and user
     */
    List<BillExpenseOwnerModel> constraintBillExpenseOwners(PeriodDto periodDto, Integer createUser);
}
