package ar.edu.utn.frc.tup.lc.iv.services.impl.billExpense;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType;
import ar.edu.utn.frc.tup.lc.iv.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BillExpenseDistributionServiceTest {
    @InjectMocks
    private BillExpenseDistributionService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testGetAmountToInstall() {
        ExpenseModel expenseModel = new ExpenseModel();
        expenseModel.setAmount(BigDecimal.valueOf(500.00));
        expenseModel.setInstallments(5);

        BigDecimal result = service.getAmountToInstall(expenseModel);

        assertEquals(0, result.compareTo(BigDecimal.valueOf(100.0)));
    }

    @Test
    public void testGetAmountToInstall_ZeroInstallments() {
        ExpenseModel expenseModel = new ExpenseModel();
        expenseModel.setAmount(BigDecimal.valueOf(500.00));
        expenseModel.setInstallments(0);

        Exception exception = assertThrows(CustomException.class, () -> service.getAmountToInstall(expenseModel));
        assertEquals("The installments must be greater than zero", exception.getMessage());
    }

    @Test
    public void testGetProportionFieldSize() {
        Integer totalSize = 500;
        Integer ownerFieldSize = 100;

        BigDecimal result = service.getProportionFieldSize(totalSize, ownerFieldSize);

        assertEquals(BigDecimal.valueOf(0.20).setScale(2, RoundingMode.HALF_UP), result);
    }

    @Test
    public void testExpensesDistribution() {
        BillExpenseOwnerModel billExpenseOwnerModel = new BillExpenseOwnerModel();
        billExpenseOwnerModel.setBillExpenseInstallments(new ArrayList<>());

        ExpenseModel expenseModel = new ExpenseModel();
        expenseModel.setExpenseType(ExpenseType.COMUN);

        ExpenseCategoryModel category = new ExpenseCategoryModel();
        category.setDescription("Maintenance");
        expenseModel.setCategory(category);
        expenseModel.setDescription("Monthly Maintenance Fee");

        ExpenseInstallmentModel installmentModel = new ExpenseInstallmentModel();
        installmentModel.setPaymentDate(LocalDate.of(2024, 6, 1));
        expenseModel.setInstallmentsList(List.of(installmentModel));

        BigDecimal amount = BigDecimal.valueOf(100.00);

        service.expensesDistribution(billExpenseOwnerModel, expenseModel, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), amount);

        assertFalse(billExpenseOwnerModel.getBillExpenseInstallments().isEmpty());
        assertEquals(amount, billExpenseOwnerModel.getBillExpenseInstallments().get(0).getAmount());
        assertEquals("Maintenance - Monthly Maintenance Fee", billExpenseOwnerModel.getBillExpenseInstallments().get(0).getDescription());
    }
    @Test
    public void testGetExpenseInstallmentsFilter() {

        ExpenseInstallmentModel installment1 = new ExpenseInstallmentModel();
        installment1.setPaymentDate(LocalDate.of(2024, 1, 1));

        ExpenseInstallmentModel installment2 = new ExpenseInstallmentModel();
        installment2.setPaymentDate(LocalDate.of(2024, 5, 1));

        ExpenseInstallmentModel installment3 = new ExpenseInstallmentModel();
        installment3.setPaymentDate(LocalDate.of(2024, 8, 1));

        List<ExpenseInstallmentModel> expenseInstallments = List.of(installment1, installment2, installment3);

        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 1);

        List<ExpenseInstallmentModel> result = service.getExpenseInstallmentsFilter(expenseInstallments, startDate, endDate);

        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2024, 1, 1), result.get(0).getPaymentDate());
        assertEquals(LocalDate.of(2024, 5, 1), result.get(1).getPaymentDate());
    }
    @Test
    public void testDistributeNonIndividualExpense() {
        BillRecordModel billRecordModel = new BillRecordModel();
        billRecordModel.setId(1);
        billRecordModel.setStart(LocalDate.of(2024, 1, 1));
        billRecordModel.setEnd(LocalDate.of(2024, 1, 5));
        billRecordModel.setBillExpenseOwner(new ArrayList<>());

        BillExpenseOwnerModel owner1 = new BillExpenseOwnerModel();
        owner1.setFieldSize(100);
        owner1.setBillExpenseInstallments(new ArrayList<>());

        BillExpenseOwnerModel owner2 = new BillExpenseOwnerModel();
        owner2.setFieldSize(200);
        owner2.setBillExpenseInstallments(new ArrayList<>());

        billRecordModel.getBillExpenseOwner().add(owner1);
        billRecordModel.getBillExpenseOwner().add(owner2);

        ExpenseModel expenseModel = new ExpenseModel();
        expenseModel.setAmount(BigDecimal.valueOf(300.00));
        expenseModel.setInstallments(3);
        expenseModel.setExpenseType(ExpenseType.COMUN);
        expenseModel.setDescription("Monthly Maintenance Fee");
        expenseModel.setCategory(ExpenseCategoryModel.builder()
                        .description("Maintenance")
                        .id(1)
                        .enabled(true)
                        .build());
        expenseModel.setInstallmentsList(new ArrayList<>());
        expenseModel.getInstallmentsList().add(
            ExpenseInstallmentModel.builder()
                    .id(1)
                    .enabled(true)
                    .installmentNumber(1)
                    .paymentDate(LocalDate.of(2024, 1, 2))
                    .build()
        );
        expenseModel.getInstallmentsList().add(
                ExpenseInstallmentModel.builder()
                        .id(2)
                        .enabled(true)
                        .installmentNumber(2)
                        .paymentDate(LocalDate.of(2024, 2, 2))
                        .build()
        );
        expenseModel.getInstallmentsList().add(
                ExpenseInstallmentModel.builder()
                        .id(3)
                        .enabled(true)
                        .installmentNumber(3)
                        .paymentDate(LocalDate.of(2024, 3, 2))
                        .build()
        );

        service.distributeNonIndividualExpense(billRecordModel, expenseModel, 300, 1);

        assertEquals(1, owner1.getBillExpenseInstallments().size());
        assertEquals(1, owner2.getBillExpenseInstallments().size());
        assertEquals(BigDecimal.valueOf(33).setScale(2), owner1.getBillExpenseInstallments().get(0).getAmount());
        assertEquals(BigDecimal.valueOf(67).setScale(2), owner2.getBillExpenseInstallments().get(0).getAmount());
    }
    @Test
    public void testGetProportionFieldSize_ZeroOwnerFieldSize() {
        BigDecimal result = service.getProportionFieldSize(100, 0);
        assertEquals(BigDecimal.ZERO.setScale(2), result);
    }
    @Test
    public void testGetProportionFieldSize_ZeroTotalFieldSize() {
        Exception exception = assertThrows(ArithmeticException.class, () -> service.getProportionFieldSize(0, 50));
        assertEquals("/ by zero", exception.getMessage());
    }
    @Test
    public void testExpensesDistribution_EmptyInstallmentsList() {
        BillExpenseOwnerModel owner = new BillExpenseOwnerModel();
        owner.setBillExpenseInstallments(new ArrayList<>());

        ExpenseModel expenseModel = new ExpenseModel();
        expenseModel.setInstallmentsList(new ArrayList<>());
        service.expensesDistribution(owner, expenseModel, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), BigDecimal.valueOf(100.00));

        assertTrue(owner.getBillExpenseInstallments().isEmpty());
    }
    @Test
    public void testDistributeIndividualExpense() {
        BillRecordModel billRecordModel = new BillRecordModel();
        billRecordModel.setId(1);
        billRecordModel.setStart(LocalDate.of(2024, 1, 1));
        billRecordModel.setEnd(LocalDate.of(2024, 12, 31));
        billRecordModel.setBillExpenseOwner(new ArrayList<>());

        BillExpenseOwnerModel owner1 = new BillExpenseOwnerModel();
        owner1.setOwnerId(1);
        owner1.setFieldSize(100);
        owner1.setBillExpenseInstallments(new ArrayList<>());

        BillExpenseOwnerModel owner2 = new BillExpenseOwnerModel();
        owner2.setOwnerId(2);
        owner2.setFieldSize(200);
        owner2.setBillExpenseInstallments(new ArrayList<>());

        billRecordModel.getBillExpenseOwner().add(owner1);
        billRecordModel.getBillExpenseOwner().add(owner2);

        ExpenseModel expenseModel = new ExpenseModel();
        expenseModel.setAmount(BigDecimal.valueOf(300.00));
        expenseModel.setInstallments(1);
        expenseModel.setExpenseType(ExpenseType.INDIVIDUAL);
        expenseModel.setDescription("Electricity Bill");
        expenseModel.setCategory(ExpenseCategoryModel.builder()
                .description("Utilities")
                .id(2)
                .enabled(true)
                .build());

        ExpenseDistributionModel distribution1 = new ExpenseDistributionModel();
        distribution1.setOwnerId(1);
        distribution1.setProportion(new BigDecimal("0.30"));

        ExpenseDistributionModel distribution2 = new ExpenseDistributionModel();
        distribution2.setOwnerId(2);
        distribution2.setProportion(new BigDecimal("0.70"));

        expenseModel.setDistributions(List.of(distribution1, distribution2));

        expenseModel.setInstallmentsList(List.of(
                ExpenseInstallmentModel.builder()
                        .id(1)
                        .enabled(true)
                        .installmentNumber(1)
                        .paymentDate(LocalDate.of(2024, 1, 15))
                        .build()
        ));

        service.distributeIndividualExpense(billRecordModel, expenseModel, 1);

        assertEquals(1, owner1.getBillExpenseInstallments().size());
        assertEquals(1, owner2.getBillExpenseInstallments().size());
        assertEquals(BigDecimal.valueOf(90.00).setScale(2), owner1.getBillExpenseInstallments().get(0).getAmount());
        assertEquals(BigDecimal.valueOf(210.00).setScale(2), owner2.getBillExpenseInstallments().get(0).getAmount());

        assertEquals("Utilities - Electricity Bill", owner1.getBillExpenseInstallments().get(0).getDescription());
        assertEquals(ExpenseType.INDIVIDUAL, owner1.getBillExpenseInstallments().get(0).getExpenseType());

        assertEquals("Utilities - Electricity Bill", owner2.getBillExpenseInstallments().get(0).getDescription());
        assertEquals(ExpenseType.INDIVIDUAL, owner2.getBillExpenseInstallments().get(0).getExpenseType());
    }
}
