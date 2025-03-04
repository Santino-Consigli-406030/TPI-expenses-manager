package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.client.SanctionRestClient;
import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.sanction.FineDto;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IFineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Service class for managing Fine RestClient.
 */
@Service
@RequiredArgsConstructor
public class FineService implements IFineService {

    /**
     * REST client for retrieving sanction information.
     */
    private final SanctionRestClient sanctionRestClient;
    /**
     * Retrieves a list of fines for a given period.
     *
     * @param period the period for which to retrieve fines
     * @return a list of {@link FineDto} objects representing the
     * fines for the specified period
     */
    @Override
    public List<FineDto> getFineByPeriod(PeriodDto period) {
        List<FineDto> result = new ArrayList<>();
        ResponseEntity<FineDto[]> response = sanctionRestClient.getFines(period);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new CustomException("Could not retrieve fines", HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (response.getBody() != null) {
            result = Arrays.asList(response.getBody());
        }
        return result;
    }
}
