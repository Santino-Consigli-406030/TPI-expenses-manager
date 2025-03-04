package ar.edu.utn.frc.tup.lc.iv.client;


import ar.edu.utn.frc.tup.lc.iv.dtos.owner.OwnerDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.owner.PlotDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


/**
 * Client for interacting with the Owner API.
 */
@Service
@RequiredArgsConstructor
public class OwnerRestClient {
    /**
     * The RestTemplate used for making HTTP requests.
     */
    private final RestTemplate restTemplate;
    /**
     * Number ten.
     */
    private static final int TEN = 10;
    /**
     * The root URL for the Owner API.
     */
    @Value("${app.api-owner}")
    private String rootUrl;
    /**
     * Retrieves a list of owners and their associated plots from the Owner API.
     *
     * @return {@link ResponseEntity} containing an array of {@link OwnerDto} objects.
     */
    public ResponseEntity<OwnerDto[]> getOwnerPlot() {
        String url = rootUrl + "/owners/ownersandplots";

        // Fetch the raw data from the API
        try {
            List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

            // Map the raw data to OwnerDto[]
            OwnerDto[] ownerDtos = response.stream()
                    .map(this::mapToOwnerDto)
                    .toArray(OwnerDto[]::new);

            return ResponseEntity.ok(ownerDtos);
        } catch (RestClientException e) {
            System.out.println(e);
            return ResponseEntity.ok(new OwnerDto[0]);

        }

    }

    /**
     * Maps a single API response entry to {@link OwnerDto}.
     *
     * @param data The raw map of data from the API.
     * @return The mapped {@link OwnerDto}.
     */
    private OwnerDto mapToOwnerDto(Map<String, Object> data) {
        Map<String, Object> owner = (Map<String, Object>) data.get("owner");
        Integer ownerId = (Integer) owner.get("id");
        String name = (String) owner.get("name");
        String lastName = (String) owner.get("lastname");
        String dni = (String) owner.get("dni");

        List<Map<String, Object>> plots = (List<Map<String, Object>>) data.get("plot");
        List<PlotDto> plotDtos = plots.stream()
                .map(plot -> new PlotDto(
                        (Integer) plot.get("id"),
                        ((Double) plot.get("total_area_in_m2")).intValue()
                ))
                .toList();

        Map<String, Object> user = (Map<String, Object>) data.get("user");
        Integer userId = (Integer) user.get("id");

        return new OwnerDto(ownerId, name, lastName, dni, plotDtos, userId);
    }
}
