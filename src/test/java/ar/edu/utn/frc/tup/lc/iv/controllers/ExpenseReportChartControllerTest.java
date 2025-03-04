package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart.ExpenseCategoryPeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart.ExpenseYearDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart.LastBillRecordDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseKPI;
import ar.edu.utn.frc.tup.lc.iv.services.impl.ExpenseReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the ExpenseReportChartController.
 */
public class ExpenseReportChartControllerTest {
    private MockMvc mockMvc;

    /**
     * Mocked instance of ExpenseReportService.
     */
    @Mock
    private ExpenseReportService expenseReportService;

    /**
     * Injected instance of ExpenseReportChartController.
     */
    @InjectMocks
    private ExpenseReportChartController expenseReportChartController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Initializes mocks before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(expenseReportChartController).build();
    }

    @Test
    void testGetYearMonth() throws Exception {
        List<ExpenseYearDto> expenseYearDtoList = Collections.singletonList(
                new ExpenseYearDto(2023, 5, new BigDecimal("1500.00"), "COMUN", 1, 2)
        );

        when(expenseReportService.getExpenseYears(2023, 2024)).thenReturn(expenseYearDtoList);

        mockMvc.perform(get("/reportchart/yearmonth")
                        .param("start_year", "2023")
                        .param("end_year", "2024")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].year").value(2023))
                .andExpect(jsonPath("$[0].month").value(5))
                .andExpect(jsonPath("$[0].amount").value(1500.00))
                .andExpect(jsonPath("$[0].expense_type").value("COMUN"))
                .andExpect(jsonPath("$[0].providerId").value(1))
                .andExpect(jsonPath("$[0].categoryId").value(2));
    }

    @Test
    void testGetCategoriesPeriod() throws Exception {
        List<ExpenseCategoryPeriodDto> expenseCategoryPeriodDtoList = Collections.singletonList(
                new ExpenseCategoryPeriodDto("Maintenance", new BigDecimal("2000.00"))
        );

        when(expenseReportService.getExpenseCategoriesPeriod(new PeriodDto(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31))))
                .thenReturn(expenseCategoryPeriodDtoList);

        mockMvc.perform(get("/reportchart/categoriesperiod")
                        .param("start_date", "2023-01-01")
                        .param("end_date", "2023-12-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Maintenance"))
                .andExpect(jsonPath("$[0].amount").value(2000.00));
    }

    @Test
    void testGetExpenseByTypeAndCategory() throws Exception {
        List<DtoExpenseKPI> expenseKpiList = Collections.singletonList(
                new DtoExpenseKPI("COMUN", 2, "Repair", new BigDecimal("500.00"), 1)
        );

        when(expenseReportService.getAllByPeriodGroupByTypeAndCategory(new PeriodDto(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31))))
                .thenReturn(expenseKpiList);

        mockMvc.perform(get("/reportchart/expenseByTypeAndCategory")
                        .param("start_date", "2023-01-01")
                        .param("end_date", "2023-12-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].expenseType").value("COMUN"))
                .andExpect(jsonPath("$[0].categoryId").value(2))
                .andExpect(jsonPath("$[0].description").value("Repair"))
                .andExpect(jsonPath("$[0].amount").value(500.00))
                .andExpect(jsonPath("$[0].providerId").value(1));
    }

    @Test
    void testGetLastBillRecord() throws Exception {
        LastBillRecordDto lastBillRecordDto = new LastBillRecordDto();
        lastBillRecordDto.setId(1);
        lastBillRecordDto.setFineAmount(new BigDecimal("100.00"));
        lastBillRecordDto.setPendingAmount(new BigDecimal("250.00"));
        lastBillRecordDto.setBills(Collections.emptyList());

        when(expenseReportService.getLastBillRecordReport()).thenReturn(lastBillRecordDto);

        mockMvc.perform(get("/reportchart/lastBillRecord")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fineAmount").value(100.00))
                .andExpect(jsonPath("$.pendingAmount").value(250.00))
                .andExpect(jsonPath("$.bills").isEmpty());
    }
}
