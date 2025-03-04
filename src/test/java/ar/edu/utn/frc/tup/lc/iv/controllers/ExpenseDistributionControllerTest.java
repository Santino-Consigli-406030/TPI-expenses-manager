package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.ExpenseOwnerVisualizerDTO;
import ar.edu.utn.frc.tup.lc.iv.services.impl.ExpenseDistributionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ExpenseDistributionControllerTest {

    @Mock
    private ExpenseDistributionService expenseDistributionService;

    @InjectMocks
    private ExpenseDistributionController expenseDistributionController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllExpensesById() {
        Integer ownerId = 1;
        String startDate = "2023-01-01";
        String endDate = "2023-12-31";

        List<ExpenseOwnerVisualizerDTO> expenseList = new ArrayList<>();
        ExpenseOwnerVisualizerDTO expense = new ExpenseOwnerVisualizerDTO();
        expenseList.add(expense);

        when(expenseDistributionService.findByOwnerId(ownerId, startDate, endDate)).thenReturn(expenseList);

        ResponseEntity<List<ExpenseOwnerVisualizerDTO>> response = expenseDistributionController.getAllExpensesById(ownerId, startDate, endDate);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expenseList, response.getBody());
    }
}
