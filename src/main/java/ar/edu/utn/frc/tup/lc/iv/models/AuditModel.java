package ar.edu.utn.frc.tup.lc.iv.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Abstract class for the audit model.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class AuditModel {
    /**
     * The unique identifier for the entity.
     */
    private Integer id;

    /**
     * The date and time when the entity was created.
     */
    private LocalDateTime createdDatetime;

    /**
     * The user who created the entity.
     */
    private Integer createdUser;

    /**
     * The date and time when the entity was last updated.
     */
    private LocalDateTime lastUpdatedDatetime;

    /**
     * The user who last updated the entity.
     */
    private Integer lastUpdatedUser;

    /**
     * Indicates whether the entity is enabled.
     */
    private Boolean enabled;
}
