package ar.edu.utn.frc.tup.lc.iv.services.impl.billExpense;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.billExpense.IPeriodBillExpenseValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Service manager validation of period for BillRecord.
 */
@Service
@RequiredArgsConstructor
public class PeriodBillExpenseValidation implements IPeriodBillExpenseValidation {
    /**
     * Validates the period provided in the {@link PeriodDto}.
     * - The start date must be earlier than the end date.
     * - Both start and end dates must be in the past (before today).
     * - The start date and end date cannot be the same.
     * - The end date cannot be today's date.
     *
     * @param periodDto The {@link PeriodDto} containing the start and end dates.
     * @throws CustomException if any validation condition is not met.
     */
    @Override
    public void validatePeriod(PeriodDto periodDto) {
        LocalDate today = LocalDate.now();
        // Validate that the period is not null
        if (periodDto == null) {
            throw new CustomException("The period be can't null", HttpStatus.BAD_REQUEST);
        }
        // Validate that the start date is not null
        if (periodDto.getStartDate() == null) {
            throw new CustomException("The start date be can't null", HttpStatus.BAD_REQUEST);
        }
        if (periodDto.getEndDate() == null) {
            throw new CustomException("The end date be can't null", HttpStatus.BAD_REQUEST);
        }
        // Validate that the start date is before the end date
        if (periodDto.getStartDate().isAfter(periodDto.getEndDate())) {
            throw new CustomException("The start date must be earlier than the end date.", HttpStatus.BAD_REQUEST);
        }

        // Validate that the start date is in the past
        if (!periodDto.getStartDate().isBefore(today)) {
            throw new CustomException("The start date must be in the past.", HttpStatus.BAD_REQUEST);
        }

        // Validate that the end date is in the past and not today
        if (!periodDto.getEndDate().isBefore(today)) {
            throw new CustomException("The end date must be before today.", HttpStatus.BAD_REQUEST);
        }

        // Validate that the start date and end date are not the same
        if (periodDto.getStartDate().isEqual(periodDto.getEndDate())) {
            throw new CustomException("The start date and end date cannot be the same.", HttpStatus.BAD_REQUEST);
        }
    }
}
