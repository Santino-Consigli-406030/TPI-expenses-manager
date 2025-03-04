package ar.edu.utn.frc.tup.lc.iv.services.interfaces;

import ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart.ExpenseCategoryPeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart.ExpenseYearDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart.LastBillRecordDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseKPI;

import java.util.List;

/**
 * Service class for managing expense reports.
 */
public interface IExpenseReportService {
    /**
     * Retrieves a list of expense categories for a given period.
     *
     * @param periodDto The period for which to retrieve expense categories.
     * @return A list of ExpenseCategoryPeriodDto objects.
     */
    List<ExpenseCategoryPeriodDto> getExpenseCategoriesPeriod(PeriodDto periodDto);

    /**
     * Retrieves a list of expense years.
     *
     * @param yearFrom start year for filter
     * @param yearTo   end year for filter
     * @return A list of ExpenseYearDto objects.
     */
    List<ExpenseYearDto> getExpenseYears(Integer yearFrom, Integer yearTo);

    /**
     * Retrieves all expenses grouped by type and category for a given period.
     *
     * @param periodDto The period for which to retrieve expenses.
     * @return A list of DtoExpenseKPI objects.
     */
    List<DtoExpenseKPI> getAllByPeriodGroupByTypeAndCategory(PeriodDto periodDto);

    /**
     * Retrieves the last bill record report.
     *
     * @return A LastBillRecordDto object.
     */
    LastBillRecordDto getLastBillRecordReport();
}
