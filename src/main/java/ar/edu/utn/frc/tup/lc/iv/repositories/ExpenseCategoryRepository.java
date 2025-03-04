package ar.edu.utn.frc.tup.lc.iv.repositories;

import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for the expense category.
 */
@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategoryEntity, Integer> {

    /**
     * Finds all enabled expense categories.
     *
     * @return a list of enabled expense categories
     */
    @Query("select e from ExpenseCategoryEntity e where e.enabled")
    List<ExpenseCategoryEntity> findAllEnabled();

    /**
     * Finds an expense category by its description.
     *
     * @param description the description of the expense category
     * @return an optional containing the found expense category,
     * or empty if not found
     */
    Optional<ExpenseCategoryEntity> findFirstByDescription(String description);
}
