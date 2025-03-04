package ar.edu.utn.frc.tup.lc.iv.services.impl.billExpense;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.response.BillExpenseDto;
import ar.edu.utn.frc.tup.lc.iv.entities.BillRecordEntity;
import ar.edu.utn.frc.tup.lc.iv.models.BillRecordModel;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseModel;
import ar.edu.utn.frc.tup.lc.iv.repositories.BillRecordRepository;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BillExpenseServiceTest {

    @Mock
    private BillRecordRepository billRecordRepository;

    @Mock
    private IExpenseService expenseService;

    @Mock
    private BillExpenseDistributionService billExpenseDistributionService;

    @Mock
    private PeriodBillExpenseValidation periodBillExpenseValidation;

    @Mock
    private BillExpenseDataService billExpenseDataService;

    @Mock
    private BillExpenseMappersService billExpenseMappersService;

    @InjectMocks
    private BillExpenseService billExpenseService;

    private PeriodDto periodDto;
    private BillRecordModel billRecordModel;
    private BillExpenseDto billExpenseDto;
    private ExpenseModel expenseModel;
    private BillRecordEntity billRecordEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inicializar datos de prueba
        periodDto = new PeriodDto();
        periodDto.setStartDate(LocalDate.parse("2023-01-01"));
        periodDto.setEndDate(LocalDate.parse("2023-01-31"));

        billRecordModel = new BillRecordModel();
        billExpenseDto = new BillExpenseDto();
        expenseModel = new ExpenseModel();
        billRecordEntity = new BillRecordEntity();
    }

    @Test
    void testGenerateBillExpense_NoExistingBillRecord_CreatesNewRecord() {
        // Configurar mocks
        when(billExpenseDataService.getBillRecord(periodDto)).thenReturn(null);
        when(billExpenseDataService.existBillRecordInPeriod(periodDto)).thenReturn(false);
        when(billExpenseDataService.constraintBillExpenseOwners(periodDto, 1))
                .thenReturn(Collections.emptyList());
        when(billExpenseMappersService.billRecordModelToDto(any(BillRecordModel.class))).thenReturn(billExpenseDto);

        // Ejecutar método
        BillExpenseDto result = billExpenseService.generateBillExpense(periodDto);

        // Verificaciones
        verify(periodBillExpenseValidation).validatePeriod(periodDto);
        verify(billExpenseDataService).getBillRecord(periodDto);
        verify(billExpenseDataService).existBillRecordInPeriod(periodDto);
        verify(billExpenseDataService).constraintBillExpenseOwners(periodDto, 1);
    }

    @Test
    void testGenerateBillExpense_ExistingBillRecord_ReturnsExistingRecord() {
        // Configurar mocks
        when(billExpenseDataService.getBillRecord(periodDto)).thenReturn(billRecordModel);
        when(billExpenseMappersService.billRecordModelToDto(billRecordModel)).thenReturn(billExpenseDto);

        // Ejecutar método
        BillExpenseDto result = billExpenseService.generateBillExpense(periodDto);

        // Verificaciones
        verify(periodBillExpenseValidation).validatePeriod(periodDto);
        verify(billExpenseDataService).getBillRecord(periodDto);
        verify(billExpenseMappersService).billRecordModelToDto(billRecordModel);
        verify(billExpenseDataService, never()).existBillRecordInPeriod(periodDto);
        verify(billRecordRepository, never()).save(any(BillRecordEntity.class));
        assertEquals(billExpenseDto, result);
    }

    @Test
    void testGenerateBillExpense_PeriodOverlaps_ThrowsException() {
        // Configurar mocks
        when(billExpenseDataService.getBillRecord(periodDto)).thenReturn(null);
        when(billExpenseDataService.existBillRecordInPeriod(periodDto)).thenReturn(true);

        // Ejecutar y verificar excepción
        CustomException exception = assertThrows(CustomException.class, () -> {
            billExpenseService.generateBillExpense(periodDto);
        });

        assertEquals("The specified period overlaps with an existing generated one", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(billExpenseDataService).existBillRecordInPeriod(periodDto);
    }



    @Test
    void testSaveBillRecord_SavesAndReturnsMappedModel() {
        // Configurar mocks
        when(billExpenseMappersService.billRecordModelToEntity(billRecordModel)).thenReturn(billRecordEntity);
        when(billRecordRepository.save(billRecordEntity)).thenReturn(billRecordEntity);
        when(billExpenseMappersService.entityToBillRecordModel(billRecordEntity)).thenReturn(billRecordModel);

        // Ejecutar método
        BillRecordModel result = billExpenseService.saveBillRecord(billRecordModel);

        // Verificaciones
        verify(billExpenseMappersService).billRecordModelToEntity(billRecordModel);
        verify(billRecordRepository).save(billRecordEntity);
        verify(billExpenseMappersService).entityToBillRecordModel(billRecordEntity);
        assertEquals(billRecordModel, result);
    }
}