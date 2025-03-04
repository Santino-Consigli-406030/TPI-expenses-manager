package ar.edu.utn.frc.tup.lc.iv.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for expense categories.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseCategoryDTO {
    /**
     * The ID of the expense category.
     */
    private Integer id;

    /**
     * The description of the expense category.
     */
    private String description;
}
