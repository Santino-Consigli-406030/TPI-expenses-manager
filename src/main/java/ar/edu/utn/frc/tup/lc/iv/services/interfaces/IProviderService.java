package ar.edu.utn.frc.tup.lc.iv.services.interfaces;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.ProviderDTO;

import java.util.List;

/**
 * Service class for managing Provider RestClient.
 */
public interface IProviderService {
    /**
     * Retrieves a list of all providers.
     *
     * @return A list of ProviderDTO objects.
     */
    List<ProviderDTO> getProviders();
}
