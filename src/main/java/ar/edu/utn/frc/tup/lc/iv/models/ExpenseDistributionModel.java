package ar.edu.utn.frc.tup.lc.iv.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model class for the expense distribution.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDistributionModel {
    /**
     * The unique identifier for the expense distribution.
     */
    private Integer id;

    /**
     * The identifier of the owner associated with the expense distribution.
     */
    private Integer ownerId;

    /**
     * The proportion of the expense distribution.
     */
    private BigDecimal proportion;

    /**
     * The date and time when the expense distribution was created.
     */
    private LocalDateTime createdDatetime;

    /**
     * The identifier of the user who created the expense distribution.
     */
    private Integer createdUser;

    /**
     * The date and time when the expense distribution was last updated.
     */
    private LocalDateTime lastUpdatedDatetime;

    /**
     * The identifier of the user who last updated the expense distribution.
     */
    private Integer lastUpdatedUser;

    /**
     * Indicates whether the expense distribution is enabled.
     */
    private Boolean enabled;
}
