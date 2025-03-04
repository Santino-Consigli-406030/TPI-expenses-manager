package ar.edu.utn.frc.tup.lc.iv.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Base class for entities that require audit fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class AuditEntity {

    /**
     * The date and time when the entity was created.
     * This field is not insertable or updatable.
     */
    @Column(name = "created_datetime", insertable = false, updatable = false)
    private LocalDateTime createdDatetime;

    /**
     * The user who created the entity.
     * This field is insertable but not updatable.
     */
    @Column(name = "created_user", insertable = true, updatable = false)
    private Integer createdUser;

    /**
     * The date and time when the entity was last updated.
     * This field is not insertable or updatable.
     */
    @Column(name = "last_updated_datetime", insertable = false, updatable = false)
    private LocalDateTime lastUpdatedDatetime;

    /**
     * The user who last updated the entity.
     * This field is insertable and updatable.
     */
    @Column(name = "last_updated_user", insertable = true, updatable = true)
    private Integer lastUpdatedUser;

    /**
     * Indicates whether the entity is enabled.
     * This field is not insertable but is updatable.
     */
    @Column(name = "enabled", insertable = false, updatable = true)
    private Boolean enabled;
}
