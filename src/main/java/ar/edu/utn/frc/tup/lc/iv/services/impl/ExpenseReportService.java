package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart.ExpenseCategoryPeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart.ExpenseYearDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReportChart.LastBillRecordDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseKPI;
import ar.edu.utn.frc.tup.lc.iv.entities.BillRecordEntity;
import ar.edu.utn.frc.tup.lc.iv.repositories.BillExpenseInstallmentsRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.BillRecordRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.ExpenseRepository;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IExpenseReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing expense reports.
 */
@Service
@RequiredArgsConstructor
public class ExpenseReportService implements IExpenseReportService {
    /**
     * Repository for accessing expense data.
     */
    private final ExpenseRepository expenseRepository;

    /**
     * Repository for accessing bill record data.
     */
    private final BillRecordRepository billRecordRepository;

    /**
     * Repository for accessing bill expense installments data.
     */
    private final BillExpenseInstallmentsRepository billExpenseInstallmentsRepository;
    /**
     * Constant representing the index for the third element in an array.
     */
    private static final int TRHEE = 3;

    /**
     * Constant representing the index for the fourth element in an array.
     */
    private static final int FOUR = 4;

    /**
     * Constant representing the index for the fifth element in an array.
     */
    private static final int FIVE = 5;

    /**
     * Retrieves a list of expense categories for a given period.
     *
     * @param periodDto the period for which to retrieve expense categories
     * @return a list of ExpenseCategoryPeriodDto objects representing the
     * expense categories for the given period
     * @throws CustomException if an error occurs while retrieving the data
     */
    @Override
    public List<ExpenseCategoryPeriodDto> getExpenseCategoriesPeriod(PeriodDto periodDto) {
        validPeriod(periodDto);
        try {
            return repoToExpenseCategoryPeriodDto(periodDto);
        } catch (Exception e) {
            throw new CustomException("Ocurrio un error al obtener los datos", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * Retrieves a list of expense years.
     *
     * @return a list of ExpenseYearDto objects representing the expense years
     * @throws CustomException if an error occurs while retrieving the data
     */
    @Override
    public List<ExpenseYearDto> getExpenseYears(Integer yearFrom, Integer yearTo) {
        if (yearFrom == null || yearTo == null) {
            throw new CustomException("Year from or year to  can't be null", HttpStatus.BAD_REQUEST);
        }
        if (yearFrom > yearTo) {
            throw new CustomException("Year from can't be greater than year to", HttpStatus.BAD_REQUEST);
        }
        try {
            return repoToExpenseYearDto(yearFrom, yearTo);
        } catch (Exception e) {
            throw new CustomException("Ocurrio un error al obtener los datos", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * Retrieves a list of DtoExpenseKPI objects grouped by type and category
     * for a given period.
     *
     * @param periodDto the period for which to retrieve the data
     * @return a list of DtoExpenseKPI objects representing the data grouped
     * by type and category
     */
    @Override
    public List<DtoExpenseKPI> getAllByPeriodGroupByTypeAndCategory(PeriodDto periodDto) {

        List<Object[]> repo = expenseRepository.findAllByPeriodGroupByTypeAndCategory(periodDto.getStartDate(), periodDto.getEndDate());

        return repo.stream()
                .map(result -> new DtoExpenseKPI((String) result[0], (Integer) result[1], (String) result[2],
                        (BigDecimal) result[TRHEE], (Integer) result[FOUR])).collect(Collectors.toList());
    }

    /**
     * Retrieves the last bill record report.
     *
     * @return a LastBillRecordDto object representing the last bill record report
     */
    @Override
    public LastBillRecordDto getLastBillRecordReport() {
        LastBillRecordDto lastBillRecordDto = getLastBillRecord();
        if (lastBillRecordDto.getId() != null) {
            lastBillRecordDto.setBills(getBillsInstallments(lastBillRecordDto.getId()));
            lastBillRecordDto.setFineAmount(getBillFindAmount(lastBillRecordDto.getId()));
            lastBillRecordDto.setPendingAmount(getBillPendingAmount());
        }
        return lastBillRecordDto;
    }

    /**
     * Converts the repository results to a list of ExpenseCategoryPeriodDto objects.
     *
     * @param periodDto the period for which to retrieve expense categories
     * @return a list of ExpenseCategoryPeriodDto objects representing the expense
     * categories for the given period
     */
    private List<ExpenseCategoryPeriodDto> repoToExpenseCategoryPeriodDto(PeriodDto periodDto) {
        List<Object[]> repo = expenseRepository.findAllByPeriodGroupByCategory(periodDto.getStartDate(), periodDto.getEndDate());
        return repo.stream()
                .map(result -> new ExpenseCategoryPeriodDto((String) result[0], (BigDecimal) result[1]))
                .collect(Collectors.toList());
    }

    /**
     * Converts the repository results to a list of ExpenseYearDto objects.
     *
     * @param yearFrom the start year of the period
     * @param yearTo   the end year of the period
     * @return a list of ExpenseYearDto objects representing the expense years
     */
    private List<ExpenseYearDto> repoToExpenseYearDto(Integer yearFrom, Integer yearTo) {
        List<Object[]> repo = expenseRepository.findAllByPeriodGroupByYearMonth(yearFrom, yearTo);
        return repo.stream()
                .map(result -> new ExpenseYearDto((Integer) result[0],
                        (Integer) result[1],
                        (BigDecimal) result[2],
                        (String) result[TRHEE],
                        (Integer) result[FOUR],
                        (Integer) result[FIVE]))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the last bill record.
     *
     * @return a LastBillRecordDto object representing the last bill record
     */
    private LastBillRecordDto getLastBillRecord() {
        Optional<BillRecordEntity> optionalBillRecordEntity = billRecordRepository.findFirstByEnabledTrueOrderByEndDesc();
        LastBillRecordDto lastBillRecordDto = new LastBillRecordDto();
        if (optionalBillRecordEntity.isPresent()) {
            BillRecordEntity billRecordEntity = optionalBillRecordEntity.get();
            lastBillRecordDto.setEndDate(billRecordEntity.getEnd());
            lastBillRecordDto.setStartDate(billRecordEntity.getStart());
            lastBillRecordDto.setId(billRecordEntity.getId());
        }
        lastBillRecordDto.setBills(new ArrayList<>());
        lastBillRecordDto.setFineAmount(BigDecimal.ZERO);
        lastBillRecordDto.setPendingAmount(BigDecimal.ZERO);
        return lastBillRecordDto;
    }

    /**
     * Retrieves the list of bill installments for a given bill ID.
     *
     * @param billId the ID of the bill
     * @return a list of DtoExpenseKPI objects representing the bill installments
     */
    private List<DtoExpenseKPI> getBillsInstallments(Integer billId) {
        List<Object[]> repo = billExpenseInstallmentsRepository.findByBillIdGroupByTypeAndCategory(billId);
        return repo.stream()
                .map(result -> new DtoExpenseKPI((String) result[0], (Integer) result[1], (String) result[2],
                        (BigDecimal) result[TRHEE], (Integer) result[FOUR])).collect(Collectors.toList());
    }

    /**
     * Retrieves the pending amount for the bill.
     *
     * @return the pending amount as a BigDecimal
     */
    private BigDecimal getBillPendingAmount() {
        Object[] repo = billRecordRepository.findAmountPendingBill();
        BigDecimal pendingAmount = (BigDecimal) repo[0];
        return pendingAmount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Retrieves the fine amount for a given bill ID.
     *
     * @param billId the ID of the bill
     * @return the fine amount as a BigDecimal
     */
    private BigDecimal getBillFindAmount(Integer billId) {
        Object[] repo = billRecordRepository.findAmountFindByBillRecordId(billId);
        BigDecimal findAmount = (BigDecimal) repo[0];
        if (findAmount == null) {
            findAmount = BigDecimal.ZERO;
        }
        return findAmount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Validates the given period.
     *
     * @param periodDto the period to validate
     * @throws CustomException if the period is null,
     * the start date is null, the end date is null, or the start date is after
     * the end date
     */
    private void validPeriod(PeriodDto periodDto) {
        // Validate that the period is not null
        if (periodDto == null) {
            throw new CustomException("The period be can't null", HttpStatus.BAD_REQUEST);
        }
        // Validate that the start date is not null
        if (periodDto.getStartDate() == null) {
            throw new CustomException("The start date be can't null", HttpStatus.BAD_REQUEST);
        }
        if (periodDto.getEndDate() == null) {
            throw new CustomException("The end date be can't null", HttpStatus.BAD_REQUEST);
        }
        // Validate that the start date is before the end date
        if (periodDto.getStartDate().isAfter(periodDto.getEndDate())) {
            throw new CustomException("The start date must be earlier than the end date.", HttpStatus.BAD_REQUEST);
        }
    }
}
