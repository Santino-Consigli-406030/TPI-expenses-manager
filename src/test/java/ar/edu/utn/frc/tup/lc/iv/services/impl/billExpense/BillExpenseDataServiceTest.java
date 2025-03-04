package ar.edu.utn.frc.tup.lc.iv.services.impl.billExpense;

import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.owner.OwnerDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.owner.PlotDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.sanction.FineDto;
import ar.edu.utn.frc.tup.lc.iv.entities.BillRecordEntity;
import ar.edu.utn.frc.tup.lc.iv.models.BillExpenseOwnerModel;
import ar.edu.utn.frc.tup.lc.iv.models.BillRecordModel;
import ar.edu.utn.frc.tup.lc.iv.repositories.BillRecordRepository;
import ar.edu.utn.frc.tup.lc.iv.services.impl.FineService;
import ar.edu.utn.frc.tup.lc.iv.services.impl.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BillExpenseDataServiceTest {

    @Mock
    private BillRecordRepository billRecordRepository;

    @Mock
    private OwnerService ownerService;

    @Mock
    private FineService fineService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BillExpenseDataService billExpenseDataService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetBillRecord_ExistingRecord() {
        // Arrange
        PeriodDto periodDto = new PeriodDto(); // Asegúrate de asignar fechas de prueba
        BillRecordEntity billRecordEntity = new BillRecordEntity();
        BillRecordModel expectedModel = new BillRecordModel();

        when(billRecordRepository.findFirstByStartAndEndAndEnabledTrue(periodDto.getStartDate(), periodDto.getEndDate()))
                .thenReturn(Optional.of(billRecordEntity));
        when(modelMapper.map(billRecordEntity, BillRecordModel.class)).thenReturn(expectedModel);

        // Act
        BillRecordModel result = billExpenseDataService.getBillRecord(periodDto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedModel, result);
    }

    @Test
    public void testGetBillRecord_NoRecord() {
        // Arrange
        PeriodDto periodDto = new PeriodDto();

        when(billRecordRepository.findFirstByStartAndEndAndEnabledTrue(periodDto.getStartDate(), periodDto.getEndDate()))
                .thenReturn(Optional.empty());

        // Act
        BillRecordModel result = billExpenseDataService.getBillRecord(periodDto);

        // Assert
        assertNull(result);
    }

    @Test
    public void testExistBillRecordInPeriod_RecordExists() {
        // Arrange
        PeriodDto periodDto = new PeriodDto();

        when(billRecordRepository.findAnyByStartAndEnd(periodDto.getStartDate(), periodDto.getEndDate()))
                .thenReturn(List.of(new BillRecordEntity()));

        // Act
        boolean result = billExpenseDataService.existBillRecordInPeriod(periodDto);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testExistBillRecordInPeriod_NoRecord() {
        // Arrange
        PeriodDto periodDto = new PeriodDto();

        when(billRecordRepository.findAnyByStartAndEnd(periodDto.getStartDate(), periodDto.getEndDate()))
                .thenReturn(Collections.emptyList());

        // Act
        boolean result = billExpenseDataService.existBillRecordInPeriod(periodDto);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testConstraintBillExpenseOwners() {
        // Arrange
        PeriodDto periodDto = new PeriodDto();
        Integer createUser = 1;

        // Configuración de OwnerDto y PlotDto usando setters
        OwnerDto owner = new OwnerDto();
        owner.setId(1);
        owner.setName("Owner1");

        PlotDto plot = new PlotDto();
        plot.setId(1);
        plot.setFieldSize(100);
        owner.setPlots(List.of(plot));

        // Configuración de FineDto usando setters
        FineDto fine = new FineDto();
        fine.setId(1);
        fine.setPlotId(1);
        fine.setAmount(BigDecimal.valueOf(60));
        fine.setDescription("Fine for plot 1");

        List<OwnerDto> owners = List.of(owner);
        List<FineDto> fines = List.of(fine);

        when(ownerService.getOwners()).thenReturn(owners);
        when(fineService.getFineByPeriod(periodDto)).thenReturn(fines);

        // Act
        List<BillExpenseOwnerModel> result = billExpenseDataService.constraintBillExpenseOwners(periodDto, createUser);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(createUser, result.get(0).getCreatedUser());
        assertEquals(BigDecimal.valueOf(60), result.get(0).getBillExpenseFines().get(0).getAmount());
    }

    @Test
    public void testGetOwners_ServiceUnavailable() {
        // Arrange
        when(ownerService.getOwners()).thenThrow(new CustomException("Service unavailable", HttpStatus.SERVICE_UNAVAILABLE));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> billExpenseDataService.getOwners());
        assertEquals("Service unavailable", exception.getMessage());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    public void testGetOwners_NotFound() {
        // Arrange
        when(ownerService.getOwners()).thenReturn(Collections.emptyList());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> billExpenseDataService.getOwners());
        assertEquals("No owners found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void testGetFines_ServiceUnavailable() {
        // Arrange
        PeriodDto periodDto = new PeriodDto();

        when(fineService.getFineByPeriod(periodDto)).thenThrow(new CustomException("Service unavailable", HttpStatus.SERVICE_UNAVAILABLE));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> billExpenseDataService.getFines(periodDto));
        assertEquals("Service unavailable", exception.getMessage());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }
}