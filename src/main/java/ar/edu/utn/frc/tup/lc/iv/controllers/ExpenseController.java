package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseQuery;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoRequestExpense;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoResponseDeleteExpense;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoResponseExpense;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for handling expenses.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {

    /**
     * Service for handling expense-related operations.
     */
    private final IExpenseService expenseService;
    /**
     * HTTP status code for a successful operation.
     */
    private static final String STATUS_OK = "200";

    /**
     * Media type for JSON content.
     */
    private static final String APLICATION_JSON = "application/json";

    /**
     * Creates a new expense with the given details and optional file.
     *
     * @param request the details of the expense to create
     * @param file    the optional file associated with the expense
     * @param userId The User ID
     * @return a ResponseEntity containing the created expense details
     */
    @Operation(summary = "Create a new expense",
            description = "Creates a new expense with the given details and optional file")
    @ApiResponse(responseCode = STATUS_OK, description = "Expense created successfully",
            content = @Content(mediaType = APLICATION_JSON,
                    schema = @Schema(implementation = DtoResponseExpense.class)))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DtoResponseExpense> postExpense(
            @RequestPart("expense") DtoRequestExpense request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("userId") Integer userId) {
        return expenseService.postExpense(request, file, userId);
    }

    /**
     * Edits an existing expense with the given details and optional file.
     *
     * @param request the details of the expense to edit
     * @param file    the optional file associated with the expense
     * @param userId The User ID
     * @return the updated expense details
     */
    @Operation(summary = "Edit a expense",
            description = "Edit a expense with the given details and optional file")
    @ApiResponse(responseCode = STATUS_OK, description = "Expense updated successfully",
            content = @Content(mediaType = APLICATION_JSON,
                    schema = @Schema(implementation = DtoResponseExpense.class)))
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DtoResponseExpense putExpense(
            @RequestPart("expense") DtoRequestExpense request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("userId") Integer userId) {
        return expenseService.putExpense(request, file, userId);
    }

    /**
     * Deletes an expense by its ID using logical deletion or
     * throws an exception if not found.
     *
     * @param id the ID of the expense to delete
     * @param userId The User ID
     * @return a DtoResponseDeleteExpense object containing
     * the details of the deleted expense
     */
    @Operation(summary = "Delete logic or expense")
    @ApiResponse(responseCode = "204", description = "Expense delete logic successfully",
            content = @Content(mediaType = APLICATION_JSON))
    @DeleteMapping()
    public DtoResponseDeleteExpense deleteExpenseByIdLogicOrThrowException(@RequestParam Integer id,
                                                                           @RequestParam Integer userId) {
        return expenseService.deteleExpense(id, userId);
    }

    /**
     * Creates a note of credit for the specified expense.
     *
     * @param id the ID of the expense for which to create a note of credit
     * @param userId The User ID
     * @return a DtoResponseExpense object containing the details of the
     * created note of credit
     */
    @Operation(summary = "Create note of credit")
    @ApiResponse(responseCode = "204", description = "Note of credit created successfully",
            content = @Content(mediaType = APLICATION_JSON))
    @DeleteMapping("/note_credit")
    public DtoResponseExpense createNoteOfCredit(@RequestParam Integer id, @RequestParam Integer userId) {
        return expenseService.createCreditNoteForExpense(id, userId);
    }

    /**
     * Retrieves an expense by its ID.
     *
     * @param expenseId the ID of the expense to retrieve (required)
     * @return a ResponseEntity containing the DtoExpenseQuery object
     * for the specified expense
     */
    @GetMapping("/getById")
    @Operation(summary = "Get expense by id")
    @ApiResponse(responseCode = STATUS_OK, description = "Expenses retrieved successfully",
            content = @Content(mediaType = APLICATION_JSON,
                    schema = @Schema(implementation = DtoExpenseQuery.class)))
    public ResponseEntity<DtoExpenseQuery> getExpenseById(@RequestParam(required = true) int expenseId) {
        return ResponseEntity.ok(expenseService.getExpenseById(expenseId));
    }

    /**
     * Retrieves a list of expenses filtered by parameters.
     *
     * @param dateFrom    start date (required, format: YYYY-MM-DD)
     * @param dateTo      end date (required, format: YYYY-MM-DD)
     * @return ResponseEntity with a list of DtoExpenseQuery objects
     */
    @GetMapping("/getByFilters")
    @Operation(summary = "Get expenses by filters")
    @ApiResponse(responseCode = STATUS_OK, description = "Expenses retrieved successfully",
            content = @Content(mediaType = APLICATION_JSON,
                    schema = @Schema(implementation = DtoExpenseQuery.class)))
    public ResponseEntity<List<DtoExpenseQuery>> getExpenses(
            @RequestParam(required = true) String dateFrom,
            @RequestParam(required = true) String dateTo) {
        return ResponseEntity.ok(expenseService.getExpenses(dateFrom, dateTo));
    }

}
