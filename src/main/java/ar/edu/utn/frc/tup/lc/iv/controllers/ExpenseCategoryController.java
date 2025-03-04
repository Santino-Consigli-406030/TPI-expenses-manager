package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoCategory;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IExpenseCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for handling expense categories.
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class ExpenseCategoryController {
    /**
     * Service for handling expense category operations.
     */
    private final IExpenseCategoryService expenseCategoryService;


    /**
     * Creates a new expense category.
     *
     * @param description the description of the category
     * @param userId the user ID
     * @return a ResponseEntity containing the created DtoCategory object
     */
    @PostMapping("/postCategory")
    public ResponseEntity<DtoCategory> postCategory(String description, Integer userId) {
        return ResponseEntity.ok(expenseCategoryService.postCategory(description, userId));
    }

    /**
     * Updates an existing expense category.
     *
     * @param id          the unique identifier of the category to update
     * @param description the new description of the category (optional)
     * @param enabled     the new enabled status of the category (optional)
     * @param userId the user ID
     * @return a ResponseEntity containing the updated DtoCategory object
     */
    @PutMapping("/putById")
    public ResponseEntity<DtoCategory> putExpenseCategory(Integer id, @RequestParam(required = false) String description,
                                                          @RequestParam(required = false) Boolean enabled, Integer userId) {
        return ResponseEntity.ok(expenseCategoryService.putCategory(id, description, enabled, userId));
    }

    /**
     * Retrieves an expense category by its unique identifier.
     *
     * @param id the unique identifier of the category to retrieve
     * @return a ResponseEntity containing the DtoCategory object
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<DtoCategory> getExpenseCategory(
            @PathVariable Integer id
    ) {

        return ResponseEntity.ok(expenseCategoryService.getCategoryById(id));
    }

    /**
     * Retrieves all available categories.
     *
     * @return a ResponseEntity containing a list of DtoCategory objects.
     */
    @GetMapping("/all")
    public ResponseEntity<List<DtoCategory>> getAllCategories() {
        return ResponseEntity.ok(expenseCategoryService.getAllCategories());
    }

}
