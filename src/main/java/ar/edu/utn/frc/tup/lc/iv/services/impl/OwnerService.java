package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.client.OwnerRestClient;
import ar.edu.utn.frc.tup.lc.iv.dtos.owner.OwnerDto;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Service class for managing Owner RestClient.
 */
@Service
@RequiredArgsConstructor
public class OwnerService implements IOwnerService {
    /**
     * REST client for retrieving owner information.
     */
    private final OwnerRestClient ownerRestClient;
    /**
     * Retrieves a list of all owners.
     *
     * @return A list of OwnerDto objects.
     */
    @Override
    public List<OwnerDto> getOwners() {
        List<OwnerDto> result = new ArrayList<>();
        ResponseEntity<OwnerDto[]> response = ownerRestClient.getOwnerPlot();
        if (response.getStatusCode() != HttpStatus.OK) {
            System.out.println(response);
        }
        if (response.getBody() != null) {
            result = Arrays.asList(response.getBody());
        }
        return result;
    }

    /**
     * Find Owner by user ID.
     *
     * @param id The user ID
     * @return A list of OwnerDto objects.
     */
    @Override
    public OwnerDto getOwnerByUserId(Integer id) {
        return getOwners().stream().filter(owner -> owner.getUserId().equals(id)).findFirst().orElse(null);
    }
}
