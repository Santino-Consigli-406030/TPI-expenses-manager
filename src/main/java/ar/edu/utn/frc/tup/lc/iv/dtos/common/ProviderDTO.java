package ar.edu.utn.frc.tup.lc.iv.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for the provider.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProviderDTO {
    /**
     * The unique identifier of the provider.
     */
    private Integer id;

    /**
     * The description of the provider.
     */
    private String description;
}
