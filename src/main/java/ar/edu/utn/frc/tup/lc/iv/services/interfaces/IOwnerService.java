package ar.edu.utn.frc.tup.lc.iv.services.interfaces;

import ar.edu.utn.frc.tup.lc.iv.dtos.owner.OwnerDto;

import java.util.List;
/**
 * Service class for managing Owner RestClient.
 */
public interface IOwnerService {
    /**
     * Retrieves a list of all owners.
     *
     * @return A list of OwnerDto objects.
     */
    List<OwnerDto> getOwners();
    /**
     * Find Owner by user ID.
     *
     * @param id The user ID
     * @return A list of OwnerDto objects.
     */
    OwnerDto getOwnerByUserId(Integer id);
}
