package ar.edu.utn.frc.tup.lc.iv.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model class for the expense installment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseInstallmentModel {
    /**
     * The unique identifier for the expense installment.
     */
    private Integer id;

    /**
     * The payment date of the expense installment.
     */
    private LocalDate paymentDate;

    /**
     * The installment number of the expense installment.
     */
    private Integer installmentNumber;

    /**
     * The date and time when the expense installment was created.
     */
    private LocalDateTime createdDatetime;

    /**
     * The identifier of the user who created the expense installment.
     */
    private Integer createdUser;

    /**
     * The date and time when the expense installment was last updated.
     */
    private LocalDateTime lastUpdatedDatetime;

    /**
     * The identifier of the user who last updated the expense installment.
     */
    private Integer lastUpdatedUser;

    /**
     * Indicates whether the expense installment is enabled.
     */
    private Boolean enabled;
}
