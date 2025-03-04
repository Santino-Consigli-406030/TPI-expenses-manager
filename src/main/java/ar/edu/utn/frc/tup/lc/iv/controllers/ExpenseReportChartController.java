package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart.ExpenseCategoryPeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart.ExpenseYearDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart.LastBillRecordDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseKPI;
import ar.edu.utn.frc.tup.lc.iv.services.impl.ExpenseReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for handling expense reports.
 */
@RestController
@RequestMapping("/reportchart")
@RequiredArgsConstructor
public class ExpenseReportChartController {
    /**
     * Service for handling expense reports.
     */
    private final ExpenseReportService expenseReportServiceice;

    /**
     * Date format for the request parameters.
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * Retrieves the expense data grouped by year and month.
     *
     * @param yearFrom start year for filter
     * @param yearTo   end year for filter
     * @return a ResponseEntity containing a list of ExpenseYearDto objects
     */
    @GetMapping("/yearmonth")
    ResponseEntity<List<ExpenseYearDto>> getYearMonth(@RequestParam(value = "start_year") Integer yearFrom,
                                                      @RequestParam(value = "end_year") Integer yearTo) {
        return new ResponseEntity<>(expenseReportServiceice.getExpenseYears(yearFrom, yearTo), HttpStatus.OK);
    }

    /**
     * Retrieves the expense categories for a specified period.
     *
     * @param startDate the start date of the period
     * @param endDate   the end date of the period
     * @return a ResponseEntity containing a list of ExpenseCategoryPeriodDto objects
     */
    @GetMapping("/categoriesperiod")
    ResponseEntity<List<ExpenseCategoryPeriodDto>> getCategoriesPeriod(@RequestParam(value = "start_date")
                                                                       @DateTimeFormat(pattern = DATE_FORMAT) LocalDate startDate,
                                                                       @RequestParam("end_date")
                                                                       @DateTimeFormat(pattern = DATE_FORMAT) LocalDate endDate) {
        PeriodDto periodDto = new PeriodDto(startDate, endDate);
        return new ResponseEntity<>(expenseReportServiceice.getExpenseCategoriesPeriod(periodDto), HttpStatus.OK);
    }

    /**
     * Retrieves the expense data grouped by type and category for a specified period.
     *
     * @param startDate the start date of the period
     * @param endDate   the end date of the period
     * @return a ResponseEntity containing a list of DtoExpenseKPI objects
     */
    @GetMapping("/expenseByTypeAndCategory")
    ResponseEntity<List<DtoExpenseKPI>> get(@RequestParam(value = "start_date") @DateTimeFormat(pattern = DATE_FORMAT) LocalDate startDate,
                                            @RequestParam("end_date") @DateTimeFormat(pattern = DATE_FORMAT) LocalDate endDate) {
        PeriodDto periodDto = new PeriodDto(startDate, endDate);
        return new ResponseEntity<>(expenseReportServiceice.getAllByPeriodGroupByTypeAndCategory(periodDto), HttpStatus.OK);
    }

    /**
     * Retrieves the last bill record report.
     *
     * @return a ResponseEntity containing a LastBillRecordDto object
     */
    @GetMapping("/lastBillRecord")
    ResponseEntity<LastBillRecordDto> getLastBillRecord() {
        return new ResponseEntity<>(expenseReportServiceice.getLastBillRecordReport(), HttpStatus.OK);
    }
}
