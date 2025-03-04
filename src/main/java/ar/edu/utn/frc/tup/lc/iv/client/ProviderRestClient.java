package ar.edu.utn.frc.tup.lc.iv.client;


import ar.edu.utn.frc.tup.lc.iv.dtos.common.ProviderDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Client for interacting with the Provider API.
 */
@Service
public class ProviderRestClient {
    /**
     * RestTemplate instance used for making REST API calls.
     */
    private final RestTemplate restTemplate;

    /**
     * Root URL for the API provider, injected from application properties.
     */
    private final String rootUrl;

    /**
     * Number five.
     */
    private static final int FIVE = 5;
    /**
     * Constructs a new ProviderRestClient with the specified
     * RestTemplate and root URL for the Provider API.
     *
     * @param paramRestTemplate the RestTemplate used for making API calls
     * @param paramRootUrl the root URL of the Provider API,
     * injected from application properties
     */
    public ProviderRestClient(RestTemplate paramRestTemplate, @Value("${app.api-provider}") String paramRootUrl) {
        this.restTemplate = paramRestTemplate;
        this.rootUrl = paramRootUrl;

    }
    /**
     * Retrieves all providers from the API.
     *
     * @return a list of {@link ProviderDTO} objects representing the providers.
     */
    public ResponseEntity<ProviderDTO[]> getAllProviders() {
        String url = rootUrl + "/suppliers";
        try {
            List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
            ProviderDTO[] providerDtos = response.stream()
                    .map(this::mapToProviderDTO)
                    .toArray(ProviderDTO[]::new);

            return ResponseEntity.ok(providerDtos);
        } catch (RestClientException e) {
            System.out.println(e);
            return ResponseEntity.ok(new ProviderDTO[0]);
        }

    }
    /**
     * Maps a data structure representing a provider (as a Map)
     * to a {@link ProviderDTO} object.
     *
     * @param data a {@link Map} containing the provider's data, where
     * keys represent field names and values.
     * @return a {@link ProviderDTO} containing the mapped data.
     * @throws ClassCastException if the data map contains values
     * that cannot be cast to their expected types.
     */
    private ProviderDTO mapToProviderDTO(Map<String, Object> data) {
        Integer id = (Integer) data.get("id");
        String description = (String) data.get("name");
        return new ProviderDTO(id, description);
    }

}
