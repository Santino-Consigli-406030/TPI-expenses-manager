package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.ExpenseOwnerVisualizerDTO;
import ar.edu.utn.frc.tup.lc.iv.services.impl.ExpenseDistributionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for handling expense distributions.
 */
@RestController
@RequestMapping("/api/expenses/distributions")
@RequiredArgsConstructor
public class ExpenseDistributionController {
    /**
     * Service for handling expense distribution operations.
     */

    private final ExpenseDistributionService expenseDistributionService;

    /**
     * Retrieves all expenses by owner ID within a specified date range.
     *
     * @param id        the ID of the User
     * @param startDate the start date in format yyyy-MM-dd
     * @param endDate   the end date in format yyyy-MM-dd
     * @return a ResponseEntity containing a list of ExpenseOwnerVisualizerDTO
     */
    @Operation(summary = "Get ALL Expenses by Id",
            description = "Get all expenses by Id")
    @ApiResponse(responseCode = "200", description = "get all successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExpenseOwnerVisualizerDTO.class)))
    @GetMapping("/getAllByOwnerId")
    public ResponseEntity<List<ExpenseOwnerVisualizerDTO>> getAllExpensesById(
            @Parameter(description = "ID of the User") Integer id,
            @Parameter(description = "Start date in format yyyy-MM-dd") String startDate,
            @Parameter(description = "End date in format yyyy-MM-dd") String endDate) {
        List<ExpenseOwnerVisualizerDTO> list = expenseDistributionService.findByOwnerId(id, startDate, endDate);
        return ResponseEntity.ok(list);
    }
}
