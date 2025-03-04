package ar.edu.utn.frc.tup.lc.iv.services.interfaces;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoCategory;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseCategoryModel;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing expense categories.
 */
@Service
public interface IExpenseCategoryService {
    /**
     * Creates a new expense category with the given description.
     *
     * @param description The description of the new category.
     * @param userId The User ID.
     * @return The created DtoCategory object.
     */
    DtoCategory postCategory(String description, Integer userId);

    /**
     * Retrieves the expense category model by its ID.
     *
     * @param id The ID of the category to retrieve.
     * @return The ExpenseCategoryModel object.
     */
    ExpenseCategoryModel getCategoryModel(Integer id);

    /**
     * Retrieves all expense categories.
     *
     * @return A list of DtoCategory objects.
     */
    List<DtoCategory> getAllCategories();

    /**
     * Updates the description and enabled status of an expense category.
     *
     * @param id          The ID of the category to update.
     * @param description The new description of the category.
     * @param enabled     The new enabled status of the category.
     * @param userId The User ID.
     * @return The updated DtoCategory object.
     */
    DtoCategory putCategory(Integer id, String description, Boolean enabled, Integer userId);

    /**
     * Retrieves the expense category by its ID.
     *
     * @param id The ID of the category to retrieve.
     * @return The DtoCategory object.
     */
    DtoCategory getCategoryById(Integer id);

}
