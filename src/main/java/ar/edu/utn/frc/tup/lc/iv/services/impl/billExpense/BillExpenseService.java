package ar.edu.utn.frc.tup.lc.iv.services.impl.billExpense;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.response.BillExpenseDto;
import ar.edu.utn.frc.tup.lc.iv.entities.BillRecordEntity;
import ar.edu.utn.frc.tup.lc.iv.models.BillExpenseOwnerModel;
import ar.edu.utn.frc.tup.lc.iv.models.BillRecordModel;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseModel;
import ar.edu.utn.frc.tup.lc.iv.repositories.BillRecordRepository;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IExpenseService;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.billExpense.IBillExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * Service class for managing bill expenses.
 */
@Service
@RequiredArgsConstructor
public class BillExpenseService implements IBillExpenseService {

    /**
     * Repository for handling BillRecord entities.
     */
    private final BillRecordRepository billRecordRepository;


    /**
     * Service for handling expense-related operations.
     */
    private final IExpenseService expenseService;

    /**
     * Repository for handling BillExpenseInstallments entities.
     */
    private final BillExpenseDistributionService billExpenseDistributionService;
    /**
     * Service for validating bill expense periods.
     */
    private final PeriodBillExpenseValidation periodBillExpenseValidation;

    /**
     * Service for handling bill expense data operations.
     */
    private final BillExpenseDataService billExpenseDataService;

    /**
     * Service for mapping bill expense entities and models.
     */
    private final BillExpenseMappersService billExpenseMappersService;

    /**
     * Generates a new BillExpense for a specific period.
     * If a BillRecord already exists for the period,
     * it returns the existing BillRecord. If no record exists, it validates
     * the period, calculates
     * the expenses, and saves a new BillRecord.
     *
     * @param periodDto {@link PeriodDto} Contains the start
     * and end date for the period.
     * @return {@link BillExpenseDto} The generated or existing BillExpense.
     */
    @Override
    public BillExpenseDto generateBillExpense(PeriodDto periodDto) {
        // Validate that the period is well-defined and does not include today
        periodBillExpenseValidation.validatePeriod(periodDto);
        BillRecordModel billRecordModel = billExpenseDataService.getBillRecord(periodDto);

        if (billRecordModel == null) {
            // Validate that there is no overlap with other BillRecords
            if (billExpenseDataService.existBillRecordInPeriod(periodDto)) {
                throw new CustomException("The specified period overlaps with an existing generated one", HttpStatus.CONFLICT);
            }
            // Calculate the bill expense for the given period
            billRecordModel = calculateBillExpense(periodDto);
            // Save the new BillRecord in the database
            billRecordModel = saveBillRecord(billRecordModel);
        }

        return billExpenseMappersService.billRecordModelToDto(billRecordModel);
    }

    /**
     * Retrieves all expenses with installment payment dates within
     * the specified period.
     *
     * @param periodDto {@link PeriodDto} Contains the start and
     * end date for the period.
     * @return List of {@link ExpenseModel} with applicable expenses.
     */
    private List<ExpenseModel> getExpenses(PeriodDto periodDto) {
        return expenseService.getExpenseByPaymentDateRange(periodDto.getStartDate(), periodDto.getEndDate());
    }

    /**
     * Calculates the BillExpense for a specified period by
     * distributing expenses among the owners.
     * This version optimizes the expense distribution by
     * combining the logic for individual,
     * non-individual, and note of credit expenses into a single loop.
     *
     * @param periodDto {@link PeriodDto} Contains the start and end
     * date for the period.
     * @return {@link BillRecordModel} The calculated bill.
     */
    private BillRecordModel calculateBillExpense(PeriodDto periodDto) {
        BillRecordModel result = BillRecordModel.builder()
                .start(periodDto.getStartDate())
                .end(periodDto.getEndDate())
                .build();

        Integer createUser = 1;
        result.setCreatedUser(createUser);

        Integer updateUser = 1;
        result.setLastUpdatedUser(updateUser);

        // Retrieve the applicable expenses and distribute them among owners
        List<ExpenseModel> expenseModels = getExpenses(periodDto);
        result.setBillExpenseOwner(billExpenseDataService.constraintBillExpenseOwners(periodDto, createUser));

        // Calculate total field size across all owners
        Integer totalSize = result.getBillExpenseOwner().stream().mapToInt(BillExpenseOwnerModel::getFieldSize).sum();

        // Iterate over all expenses and handle both individual
        // and non-individual types in a single loop
        for (ExpenseModel expense : expenseModels) {
            if (expense.getDistributions().isEmpty()) {
                // Handle non-individual expenses and note of credit without
                // specific distributions
                billExpenseDistributionService.distributeNonIndividualExpense(result, expense, totalSize, createUser);
            } else {
                // Handle individual expenses and note of credit with
                // specific distributions
                billExpenseDistributionService.distributeIndividualExpense(result, expense, createUser);
            }
        }

        return result;
    }


    /**
     * Saves a BillRecord to the database and returns the saved model.
     *
     * @param billRecordModel The BillRecord to save.
     * @return {@link BillRecordModel} The saved BillRecordModel.
     */
    @Transactional
    protected BillRecordModel saveBillRecord(BillRecordModel billRecordModel) {
        // Map BillRecordModel to BillRecordEntity for database storage
        BillRecordEntity billRecordEntity = billExpenseMappersService.billRecordModelToEntity(billRecordModel);
        billRecordRepository.save(billRecordEntity);
        return billExpenseMappersService.entityToBillRecordModel(billRecordEntity);
    }
}

