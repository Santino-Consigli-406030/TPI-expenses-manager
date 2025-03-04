package ar.edu.utn.frc.tup.lc.iv.dtos.common;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Data transfer object for distributions.
 */
@Data
public class DtoDistribution {
    /**
     * The unique identifier for the owner.
     */
    private Integer ownerId;

    /**
     * The proportion value.
     */
    private BigDecimal proportion;
}
