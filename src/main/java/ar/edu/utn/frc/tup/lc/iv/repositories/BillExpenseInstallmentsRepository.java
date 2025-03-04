package ar.edu.utn.frc.tup.lc.iv.repositories;

import ar.edu.utn.frc.tup.lc.iv.entities.BillExpenseInstallmentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for the bill expense installments.
 */
public interface BillExpenseInstallmentsRepository extends JpaRepository<BillExpenseInstallmentsEntity, Integer> {

    /**
     * Finds installment ID and expense type by bill record ID.
     *
     * @param billRecordId the ID of the bill record
     * @return a list of objects containing installment ID,
     * expense type, and description
     */
    @Query(value = "SELECT bei.id, e.expense_type, ec.description "
            + "FROM bills_record br "
            + "INNER JOIN bills_expense_owners beo ON br.id = beo.bill_record_id "
            + "INNER JOIN bills_expense_installments bei ON beo.id = bei.bill_expense_owner_id "
            + "INNER JOIN expense_installments ei ON bei.expense_installment_id = ei.id "
            + "INNER JOIN expenses e ON e.id = ei.expense_id "
            + "INNER JOIN expense_categories ec on e.expense_category_id = ec.id "
            + "WHERE br.id = :billRecordId", nativeQuery = true)
    List<Object[]> findInstallmentIdAndExpenseTypeByBillRecordId(@Param("billRecordId") Integer billRecordId);

    /**
     * Finds BillExpenseInstallmentsEntity by expense ID.
     *
     * @param expenseId the ID of the expense
     * @return an optional list of BillExpenseInstallmentsEntity
     */
    @Query("SELECT bei FROM BillExpenseInstallmentsEntity bei "
            + "JOIN bei.expenseInstallment ei "
            + "WHERE ei.expense.id = :expenseId")
    Optional<List<BillExpenseInstallmentsEntity>> findByExpenseId(@Param("expenseId") Integer expenseId);

    /**
     * Finds expense type, category ID, category description,
     * sum of amounts, and provider ID by bill ID.
     *
     * @param billId the ID of the bill
     * @return a list of objects with the details
     */
    @Query(value = "select expenses.expense_type,expense_categories.id as category_id,"
            + "expense_categories.description,sum(bills_expense_installments.amount),expenses.provider_id "
            + "from bills_expense_installments "
            + "inner join bills_expense_owners on bills_expense_installments.bill_expense_owner_id = bills_expense_owners.id "
            + "inner join expense_installments on bills_expense_installments.expense_installment_id  =  expense_installments.id "
            + "inner join expenses on expense_installments.expense_id = expenses.id "
            + "inner join expense_categories on expenses.expense_category_id = expense_categories.id "
            + "where bills_expense_owners.bill_record_id = :billId "
            + "group by expenses.expense_type,expense_categories.id , expenses.provider_id", nativeQuery = true)
    List<Object[]> findByBillIdGroupByTypeAndCategory(@Param("billId") Integer billId);
}
