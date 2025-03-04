package ar.edu.utn.frc.tup.lc.iv.repositories;

import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseInstallmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the expense installment.
 */
@Repository
public interface ExpenseInstallmentRepository extends JpaRepository<ExpenseInstallmentEntity, Integer> {
}
