package ar.edu.utn.frc.tup.lc.iv.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Data transfer object for expense installments.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoExpenseInstallment {
    /**
     * The date when the payment is made.
     */
    private LocalDate paymentDate;

    /**
     * The number of the installment.
     */
    private Integer installmentNumber;
}
