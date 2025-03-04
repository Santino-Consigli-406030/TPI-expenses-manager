package ar.edu.utn.frc.tup.lc.iv.services.interfaces;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseQuery;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoRequestExpense;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoResponseDeleteExpense;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoResponseExpense;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;


/**
 * Service class for managing expenses.
 */
@Service
public interface IExpenseService {
    /**
     * Retrieves an expense by its ID.
     *
     * @param expenseId the ID of the expense
     * @return the expense details
     */
    DtoExpenseQuery getExpenseById(Integer expenseId);

    /**
     * Retrieves a list of expenses based on the provided filters.
     *
     * @param dateFrom    the start date for the expense search
     * @param dateTo      the end date for the expense search
     * @return a list of expenses matching the filters
     */
    List<DtoExpenseQuery> getExpenses(String dateFrom, String dateTo);

    /**
     * Creates a new expense.
     *
     * @param request the expense request details
     * @param file    the file associated with the expense
     * @param userId  The User ID
     * @return the response entity containing the created expense details
     */
    ResponseEntity<DtoResponseExpense> postExpense(DtoRequestExpense request, MultipartFile file, Integer userId);

    /**
     * Deletes an expense by its ID.
     *
     * @param id the ID of the expense to delete
     * @param userId  The User ID
     * @return the response entity containing the delete status
     */
    DtoResponseDeleteExpense deteleExpense(Integer id, Integer userId);

    /**
     * Creates a credit note for an existing expense.
     *
     * @param id the ID of the expense
     * @param userId  The User ID
     * @return the response entity containing the credit note details
     */
    DtoResponseExpense createCreditNoteForExpense(Integer id, Integer userId);

    /**
     * Retrieves a list of expenses within a specified payment date range.
     *
     * @param startDate the start date of the payment date range
     * @param endDate   the end date of the payment date range
     * @return a list of expenses within the specified date range
     */
    List<ExpenseModel> getExpenseByPaymentDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Updates an existing expense.
     *
     * @param request the expense request details
     * @param file    the file associated with the expense
     * @param userId  The User ID
     * @return the response entity containing the updated expense details
     */
    DtoResponseExpense putExpense(DtoRequestExpense request, MultipartFile file, Integer userId);
}
