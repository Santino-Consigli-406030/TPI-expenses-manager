package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoCategory;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseCategoryEntity;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseCategoryModel;
import ar.edu.utn.frc.tup.lc.iv.repositories.ExpenseCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for the ExpenseCategoryService.
 */
public class ExpenseCategoryServiceTest {

    /**
     * Mocked repository for expense categories.
     */
    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;

    /**
     * Mocked model mapper for mapping entities to DTOs.
     */
    @Mock
    private ModelMapper modelMapper;

    /**
     * Service under test, with injected mocks.
     */
    @InjectMocks
    private ExpenseCategoryService expenseCategoryService;

    /**
     * Category entity used in tests.
     */
    private ExpenseCategoryEntity categoryEntity;

    /**
     * Initializes mocks and test data before each test.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initializing categoryEntity with test data
        categoryEntity = new ExpenseCategoryEntity();
        categoryEntity.setId(1);
        categoryEntity.setDescription("Test Category");
        categoryEntity.setEnabled(true);
        categoryEntity.setLastUpdatedDatetime(LocalDateTime.now());
        categoryEntity.setCreatedUser(1);
        categoryEntity.setLastUpdatedUser(1);
    }

    /**
     * Test for creating a new category.
     * Ensures that the category is created and saved correctly.
     */
    @Test
    public void testPostCategory_NewCategory() {
        // Arrange
        String description = "Test Category";
        when(expenseCategoryRepository.findFirstByDescription(description)).thenReturn(Optional.empty());
        when(expenseCategoryRepository.save(any(ExpenseCategoryEntity.class))).thenReturn(categoryEntity);

        // Act
        DtoCategory result = expenseCategoryService.postCategory(description, 1);

        // Assert
        assertNotNull(result);
        assertEquals("Test Category", result.getDescription());
        assertEquals("Activo", result.getState());
        verify(expenseCategoryRepository, times(1)).save(any(ExpenseCategoryEntity.class));
    }

    /**
     * Test for creating a category when a category with the same description already exists.
     * Ensures that a CustomException is thrown with the expected message and status.
     */
    @Test
    public void testPostCategory_CategoryExists_ThrowsCustomException() {
        // Arrange
        String description = "Test Category";
        when(expenseCategoryRepository.findFirstByDescription(description)).thenReturn(Optional.of(categoryEntity));

        // Act & Assert
        CustomException thrown = assertThrows(CustomException.class, () -> expenseCategoryService.postCategory(description, 1));
        assertEquals("A category with this description already exists", thrown.getMessage());

    }

    /**
     * Test for retrieving a category model by ID when the category exists.
     * Ensures that the returned model is not null.
     */
    @Test
    public void testGetCategoryModel_FoundCategory() {
        // Arrange
        when(expenseCategoryRepository.findById(1)).thenReturn(Optional.of(categoryEntity));
        when(modelMapper.map(any(ExpenseCategoryEntity.class), eq(ExpenseCategoryModel.class)))
                .thenReturn(new ExpenseCategoryModel());

        // Act
        ExpenseCategoryModel result = expenseCategoryService.getCategoryModel(1);

        // Assert
        assertNotNull(result);
        verify(expenseCategoryRepository, times(1)).findById(1);
    }

    /**
     * Test for retrieving a category model by ID when the category does not exist.
     * Ensures that the returned model is null.
     */
    @Test
    public void testGetCategoryModel_CategoryNotFound() {
        // Arrange
        when(expenseCategoryRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        ExpenseCategoryModel result = expenseCategoryService.getCategoryModel(1);

        // Assert
        assertNull(result);
    }

    /**
     * Test for retrieving all categories when categories are found.
     * Ensures that the list is not null and contains the expected number of elements.
     */
    @Test
    public void testGetAllCategories() {
        List<ExpenseCategoryEntity> categories = new ArrayList<>();
        categories.add(categoryEntity);
        when(expenseCategoryRepository.findAll()).thenReturn(categories);

        List<DtoCategory> result = expenseCategoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Category", result.get(0).getDescription());
    }
    /**
     * Test retrieving a category by ID when the category exists but is disabled.
     */
    @Test
    public void testGetCategoryById_CategoryExistsAndDisabled() {
        categoryEntity.setEnabled(false);
        when(expenseCategoryRepository.findById(1)).thenReturn(Optional.of(categoryEntity));
        DtoCategory result = expenseCategoryService.getCategoryById(1);
        assertNotNull(result);
        assertEquals("Test Category", result.getDescription());
        assertEquals("Inactivo", result.getState());
        assertEquals(1, result.getId());
        verify(expenseCategoryRepository, times(1)).findById(1);
    }

    /**
     * Test retrieving a category by ID when the category does not exist.
     * Ensures a CustomException is thrown with the expected message and status.
     */
    @Test
    public void testGetCategoryById_CategoryNotFound() {
        when(expenseCategoryRepository.findById(1)).thenReturn(Optional.empty());
        CustomException thrown = assertThrows(CustomException.class, () -> expenseCategoryService.getCategoryById(1));
        assertEquals("Category not found", thrown.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
        verify(expenseCategoryRepository, times(1)).findById(1);
    }
    /**
     * Test for retrieving all categories when no categories are found.
     * Ensures that a CustomException is thrown with the expected message and status.
     */
    @Test
    public void testGetAllCategories_NoCategoriesFound() {
        when(expenseCategoryRepository.findAll()).thenReturn(Collections.emptyList());

        CustomException thrown = assertThrows(CustomException.class, () -> expenseCategoryService.getAllCategories());
        assertEquals("No categories found", thrown.getMessage());

    }

    /**
     * Test for updating an existing category with new description and enabled status.
     * Ensures that the category is updated correctly.
     */
    @Test
    public void testPutCategory_UpdateCategory() {
        String newDescription = "Updated Category";
        Boolean newEnabledStatus = false;
        ExpenseCategoryEntity updatedCategory = new ExpenseCategoryEntity();
        updatedCategory.setId(1);
        updatedCategory.setDescription(newDescription);
        updatedCategory.setEnabled(newEnabledStatus);
        updatedCategory.setLastUpdatedDatetime(LocalDateTime.now());

        when(expenseCategoryRepository.findById(1)).thenReturn(Optional.of(categoryEntity));
        when(expenseCategoryRepository.save(any(ExpenseCategoryEntity.class))).thenReturn(updatedCategory);

        DtoCategory result = expenseCategoryService.putCategory(1, newDescription, newEnabledStatus,1);
        assertNotNull(result);
        assertEquals(newDescription, result.getDescription());
        assertEquals("Inactivo", result.getState());
        verify(expenseCategoryRepository, times(1)).save(any(ExpenseCategoryEntity.class));
    }

    /**
     * Test for updating a category when the category does not exist.
     * Ensures that a CustomException is thrown with the expected message and status.
     */
    @Test
    public void testPutCategory_CategoryNotFound() {
        when(expenseCategoryRepository.findById(1)).thenReturn(Optional.empty());
        CustomException thrown = assertThrows(CustomException.class, () -> expenseCategoryService.putCategory(1, "New Category", true, 1));
        assertEquals("The category does not exist", thrown.getMessage());

    }
    /**
     * Test retrieving a category by ID when the category exists and is enabled.
     */
    @Test
    public void testGetCategoryById_CategoryExistsAndEnabled() {
        when(expenseCategoryRepository.findById(1)).thenReturn(Optional.of(categoryEntity));
        DtoCategory result = expenseCategoryService.getCategoryById(1);
        assertNotNull(result);
        assertEquals("Test Category", result.getDescription());
        assertEquals("Activo", result.getState());
        assertEquals(1, result.getId());
        verify(expenseCategoryRepository, times(1)).findById(1);
    }
    /**
     * Test for updating a category when a category with the same description already exists.
     * Ensures that a CustomException is thrown with the expected message and status.
     */
    @Test
    public void testPutCategory_DuplicateDescription() {

        String description = "Test Category";
        when(expenseCategoryRepository.findFirstByDescription(description)).thenReturn(Optional.of(categoryEntity));
        CustomException thrown = assertThrows(CustomException.class, () -> expenseCategoryService.putCategory(1, description, true, 1));
        assertEquals("The category does not exist", thrown.getMessage());

    }
    @Test
    void postCategory_WithNullUserId_ThrowsCustomException() {
        // Arrange
        String description = "Test Category";
        Integer userId = null;

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> expenseCategoryService.postCategory(description, userId));

        assertEquals("The userId is required", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        verify(expenseCategoryRepository, never()).findFirstByDescription(any());
        verify(expenseCategoryRepository, never()).save(any());
    }

    @Test
    void putCategory_WithNullUserId_ThrowsCustomException() {
        // Arrange
        Integer id = 1;
        String description = "Test Category";
        Boolean enabled = true;
        Integer userId = null;

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> expenseCategoryService.putCategory(id, description, enabled, userId));

        assertEquals("The userId is required", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        verify(expenseCategoryRepository, never()).findById(any());
        verify(expenseCategoryRepository, never()).save(any());
    }

    @Test
    void postCategory_WithValidUserId_ValidatesSuccessfully() {
        // Arrange
        String description = "Test Category";
        Integer userId = 1;
        ExpenseCategoryEntity savedEntity = new ExpenseCategoryEntity();
        savedEntity.setId(1);
        savedEntity.setDescription(description);
        savedEntity.setEnabled(true);
        savedEntity.setCreatedUser(userId);
        savedEntity.setLastUpdatedUser(userId);

        when(expenseCategoryRepository.findFirstByDescription(description))
                .thenReturn(Optional.empty());
        when(expenseCategoryRepository.save(any(ExpenseCategoryEntity.class)))
                .thenReturn(savedEntity);

        // Act
        DtoCategory result = expenseCategoryService.postCategory(description, userId);

        // Assert
        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals("Activo", result.getState());
        verify(expenseCategoryRepository).findFirstByDescription(description);
        verify(expenseCategoryRepository).save(any(ExpenseCategoryEntity.class));
    }

    @Test
    void putCategory_WithValidUserId_ValidatesSuccessfully() {
        // Arrange
        Integer id = 1;
        String description = "Updated Category";
        Boolean enabled = true;
        Integer userId = 1;

        ExpenseCategoryEntity existingEntity = new ExpenseCategoryEntity();
        existingEntity.setId(id);
        existingEntity.setDescription("Old Description");
        existingEntity.setEnabled(true);

        when(expenseCategoryRepository.findById(id))
                .thenReturn(Optional.of(existingEntity));
        when(expenseCategoryRepository.findFirstByDescription(description))
                .thenReturn(Optional.empty());
        when(expenseCategoryRepository.save(any(ExpenseCategoryEntity.class)))
                .thenReturn(existingEntity);

        // Act
        DtoCategory result = expenseCategoryService.putCategory(id, description, enabled, userId);

        // Assert
        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals("Activo", result.getState());
        verify(expenseCategoryRepository).findById(id);
        verify(expenseCategoryRepository).findFirstByDescription(description);
        verify(expenseCategoryRepository).save(any(ExpenseCategoryEntity.class));
    }
    @Test
    void postCategory_WithNullDescription_ThrowsCustomException() {
        String description = null;
        Integer userId = 1;

        CustomException exception = assertThrows(CustomException.class,
                () -> expenseCategoryService.postCategory(description, userId));

        assertEquals("The description is required", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
    @Test
    void testConstructor() {
        ExpenseCategoryService service = new ExpenseCategoryService(expenseCategoryRepository, modelMapper);
        assertNotNull(service);
    }
    @Test
    void putCategory_WithEmptyDescription_ThrowsCustomException() {
        Integer id = 1;
        String description = "";
        Boolean enabled = true;
        Integer userId = 1;

        CustomException exception = assertThrows(CustomException.class,
                () -> expenseCategoryService.putCategory(id, description, enabled, userId));

        assertEquals("The description is required", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
    @Test
    void validateUniqueDescription_WithExistingDescriptionDifferentId_ThrowsCustomException() {
        // Arrange
        ExpenseCategoryEntity existingCategory = new ExpenseCategoryEntity();
        existingCategory.setId(2); // Un ID diferente
        existingCategory.setDescription("Existing Category");

        when(expenseCategoryRepository.findById(1)).thenReturn(Optional.of(new ExpenseCategoryEntity()));
        when(expenseCategoryRepository.findFirstByDescription("Existing Category"))
                .thenReturn(Optional.of(existingCategory));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> expenseCategoryService.putCategory(1, "Existing Category", true, 1));

        assertEquals("A category with this description already exists", exception.getMessage());
    }
}
