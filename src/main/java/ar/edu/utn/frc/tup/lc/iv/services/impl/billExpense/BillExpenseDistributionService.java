package ar.edu.utn.frc.tup.lc.iv.services.impl.billExpense;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.models.BillExpenseInstallmentModel;
import ar.edu.utn.frc.tup.lc.iv.models.BillExpenseOwnerModel;
import ar.edu.utn.frc.tup.lc.iv.models.BillRecordModel;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseDistributionModel;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseInstallmentModel;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseModel;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.billExpense.IBillExpenseDistributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
/**
 * Service class for managing distribution for BillRecord.
 */
@Service
@RequiredArgsConstructor
public class BillExpenseDistributionService implements IBillExpenseDistributionService {
    /**
     * The ID of the user who created the record.
     */
    private Integer createUser;

    /**
     * Distributes non-individual expenses proportionally
     *  among all owners based on their field size.
     *
     * @param billRecordModel The BillRecord to which expenses will be added.
     * @param expenseModel The non-individual expense to distribute.
     * @param totalSize The total field size of all owners.
     * @param paramCreateUser User Id.
     */
    @Override
    public void distributeNonIndividualExpense(BillRecordModel billRecordModel,
                                               ExpenseModel expenseModel, Integer totalSize,
                                               Integer paramCreateUser) {
        this.createUser = paramCreateUser;
        // Calculate the amount to distribute for each installment
        BigDecimal amountToInstall = getAmountToInstall(expenseModel);

        // Distribute the expense proportionally to each owner based on their field size
        for (BillExpenseOwnerModel billExpenseOwnerModel : billRecordModel.getBillExpenseOwner()) {
            BigDecimal proportionFieldSize = getProportionFieldSize(totalSize, billExpenseOwnerModel.getFieldSize());
            BigDecimal amount = amountToInstall.multiply(proportionFieldSize).setScale(2, RoundingMode.HALF_UP);
            expensesDistribution(billExpenseOwnerModel, expenseModel, billRecordModel.getStart(), billRecordModel.getEnd(), amount);
        }
    }

    /**
     * Distributes individual expenses based on the
     *  specific proportion assigned to each owner in the expense's distribution.
     *
     * @param billRecordModel The BillRecord to which the individual
     * expenses will be added.
     * @param expenseModel The individual expense to distribute.
     * @param paramCreateUser User Id.
     */
    @Override
    public void distributeIndividualExpense(BillRecordModel billRecordModel, ExpenseModel expenseModel,
                                            Integer paramCreateUser) {
        this.createUser = paramCreateUser;
        // Calculate the amount to distribute for each installment
        BigDecimal amountToInstall = getAmountToInstall(expenseModel);

        // Distribute the expense only to the owners listed in the expense's distribution
        for (BillExpenseOwnerModel billExpenseOwnerModel
                : billRecordModel.getBillExpenseOwner().stream().filter(m -> expenseModel.getDistributions().stream()
                .anyMatch(l -> l.getOwnerId().equals(m.getOwnerId()))).toList()) {

            // Find the specific distribution entry for the owner
            ExpenseDistributionModel expenseDistributionModel = expenseModel.getDistributions().stream()
                    .filter(m -> m.getOwnerId().equals(billExpenseOwnerModel.getOwnerId())).findFirst().get();

            BigDecimal amountToProportion = amountToInstall
                    .multiply(expenseDistributionModel.getProportion()).setScale(2, RoundingMode.HALF_UP);
            expensesDistribution(billExpenseOwnerModel, expenseModel,
                    billRecordModel.getStart(), billRecordModel.getEnd(), amountToProportion);
        }
    }

    /**
     * Calculates the amount to charge for each installment of an expense.
     *
     * @param expenseModel The expense for which the
     *  installment amount is being calculated.
     * @return {@link BigDecimal} The amount per installment.
     */
    public BigDecimal getAmountToInstall(ExpenseModel expenseModel) {
        // Total amount divided by the number of installments
        BigDecimal amount = expenseModel.getAmount();
        BigDecimal installments = BigDecimal.valueOf(expenseModel.getInstallments());
        if (installments.compareTo(BigDecimal.ZERO) <= 0) {

            throw new CustomException("The installments must be greater than zero", HttpStatus.BAD_REQUEST);
        }
        return amount.divide(installments, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the proportional field size for an owner
     *  relative to the total field size of all owners.
     *
     * @param totalFieldSize The total field size of all owners.
     * @param ownerFieldSize The field size of the specific owner.
     * @return {@link BigDecimal} The proportion of the owner's
     *  field size relative to the total.
     */
    public BigDecimal getProportionFieldSize(Integer totalFieldSize, Integer ownerFieldSize) {
        // Proportion = (ownerFieldSize / totalFieldSize)
        BigDecimal ownerProportion = BigDecimal.valueOf(ownerFieldSize);
        BigDecimal totalSize = BigDecimal.valueOf(totalFieldSize);
        ownerProportion = ownerProportion.divide(totalSize, 2, RoundingMode.HALF_UP);
        return ownerProportion;
    }

    /**
     * Distributes the specified amount of an expense to an owner's installments,
     *  adjusting for the billing period.
     *
     * @param billExpenseOwnerModel The owner to whom the expense
     * is being distributed.
     * @param expenseModel  The expense being distributed.
     * @param startDate The start date of the billing period.
     * @param endDate The end date of the billing period.
     * @param amount The amount to distribute to the owner.
     */
    public void expensesDistribution(BillExpenseOwnerModel billExpenseOwnerModel, ExpenseModel expenseModel,
                                     LocalDate startDate, LocalDate endDate, BigDecimal amount) {
        // Filter the installments that fall within the billing period
        for (ExpenseInstallmentModel installmentModel
                : getExpenseInstallmentsFilter(expenseModel.getInstallmentsList(), startDate, endDate)) {
            String description = expenseModel.getCategory().getDescription() + " - "
                    + expenseModel.getDescription();
            BillExpenseInstallmentModel billExpenseInstallmentModel = BillExpenseInstallmentModel.builder()
                    .amount(amount)
                    .expenseInstallment(installmentModel)
                    .description(description)
                    .expenseType(expenseModel.getExpenseType())
                    .build();

            billExpenseInstallmentModel.setCreatedUser(createUser);
            billExpenseInstallmentModel.setLastUpdatedUser(createUser);

            // Add the calculated installment to the owner's bill
            billExpenseOwnerModel.getBillExpenseInstallments().add(billExpenseInstallmentModel);
        }
    }

    /**
     * Filters the list of installments to only include those
     * that fall within the specified billing period.
     *
     * @param expenseInstallmentModels The list of installments to filter.
     * @param startDate The start date of the billing period.
     * @param endDate The end date of the billing period.
     * @return A list of {@link ExpenseInstallmentModel} that fall
     * within the billing period.
     */
    public List<ExpenseInstallmentModel> getExpenseInstallmentsFilter(List<ExpenseInstallmentModel> expenseInstallmentModels,
                                                                      LocalDate startDate, LocalDate endDate) {
        return expenseInstallmentModels.stream()
                .filter(installment -> !installment.getPaymentDate().isBefore(startDate)
                        && !installment.getPaymentDate().isAfter(endDate)).toList();
    }

}
