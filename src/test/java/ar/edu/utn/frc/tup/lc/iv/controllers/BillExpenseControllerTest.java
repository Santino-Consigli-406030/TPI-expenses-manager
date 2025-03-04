package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.response.BillExpenseDto;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.billExpense.IBillExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class BillExpenseControllerTest {

    @Mock
    private IBillExpenseService billExpenseService;

    @InjectMocks
    private BillExpenseController billExpenseController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateExpenses() {
        PeriodDto periodDto = new PeriodDto();
        BillExpenseDto billExpenseDto = new BillExpenseDto();

        when(billExpenseService.generateBillExpense(periodDto)).thenReturn(billExpenseDto);

        ResponseEntity<BillExpenseDto> response = billExpenseController.generateExpenses(periodDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(billExpenseDto, response.getBody());
    }
}