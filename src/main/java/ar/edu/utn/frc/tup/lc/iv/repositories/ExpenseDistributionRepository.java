package ar.edu.utn.frc.tup.lc.iv.repositories;

import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseDistributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for the expense distribution.
 */
@Repository
public interface ExpenseDistributionRepository extends JpaRepository<ExpenseDistributionEntity, Integer> {
    /**
     * Retrieves all distinct expense distribution entities.
     *
     * @return a list of distinct ExpenseDistributionEntity objects
     */
    @Query("SELECT DISTINCT e FROM ExpenseDistributionEntity e")
    List<ExpenseDistributionEntity> findAllDistinct();

    /**
     * Retrieves all expense distribution entities by the owner ID.
     *
     * @param ownerId the ID of the owner
     * @return a list of ExpenseDistributionEntity objects associated
     * with the given owner ID
     */
    List<ExpenseDistributionEntity> findAllByOwnerId(Integer ownerId);

}
