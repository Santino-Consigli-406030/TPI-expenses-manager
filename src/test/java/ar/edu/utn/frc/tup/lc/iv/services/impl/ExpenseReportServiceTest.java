package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart.ExpenseCategoryPeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart.ExpenseYearDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart.LastBillRecordDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseKPI;
import ar.edu.utn.frc.tup.lc.iv.entities.BillRecordEntity;
import ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType;
import ar.edu.utn.frc.tup.lc.iv.repositories.BillExpenseInstallmentsRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.BillRecordRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for the ExpenseReportService.
 */
public class ExpenseReportServiceTest {

    /**
     * Mock repository for expense data.
     */
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private BillRecordRepository billRecordRepository;

    @Mock
    private BillExpenseInstallmentsRepository billExpenseInstallmentsRepository;

    @InjectMocks
    private ExpenseReportService expenseReportService;

    /**
     * Initializes mocks before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetExpenseCategoriesPeriodSuccess() {
        PeriodDto periodDto = new PeriodDto();
        periodDto.setStartDate(LocalDate.of(2023, 1, 1));
        periodDto.setEndDate(LocalDate.of(2023, 12, 31));

        Object[] result1 = {"Category1", BigDecimal.valueOf(100)};
        Object[] result2 = {"Category2", BigDecimal.valueOf(200)};
        List<Object[]> repoResults = Arrays.asList(result1, result2);

        when(expenseRepository.findAllByPeriodGroupByCategory(periodDto.getStartDate(), periodDto.getEndDate()))
                .thenReturn(repoResults);

        List<ExpenseCategoryPeriodDto> result = expenseReportService.getExpenseCategoriesPeriod(periodDto);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Category1", result.get(0).getCategory());
        assertEquals(BigDecimal.valueOf(100), result.get(0).getAmount());
        assertEquals("Category2", result.get(1).getCategory());
        assertEquals(BigDecimal.valueOf(200), result.get(1).getAmount());
    }

    @Test
    void testGetExpenseCategoriesPeriodInvalidPeriod() {
        PeriodDto periodDto = new PeriodDto();
        periodDto.setStartDate(LocalDate.of(2023, 12, 31));
        periodDto.setEndDate(LocalDate.of(2023, 1, 1));

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseReportService.getExpenseCategoriesPeriod(periodDto);
        });

        assertEquals("The start date must be earlier than the end date.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testGetExpenseCategoriesPeriodException() {
        PeriodDto periodDto = new PeriodDto();
        periodDto.setStartDate(LocalDate.of(2023, 1, 1));
        periodDto.setEndDate(LocalDate.of(2023, 12, 31));

        when(expenseRepository.findAllByPeriodGroupByCategory(periodDto.getStartDate(), periodDto.getEndDate()))
                .thenThrow(new RuntimeException("Database error"));

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseReportService.getExpenseCategoriesPeriod(periodDto);
        });

        assertEquals("Ocurrio un error al obtener los datos", exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    @Test
    void testGetExpenseYearsSuccess() {
        PeriodDto periodDto = new PeriodDto();
        periodDto.setStartDate(LocalDate.of(2023, 1, 1));
        periodDto.setEndDate(LocalDate.of(2023, 12, 31));

        Object[] result1 = {2023, 1, BigDecimal.valueOf(100), ExpenseType.COMUN.name(), 1, 1};
        List<Object[]> repoResults = new ArrayList<>();
        repoResults.add(result1);

        when(expenseRepository.findAllByPeriodGroupByYearMonth(any(), any()))
                .thenReturn(repoResults);

        List<ExpenseYearDto> result = expenseReportService.getExpenseYears(2023, 2024);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2023, result.get(0).getYear());
        assertEquals(1, result.get(0).getMonth());
        assertEquals(BigDecimal.valueOf(100), result.get(0).getAmount());
    }

    @Test
    void testGetExpenseYearsException() {
        Object[] result1 = {2023, 1};
        List<Object[]> repoResults = new ArrayList<>();
        repoResults.add(result1);

        when(expenseRepository.findAllByPeriodGroupByYearMonth(any(), any()))
                .thenReturn(repoResults);

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseReportService.getExpenseYears(2023, 2024);
        });

        assertEquals("Ocurrio un error al obtener los datos", exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    @Test
    void testGetExpenseYearsExceptionWhenYearFromGreateYaerTo() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseReportService.getExpenseYears(2024, 2023);
        });
        assertEquals("Year from can't be greater than year to", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testGetExpenseYearsExceptionWhenYearIsNull() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseReportService.getExpenseYears(null, 2023);
        });
        CustomException exceptionB = assertThrows(CustomException.class, () -> {
            expenseReportService.getExpenseYears(null, null);
        });
        assertEquals("Year from or year to  can't be null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Year from or year to  can't be null", exceptionB.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exceptionB.getStatus());

    }

    @Test
    void testGetAllByPeriodGroupByTypeAndCategory() {
        PeriodDto periodDto = new PeriodDto();
        periodDto.setStartDate(LocalDate.of(2023, 1, 1));
        periodDto.setEndDate(LocalDate.of(2023, 12, 31));

        Object[] result1 = {"COMUN", 1, "Utilities", BigDecimal.valueOf(100), 3};
        List<Object[]> repoResults = new ArrayList<>();
        repoResults.add(result1);

        when(expenseRepository.findAllByPeriodGroupByTypeAndCategory(periodDto.getStartDate(), periodDto.getEndDate()))
                .thenReturn(repoResults);

        List<DtoExpenseKPI> result = expenseReportService.getAllByPeriodGroupByTypeAndCategory(periodDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("COMUN", result.get(0).getExpenseType());
        assertEquals(1, result.get(0).getCategoryId());
        assertEquals("Utilities", result.get(0).getDescription());
        assertEquals(BigDecimal.valueOf(100), result.get(0).getAmount());
    }

    @Test
    void testGetLastBillRecordReportWithRecord() {
        BillRecordEntity billRecordEntity = new BillRecordEntity();
        billRecordEntity.setId(1);
        billRecordEntity.setStart(LocalDate.of(2023, 1, 1));
        billRecordEntity.setEnd(LocalDate.of(2023, 12, 31));

        Object[] result1 = {"COMUN", 1, "Utilities", BigDecimal.valueOf(100), 3};
        List<Object[]> repoResults = new ArrayList<>();
        repoResults.add(result1);
        Object[] result2 = {BigDecimal.ZERO};

        when(billRecordRepository.findFirstByEnabledTrueOrderByEndDesc())
                .thenReturn(Optional.of(billRecordEntity));
        when(billRecordRepository.findAmountFindByBillRecordId(any())).thenReturn(result2);
        when(billRecordRepository.findAmountPendingBill()).thenReturn(result2);
        when(billExpenseInstallmentsRepository.findByBillIdGroupByTypeAndCategory(any())).thenReturn(repoResults);

        LastBillRecordDto result = expenseReportService.getLastBillRecordReport();

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(LocalDate.of(2023, 1, 1), result.getStartDate());
        assertEquals(LocalDate.of(2023, 12, 31), result.getEndDate());
    }

    @Test
    void testGetLastBillRecordReportWithoutRecord() {
        when(billRecordRepository.findFirstByEnabledTrueOrderByEndDesc())
                .thenReturn(Optional.empty());

        LastBillRecordDto result = expenseReportService.getLastBillRecordReport();

        assertNotNull(result);
        assertNull(result.getId());
        assertTrue(result.getBills().isEmpty());
        assertEquals(BigDecimal.ZERO, result.getFineAmount());
        assertEquals(BigDecimal.ZERO, result.getPendingAmount());
    }

    @Test
    void testValidPeriodWithNullPeriod() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseReportService.getExpenseCategoriesPeriod(null);
        });
        assertEquals("The period be can't null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}
