package ar.edu.utn.frc.tup.lc.iv.client;


import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.sanction.FineDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * Client for interacting with the Sanction API.
 */
@Service
@RequiredArgsConstructor
public class SanctionRestClient {
    /**
     * The RestTemplate used to make HTTP requests.
     */
    private final RestTemplate restTemplate;

    /**
     * The root URL for the sanction API.
     */
    @Value("${app.api-sanction}")
    private String rootUrl;
    /**
     * Retrieves the fines for a given period.
     *
     * @param periodDto the period for which to retrieve fines
     * @return a ResponseEntity containing an array of FineDto objects
     */
    public ResponseEntity<FineDto[]> getFines(PeriodDto periodDto) {
        String url = rootUrl + "/api/sanction/fines?start_date={startDate}&end_date={endDate}";
        String startDate = periodDto.getStartDate().toString();
        String endDate = periodDto.getEndDate().toString();
        return restTemplate.getForEntity(url, FineDto[].class, startDate, endDate);
    }
}
