package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.client.ProviderRestClient;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.ExpenseCategoryDTO;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.ExpenseOwnerVisualizerDTO;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.ProviderDTO;
import ar.edu.utn.frc.tup.lc.iv.dtos.owner.OwnerDto;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseCategoryEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseDistributionEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseEntity;
import ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType;
import ar.edu.utn.frc.tup.lc.iv.repositories.ExpenseDistributionRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.ExpenseRepository;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ExpenseDistributionServiceTest {

    @Mock
    private ExpenseDistributionRepository repository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private IProviderService providerService;

    @Mock
    private OwnerService ownerService;

    @InjectMocks
    private ExpenseDistributionService service;

    private ExpenseEntity testExpense;
    private ExpenseDistributionEntity testDistribution;
    private List<ProviderDTO> providers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        testExpense = new ExpenseEntity();
        testExpense.setId(1);
        testExpense.setDescription("Test Expense");
        testExpense.setAmount(BigDecimal.valueOf(100));
        testExpense.setExpenseDate(LocalDate.now());
        testExpense.setExpenseType(ExpenseType.COMUN);
        testExpense.setProviderId(1);
        testExpense.setEnabled(true);
        testExpense.setInstallments(1);

        ExpenseCategoryEntity category = new ExpenseCategoryEntity();
        category.setId(1);
        category.setDescription("Test Category");
        testExpense.setCategory(category);

        testDistribution = new ExpenseDistributionEntity();
        testDistribution.setId(1);
        testDistribution.setOwnerId(1);
        testDistribution.setProportion(BigDecimal.ONE);
        testDistribution.setEnabled(true);
        testDistribution.setExpense(testExpense);

        List<ExpenseDistributionEntity> distributions = new ArrayList<>();
        distributions.add(testDistribution);
        testExpense.setDistributions(distributions);

        providers = Arrays.asList(
                new ProviderDTO(1, "Test Provider")
        );

        // Setup mock responses
        when(repository.findAllDistinct()).thenReturn(Arrays.asList(testDistribution));
        when(providerService.getProviders()).thenReturn(providers);
    }



    @Test
    void findByOwnerId_WithValidInput_ShouldReturnFilteredList() {
        OwnerDto ownerDto = OwnerDto.builder()
                .id(1)
                .name("Test Owner")
                .dni("123456")
                .userId(1)
                .build();
        // Arrange
        when(expenseRepository.findAllByDate(any(), any())).thenReturn(Arrays.asList(testExpense));
        when(ownerService.getOwnerByUserId(any())).thenReturn(ownerDto);
        // Act
        List<ExpenseOwnerVisualizerDTO> result = service.findByOwnerId(1,
                LocalDate.now().toString(), LocalDate.now().toString());

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findByOwnerId_WithInvalidOwnerId_ShouldThrowException() {
        // Act & Assert
        when(ownerService.getOwnerByUserId(any())).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () ->
                service.findByOwnerId(-1, LocalDate.now().toString(), LocalDate.now().toString()));
    }


}