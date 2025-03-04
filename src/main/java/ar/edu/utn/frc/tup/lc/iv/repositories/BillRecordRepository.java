package ar.edu.utn.frc.tup.lc.iv.repositories;

import ar.edu.utn.frc.tup.lc.iv.entities.BillRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for the bill record.
 */
@Repository
public interface BillRecordRepository extends JpaRepository<BillRecordEntity, Integer> {
    /**
     * Finds the first enabled BillRecordEntity
     * by start and end date.
     *
     * @param start the start date,
     * @param end   the end date
     * @return an Optional with the found BillRecordEntity
     */
    Optional<BillRecordEntity> findFirstByStartAndEndAndEnabledTrue(LocalDate start, LocalDate end);

    /**
     * Finds enabled BillRecordEntity where start or end date
     * is between given dates.
     *
     * @param start the start date
     * @param end   the end date
     * @return a List containing the found BillRecordEntity
     */
    @Query("select b from BillRecordEntity b where (b.start between :start and :end or b.end between :start and :end) and b.enabled")
    List<BillRecordEntity> findAnyByStartAndEnd(@Param("start") LocalDate start, @Param("end") LocalDate end);

    /**
     * Finds the first enabled BillRecordEntity
     * ordered by end date in descending order.
     *
     * @return an Optional containing the found BillRecordEntity,
     * or empty if not found
     */
    Optional<BillRecordEntity> findFirstByEnabledTrueOrderByEndDesc();

    /**
     * Finds the total amount of fines for a given bill record ID.
     *
     * @param billRecordId the ID of the bill record
     * @return an array containing the total amount of fines
     */
    @Query(value = "select sum(amount) from bills_expense_fines "
            + "inner join bills_expense_owners on bills_expense_fines.bill_expense_owner_id = bills_expense_owners.id "
            + "where bills_expense_owners.bill_record_id = :billRecordId", nativeQuery = true)
    Object[] findAmountFindByBillRecordId(@Param("billRecordId") Integer billRecordId);

    /**
     * Finds the total amount of pending bills.
     *
     * @return an array containing the total amount of pending bills
     */
    @Query(value = "select sum(expenses.amount/expenses.installments) as amount  "
            + "from expense_installments inner join expenses on expense_installments.expense_id = expenses.id "
            + "where expense_installments.id not in (select expense_installment_id from bills_expense_installments)",
            nativeQuery = true
    )
    Object[] findAmountPendingBill();
}
