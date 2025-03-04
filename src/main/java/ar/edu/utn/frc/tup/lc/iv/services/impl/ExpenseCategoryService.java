package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoCategory;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseCategoryEntity;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseCategoryModel;
import ar.edu.utn.frc.tup.lc.iv.repositories.ExpenseCategoryRepository;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IExpenseCategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing expense categories.
 */

@Service
@RequiredArgsConstructor
public class ExpenseCategoryService implements IExpenseCategoryService {
    /**
     * Repository for accessing expense category data.
     */
    private final ExpenseCategoryRepository expenseCategoryRepository;

    /**
     * Mapper for converting between entity and DTO objects.
     */
    private final ModelMapper modelMapper;

    /**
     * Status indicating that the category is active.
     */
    private static final String ACTIVO = "Activo";
    /**
     * Status indicating that the category is inactive.
     */
    private static final String INACTIVO = "Inactivo";

    /**
     * Creates a new expense category if one with the specified description
     * does not exist. Throws CustomException if an enabled category exist.
     *
     * @param description The description of the new category.
     * @param userId The User ID.
     * @return the created {@link DtoCategory}.
     * @throws CustomException if a category already exists.
     */
    @Override
    public DtoCategory postCategory(String description, Integer userId) {
        validateUserId(userId);
        if (description == null || description.isBlank()) {
            throw new CustomException("The description is required", HttpStatus.BAD_REQUEST);
        }
        Optional<ExpenseCategoryEntity> existingCategory = expenseCategoryRepository.findFirstByDescription(description);

        if (existingCategory.isPresent()) {
            throw new CustomException("A category with this description already exists", HttpStatus.CONFLICT);
        }

        ExpenseCategoryEntity newCategory = new ExpenseCategoryEntity();

        newCategory.setDescription(description);
        newCategory.setEnabled(true);
        newCategory.setCreatedDatetime(LocalDateTime.now());
        newCategory.setCreatedUser(userId);
        newCategory.setLastUpdatedDatetime(LocalDateTime.now());
        newCategory.setLastUpdatedUser(userId);
        expenseCategoryRepository.save(newCategory);

        return DtoCategory.builder()
                .id(newCategory.getId())
                .lastUpdatedDatetime(newCategory.getLastUpdatedDatetime())
                .state(newCategory.getEnabled() ? ACTIVO : INACTIVO)
                .description(description)
                .build();
    }


    /**
     * Retrieves an {@link ExpenseCategoryModel} based on the category ID.
     *
     * @param id the ID of the expense category to retrieve.
     * @return the corresponding {@link ExpenseCategoryModel},
     * or {@code null} if not found.
     */
    @Override
    public ExpenseCategoryModel getCategoryModel(Integer id) {
        Optional<ExpenseCategoryEntity> expenseCategoryEntity = expenseCategoryRepository.findById(id);
        return expenseCategoryEntity.map(categoryEntity -> modelMapper.map(categoryEntity, ExpenseCategoryModel.class)).orElse(null);
    }

    /**
     * Retrieves a list of all enabled categories as DtoCategory objects.
     *
     * @return a list of enabled categories, mapped to DtoCategory DTOs.
     * @throws CustomException if no categories are found.
     */
    @Override
    public List<DtoCategory> getAllCategories() {
        List<ExpenseCategoryEntity> expenseCategoryEntities = expenseCategoryRepository.findAll();
        if (Collections.emptyList().equals(expenseCategoryEntities)) {
            throw new CustomException("No categories found", HttpStatus.NOT_FOUND);
        }

        List<DtoCategory> dtoCategories = new ArrayList<>();
        for (ExpenseCategoryEntity categoryEntity : expenseCategoryEntities) {
            DtoCategory dtoCategory = new DtoCategory();
            dtoCategory.setId(categoryEntity.getId());
            dtoCategory.setDescription(categoryEntity.getDescription());
            dtoCategory.setLastUpdatedDatetime(categoryEntity.getLastUpdatedDatetime());
            if (categoryEntity.getEnabled()) {
                dtoCategory.setState(ACTIVO);
            } else {
                dtoCategory.setState(INACTIVO);
            }
            dtoCategories.add(dtoCategory);
        }

        return dtoCategories;
    }


    /**
     * Updates an expense category.
     *
     * @param id          the category ID
     * @param description the new description
     * @param enabled     the new status
     * @param userId The User ID.
     * @return the updated {@link DtoCategory}
     * @throws CustomException if a category with the same description exists
     */
    @Override
    public DtoCategory putCategory(Integer id, String description, Boolean enabled, Integer userId) {
        validateUserId(userId);
        if (description == null || description.isBlank()) {
            throw new CustomException("The description is required", HttpStatus.BAD_REQUEST);
        }
        boolean isEnabled = (enabled != null) && enabled;
        ExpenseCategoryEntity categoryToUpdate = findCategoryById(id);
        validateUniqueDescription(description, id);
        categoryToUpdate.setDescription(description);
        categoryToUpdate.setEnabled(isEnabled);
        categoryToUpdate.setLastUpdatedUser(userId);
        categoryToUpdate.setLastUpdatedDatetime(LocalDateTime.now());
        expenseCategoryRepository.save(categoryToUpdate);

        return DtoCategory.builder()
                .description(categoryToUpdate.getDescription())
                .id(categoryToUpdate.getId())
                .lastUpdatedDatetime(LocalDateTime.now())
                .state(categoryToUpdate.getEnabled() ? ACTIVO : INACTIVO)
                .build();
    }

    /**
     * Retrieves a {@link DtoCategory} based on the category ID.
     *
     * @param id the ID of the expense category to retrieve.
     * @return the corresponding {@link DtoCategory}, or {@code null}
     * if not found.
     * @throws CustomException if the category is not found.
     */
    @Override
    public DtoCategory getCategoryById(Integer id) {
        ExpenseCategoryEntity expenseCategoryEntity = expenseCategoryRepository.findById(id).orElse(null);
        if (expenseCategoryEntity == null) {
            throw new CustomException("Category not found", HttpStatus.NOT_FOUND);
        }
        DtoCategory dtoCategory = new DtoCategory();
        dtoCategory.setDescription(expenseCategoryEntity.getDescription());
        dtoCategory.setLastUpdatedDatetime(expenseCategoryEntity.getLastUpdatedDatetime());
        if (expenseCategoryEntity.getEnabled()) {
            dtoCategory.setState("Activo");
        } else {
            dtoCategory.setState("Inactivo");
        }
        dtoCategory.setId(id);
        return dtoCategory;
    }


    // Helper methods
    private ExpenseCategoryEntity findCategoryById(Integer id) {
        return expenseCategoryRepository.findById(id)
                .orElseThrow(() -> new CustomException("The category does not exist", HttpStatus.NOT_FOUND));
    }

    private void validateUniqueDescription(String description, Integer excludeId) {
        expenseCategoryRepository.findFirstByDescription(description)
                .ifPresent(existingCategory -> {
                    if (!existingCategory.getId().equals(excludeId)) {
                        throw new CustomException("A category with this description already exists", HttpStatus.CONFLICT);
                    }
                });
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new CustomException("The userId is required", HttpStatus.FORBIDDEN);
        }
    }

}
