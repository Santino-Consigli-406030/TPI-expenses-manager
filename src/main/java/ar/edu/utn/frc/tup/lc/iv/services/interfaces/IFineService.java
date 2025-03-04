package ar.edu.utn.frc.tup.lc.iv.services.interfaces;

import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.sanction.FineDto;

import java.util.List;
/**
 * Service class for managing Fine RestClient.
 */
public interface IFineService {
    /**
     * Retrieves a list of fines for a given period.
     *
     * @param period The period for which to retrieve fines.
     * @return A list of FineDto objects.
     */
    List<FineDto> getFineByPeriod(PeriodDto period);
}
