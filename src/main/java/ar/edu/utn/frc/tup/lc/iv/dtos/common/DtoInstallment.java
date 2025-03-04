package ar.edu.utn.frc.tup.lc.iv.dtos.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data transfer object for installments.
 */
@Data
public class DtoInstallment {
    /**
     * The number of the installment.
     */
    private Integer installmentNumber;

    /**
     * The date of the payment for the installment.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;
}
