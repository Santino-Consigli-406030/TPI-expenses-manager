package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.client.ProviderRestClient;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.ProviderDTO;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Service class for managing Provider RestClient.
 */
@Service
@RequiredArgsConstructor
public class ProviderService implements IProviderService {
    /**
     * REST client for retrieving providers information.
     */
    private final ProviderRestClient providerRestClient;
    /**
     * Retrieves a list of all providers.
     *
     * @return A list of ProviderDTO objects.
     */
    @Override
    public List<ProviderDTO> getProviders() {
        List<ProviderDTO> result = new ArrayList<>();
        ResponseEntity<ProviderDTO[]> response = providerRestClient.getAllProviders();
        if (response.getStatusCode() != HttpStatus.OK) {
            System.out.println(response);
        }
        if (response.getBody() != null) {
            result = Arrays.asList(response.getBody());
        }
        return result;
    }
}
