package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoCategory;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IExpenseCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class ExpenseCategoryControllerTest {

    @Mock
    private IExpenseCategoryService expenseCategoryService;

    @InjectMocks
    private ExpenseCategoryController expenseCategoryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPostCategory() {
        DtoCategory mockCategory = new DtoCategory();
        mockCategory.setDescription("Test Category");

        when(expenseCategoryService.postCategory(any(String.class),anyInt())).thenReturn(mockCategory);

        ResponseEntity<DtoCategory> response = expenseCategoryController.postCategory("Test Category",1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test Category", response.getBody().getDescription());
    }

    @Test
    public void testPutExpenseCategory() {
        DtoCategory mockCategory = new DtoCategory();
        mockCategory.setDescription("Updated Category");

        when(expenseCategoryService.putCategory(anyInt(), any(String.class), any(Boolean.class), anyInt())).thenReturn(mockCategory);

        ResponseEntity<DtoCategory> response = expenseCategoryController.putExpenseCategory(1, "Updated Category", true,1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated Category", response.getBody().getDescription());
    }

    @Test
    public void testGetExpenseCategory() {
        DtoCategory mockCategory = new DtoCategory();
        mockCategory.setDescription("Test Category");

        when(expenseCategoryService.getCategoryById(anyInt())).thenReturn(mockCategory);

        ResponseEntity<DtoCategory> response = expenseCategoryController.getExpenseCategory(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test Category", response.getBody().getDescription());
    }

    @Test
    public void testGetAllCategories() {
        DtoCategory category1 = new DtoCategory();
        category1.setDescription("Category 1");

        DtoCategory category2 = new DtoCategory();
        category2.setDescription("Category 2");

        List<DtoCategory> categories = Arrays.asList(category1, category2);

        when(expenseCategoryService.getAllCategories()).thenReturn(categories);

        ResponseEntity<List<DtoCategory>> response = expenseCategoryController.getAllCategories();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("Category 1", response.getBody().get(0).getDescription());
        assertEquals("Category 2", response.getBody().get(1).getDescription());
    }
}