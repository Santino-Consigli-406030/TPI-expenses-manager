package ar.edu.utn.frc.tup.lc.iv.dtos.common;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * Data transfer object for the response of deleting an expense.
 */
@Data
public class DtoResponseDeleteExpense {
    /**
     * The expense that was deleted.
     */
    private String expense;

    /**
     * The description of the response.
     */
    private String descriptionResponse;

    /**
     * The HTTP status of the response.
     */
    private HttpStatus httpStatus;
}
