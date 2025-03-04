package ar.edu.utn.frc.tup.lc.iv.repositories;

import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for the expense.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Integer> {

    /**
     * Finds the first `ExpenseEntity` by invoice number and provider ID.
     *
     * @param invoiceNumber the invoice number to search for
     * @param providerId    the provider ID to search for
     * @return an `Optional` containing the first matching `ExpenseEntity`,
     * or empty if no match is found
     */
    Optional<ExpenseEntity> findFirstByInvoiceNumberAndProviderId(String invoiceNumber, Integer providerId);

    /**
     * Finds all `ExpenseEntity` records where the payment date of
     * the installments is between the specified dates.
     *
     * @param from the start date
     * @param to   the end date
     * @return a list of `ExpenseEntity` records
     */
    @Query("select e from ExpenseEntity e join e.installmentsList i where i.paymentDate between :from and :to and e.enabled")
    List<ExpenseEntity> findAllByPaymentDate(@Param("from") LocalDate from, @Param("to") LocalDate to);

    /**
     * Finds all enabled ExpenseEntity records where the
     * expense date is between the specified dates.
     *
     * @param from the start date
     * @param to   the end date
     * @return a list of ExpenseEntity records
     */
    @Query("select e from ExpenseEntity e where e.expenseDate between :from and :to and e.enabled")
    List<ExpenseEntity> findAllByDate(@Param("from") LocalDate from, @Param("to") LocalDate to);

    /**
     * Retrieves expenses grouped by year and month.
     * Only enabled expenses are considered.
     *
     * @param start The start year (inclusive).
     * @param end   The end year (inclusive).
     * @return A list of Object arrays.
     */
    @Query(value = "SELECT YEAR(expense_date) AS year, MONTH(expense_date) AS month, SUM(amount) AS amount ,"
            + "expense_type,expenses.provider_id,expenses.expense_category_id "
            + "FROM expenses "
            + "inner join expense_categories on expenses.expense_category_id = expense_categories.id "
            + "WHERE YEAR(expense_date) BETWEEN :start AND :end AND expenses.enabled IS TRUE "
            + "group by YEAR(expense_date),month(expense_date),expense_type,expenses.provider_id,expenses.expense_category_id "
            + "order by 1,2,4,5,6", nativeQuery = true)
    List<Object[]> findAllByPeriodGroupByYearMonth(@Param("start") Integer start, @Param("end") Integer end);


    /**
     * Retrieves a list of expenses grouped by category for a period.
     * Only enabled expenses (expenses.enabled = true) are considered.
     *
     * @param start The start date of the period (inclusive).
     * @param end   The end date of the period (inclusive).
     * @return A list of Object arrays.
     */
    @Query(value = "SELECT expense_categories.description, SUM(expenses.amount) AS amount "
            + "FROM expenses "
            + "INNER JOIN expense_categories ON expenses.expense_category_id = expense_categories.id "
            + "WHERE expense_date BETWEEN :start AND :end AND expenses.enabled IS TRUE and expenses.expense_type != 'individual' "
            + "GROUP BY expense_categories.description "
            + "ORDER BY 1", nativeQuery = true)
    List<Object[]> findAllByPeriodGroupByCategory(@Param("start") LocalDate start, @Param("end") LocalDate end);

    /**
     * Retrieves expenses grouped by type and category for a period.
     * Calculates total amount for each type and category combination.
     *
     * @param start Start date (inclusive)
     * @param end   End date (inclusive)
     * @return List of Object arrays.
     */
    @Query(value = "SELECT "
            + "expenses.expense_type, "
            + "expense_categories.id as category_id, "
            + "expense_categories.description, "
            + "SUM(expenses.amount) as amount, "
            + "expenses.provider_id "
            + "FROM expenses "
            + "INNER JOIN expense_categories ON expenses.expense_category_id = expense_categories.id "
            + "WHERE expense_date BETWEEN :start AND :end "
            + "GROUP BY expenses.expense_type, expense_categories.id,expenses.provider_id "
            + "ORDER BY expenses.expense_type, expense_categories.description",
            nativeQuery = true)
    List<Object[]> findAllByPeriodGroupByTypeAndCategory(@Param("start") LocalDate start, @Param("end") LocalDate end);

}
