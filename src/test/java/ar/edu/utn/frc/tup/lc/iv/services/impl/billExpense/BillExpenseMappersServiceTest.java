package ar.edu.utn.frc.tup.lc.iv.services.impl.billExpense;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.response.BillExpenseDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.response.BillOwnerDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseQuery;
import ar.edu.utn.frc.tup.lc.iv.entities.BillExpenseFineEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.BillExpenseOwnerEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.BillRecordEntity;
import ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType;
import ar.edu.utn.frc.tup.lc.iv.models.BillExpenseInstallmentModel;
import ar.edu.utn.frc.tup.lc.iv.models.BillExpenseOwnerModel;
import ar.edu.utn.frc.tup.lc.iv.models.BillRecordModel;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseInstallmentModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class BillExpenseMappersServiceTest {

    @Mock
    private BillExpInstallmentService billExpInstallmentService;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private BillExpenseMappersService billExpenseMappersService;

    @Test
    void billRecordModelToEntity_ShouldMapModelToEntity_WithCorrectRelationships() {

        BillRecordModel billRecordModel = createBillRecordModelWithOwners();

        BillRecordEntity result = billExpenseMappersService.billRecordModelToEntity(billRecordModel);

        assertNotNull(result);
        assertEquals(billRecordModel.getBillExpenseOwner().size(), result.getBillExpenseOwner().size());

        result.getBillExpenseOwner().forEach(owner -> {
            assertNotNull(owner.getBillRecord());
            owner.getBillExpenseFines().forEach(fine -> assertEquals(owner, fine.getBillExpenseOwner()));
            owner.getBillExpenseInstallments().forEach(installment -> assertEquals(owner, installment.getBillExpenseOwner()));
        });
    }

    @Test
    void billRecordModelToDto_ShouldReturnDtoWithOwners() {

        BillRecordModel billRecordModel = createBillRecordModelWithOwners();
        Map<Integer, DtoExpenseQuery> installmentsType = createInstallmentsTypeMap();
        when(billExpInstallmentService.getInstallmentAndExpenseType(anyInt())).thenReturn(installmentsType);

        BillExpenseDto result = billExpenseMappersService.billRecordModelToDto(billRecordModel);

        assertNotNull(result);
        assertEquals(billRecordModel.getBillExpenseOwner().size(), result.getOwners().size());
    }


    private BillRecordModel createBillRecordModelWithOwners() {
        BillRecordModel model = new BillRecordModel();
        model.setId(1);
        model.setStart(LocalDate.now().plusDays(-2));
        model.setEnd(LocalDate.now().plusDays(-1));
        model.setBillExpenseOwner(List.of(createOwnerModelWithInstallments()));
        return model;
    }

    private BillExpenseOwnerModel createOwnerModelWithInstallments() {
        BillExpenseOwnerModel ownerModel = new BillExpenseOwnerModel();
        ownerModel.setOwnerId(1);
        ownerModel.setId(1);
        ownerModel.setFieldSize(100);
        ownerModel.setBillExpenseInstallments(List.of(BillExpenseInstallmentModel.builder().
                expenseType(ExpenseType.COMUN).
                amount(BigDecimal.TEN).
                description("Installment 1").
                id(1).
                expenseInstallment(ExpenseInstallmentModel.builder().id(1).build())
                .build()));
        ownerModel.setBillExpenseFines(new ArrayList<>());
        return ownerModel;
    }

    private Map<Integer, DtoExpenseQuery> createInstallmentsTypeMap() {
        Map<Integer, DtoExpenseQuery> map = new HashMap<>();
        DtoExpenseQuery query = new DtoExpenseQuery();
        query.setExpenseType("COMUN");
        query.setCategory("Category 1");
        map.put(1, query);
        return map;
    }

    @Test
    void billRecordModelToDto_ShouldThrowException_WhenInstallmentServiceFails() {
        BillRecordModel billRecordModel = createBillRecordModelWithOwners();
        when(billExpInstallmentService.getInstallmentAndExpenseType(anyInt()))
                .thenThrow(new RuntimeException("Service Error"));

        CustomException exception = assertThrows(CustomException.class,
                () -> billExpenseMappersService.billRecordModelToDto(billRecordModel));

        assertEquals("Error occurred processing the bill record. The record was created or already exists, but the return process failed.",
                exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }
    @Test
    void entityToBillRecordModel_ShouldReturnCorrectModel() {
        BillRecordEntity entity = new BillRecordEntity();
        entity.setId(1);

        BillRecordModel result = billExpenseMappersService.entityToBillRecordModel(entity);

        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
    }


}