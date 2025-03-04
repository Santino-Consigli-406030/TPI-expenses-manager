package ar.edu.utn.frc.tup.lc.iv.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Model class for the expense category.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseCategoryModel {
    /**
     * The unique identifier for the expense category.
     */
    private Integer id;

    /**
     * The description of the expense category.
     */
    private String description;

    /**
     * The date and time when the expense category was created.
     */
    private LocalDateTime createdDatetime;

    /**
     * The ID of the user who created the expense category.
     */
    private Integer createdUser;

    /**
     * The date and time when the expense category was last updated.
     */
    private LocalDateTime lastUpdatedDatetime;

    /**
     * The ID of the user who last updated the expense category.
     */
    private Integer lastUpdatedUser;

    /**
     * Indicates whether the expense category is enabled.
     */
    private Boolean enabled;
}
