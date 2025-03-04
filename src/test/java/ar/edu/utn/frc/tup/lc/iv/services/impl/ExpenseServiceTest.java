package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.client.FileManagerRestClient;
import ar.edu.utn.frc.tup.lc.iv.client.OwnerRestClient;
import ar.edu.utn.frc.tup.lc.iv.client.ProviderRestClient;
import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.*;
import ar.edu.utn.frc.tup.lc.iv.dtos.fileManager.UuidResponseDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.owner.OwnerDto;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseDistributionEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseInstallmentEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseCategoryEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.BillExpenseInstallmentsEntity;

import ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseCategoryModel;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseDistributionModel;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseInstallmentModel;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseModel;
import ar.edu.utn.frc.tup.lc.iv.repositories.*;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IProviderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;


/**
 * Test class for the ExpenseService.
 */
public class ExpenseServiceTest {
    /**
     * Injects the ExpenseService instance for testing.
     */
    @InjectMocks
    private ExpenseService expenseService;

    /**
     * Mocks the ExpenseRepository for testing.
     */
    @Mock
    private ExpenseRepository expenseRepository;


    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;
    @Mock
    private FileManagerRestClient fileManagerRestClient;
    /**
     * Mocks the ExpenseDistributionRepository for testing.
     */
    @Mock
    private ExpenseDistributionRepository expenseDistributionRepository;
    @Mock
    private IProviderService providerService;
    @Mock
    private OwnerRestClient ownerRestClient;
    /**
     * Mocks the ExpenseInstallmentRepository for testing.
     */
    @Mock
    private ExpenseInstallmentRepository expenseInstallmentRepository;

    /**
     * Mocks the ModelMapper for testing.
     */
    @Spy
    private ModelMapper modelMapper;

    /**
     * Mocks the ExpenseCategoryService for testing.
     */
    @Mock
    private ExpenseCategoryService expenseCategoryService;

    /**
     * Mocks the BillExpenseInstallmentsRepository for testing.
     */
    @Mock
    private BillExpenseInstallmentsRepository billExpenseInstallmentsRepository;

    @Mock
    private OwnerService ownerService;

    private DtoRequestExpense requestExpense;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestExpense = new DtoRequestExpense();
        requestExpense.setDescription("Test Expense");
        requestExpense.setProviderId(1);
        requestExpense.setExpenseDate(LocalDate.of(2024,11,15));
        requestExpense.setInvoiceNumber("123123");
        requestExpense.setTypeExpense("COMUN");
        requestExpense.setAmount(BigDecimal.valueOf(100));
        requestExpense.setInstallments(3);
        requestExpense.setDistributions(Collections.emptyList());
        requestExpense.setCategoryId(1);
    }

    @Test
    void postExpenseValidRequestReturnsOkResponse() {
        // Crear el DTO de solicitud válido
        DtoRequestExpense request = createValidDtoRequestExpense();

        // Crear y mockear el MultipartFile
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/jpeg");

        // Simular la respuesta de la carga del archivo (mock del client REST para subir archivos)
        ResponseEntity<UuidResponseDto> fileUploadResponse = mock(ResponseEntity.class);
        UuidResponseDto uuidResponse = mock(UuidResponseDto.class);
        when(uuidResponse.getUuid()).thenReturn(UUID.fromString(UUID.randomUUID().toString()));
        when(fileUploadResponse.getBody()).thenReturn(uuidResponse);

        // Mockear el método de carga de archivo en el cliente
        when(fileManagerRestClient.uploadFile(eq(file), isNull(), isNull())).thenReturn(fileUploadResponse);

        // Mockear el servicio de categoría de gasto
        ExpenseCategoryModel categoryModel = new ExpenseCategoryModel();
        categoryModel.setId(1);
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(categoryModel);

        // Mockear la consulta al repositorio de gastos
        when(expenseRepository.findFirstByInvoiceNumberAndProviderId(anyString(), anyInt()))
                .thenReturn(Optional.empty());

        // Crear y configurar una entidad de gasto válida
        ExpenseEntity validExpenseEntity = new ExpenseEntity();
        validExpenseEntity.setAmount(new BigDecimal("100.00"));
        validExpenseEntity.setCategory(new ExpenseCategoryEntity());
        validExpenseEntity.setDescription("Test Expense");
        validExpenseEntity.setExpenseDate(LocalDate.now());
        validExpenseEntity.setExpenseType(ExpenseType.COMUN);
        validExpenseEntity.setInvoiceNumber("909090");
        validExpenseEntity.setProviderId(1);
        validExpenseEntity.setInstallmentsList(new ArrayList<>());

        // Mockear la persistencia de datos
        when(expenseRepository.save(any())).thenReturn(validExpenseEntity);

        // Ejecutar el servicio
        ResponseEntity<DtoResponseExpense> response = expenseService.postExpense(request, file,1);

        // Verificar el resultado
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // Verificar las interacciones
        verify(expenseRepository, times(1)).save(any());
        verify(expenseInstallmentRepository, times(1)).save(any());
        verify(fileManagerRestClient, times(1)).uploadFile(eq(file), isNull(), isNull());
    }

    @Test
    void updateExpenseEntityTest() {
        // Configuración del ExpenseEntity existente
        ExpenseEntity existingExpense = new ExpenseEntity();
        existingExpense.setDistributions(new ArrayList<>());
        existingExpense.setInstallmentsList(new ArrayList<>());

        // Creación de distribuciones existentes y nuevas
        ExpenseDistributionEntity existingDistribution = new ExpenseDistributionEntity();
        existingDistribution.setOwnerId(1);  // Esta distribución se debe deshabilitar
        existingExpense.getDistributions().add(existingDistribution);

        // Nueva distribución que se debe agregar
        ExpenseDistributionModel newDistribution1 = new ExpenseDistributionModel();
        newDistribution1.setOwnerId(2);  // Nueva distribución
        newDistribution1.setProportion(new BigDecimal("10.00"));

        // Nueva distribución que debe actualizar la existente
        ExpenseDistributionModel newDistribution2 = new ExpenseDistributionModel();
        newDistribution2.setOwnerId(1);  // Debe actualizarse
        newDistribution2.setProportion(new BigDecimal("20.00"));

        List<ExpenseDistributionModel> newDistributions = Arrays.asList(newDistribution1, newDistribution2);

        // ExpenseModel (con información básica, y cuotas si es necesario)
        ExpenseModel expenseModel = new ExpenseModel();
        expenseModel.setDescription("Updated Expense");
        expenseModel.setExpenseDate(LocalDate.now());
        expenseModel.setAmount(new BigDecimal("200.00"));
        expenseModel.setExpenseType(ExpenseType.COMUN);
        expenseModel.setCategory(new ExpenseCategoryModel());
        expenseModel.setFileId(UUID.randomUUID());
        expenseModel.setProviderId(1);
        expenseModel.setInvoiceNumber("12345");
        expenseModel.setInstallmentsList(Arrays.asList(new ExpenseInstallmentModel()));

        // Ejecutar la actualización
        expenseService.updateExpenseEntity(existingExpense, expenseModel, newDistributions,1);

        // Verificar que las distribuciones existentes se actualizaron
        assertEquals(2, existingExpense.getDistributions().size()); // Ahora debería haber 2 distribuciones

        // Verificar que la distribución con ownerId 1 se haya deshabilitado
        ExpenseDistributionEntity updatedDistribution1 = existingExpense.getDistributions().stream()
                .filter(d -> d.getOwnerId().equals(1))
                .findFirst()
                .orElse(null);
        assertNotNull(updatedDistribution1);
        // Debe estar deshabilitada

        // Verificar que la distribución con ownerId 2 se haya agregado
        ExpenseDistributionEntity newDistributionEntity = existingExpense.getDistributions().stream()
                .filter(d -> d.getOwnerId().equals(2))
                .findFirst()
                .orElse(null);
        assertNotNull(newDistributionEntity);
        assertEquals(new BigDecimal("10.00"), newDistributionEntity.getProportion());
    }

    @Test
    void putExpenseValidRequestDistributionsReturnsOkResponse() {
        // Crear el DTO de solicitud válido
        DtoRequestExpense request = createValidDtoRequestExpense();
        request.setId(1);  // Asignar un ID para simular que es una actualización

        // Crear y mockear el MultipartFile
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/jpeg");

        // Simular la respuesta de la carga del archivo (mock del client REST para subir archivos)
        ResponseEntity<UuidResponseDto> fileUploadResponse = mock(ResponseEntity.class);
        UuidResponseDto uuidResponse = mock(UuidResponseDto.class);
        when(uuidResponse.getUuid()).thenReturn(UUID.fromString(UUID.randomUUID().toString()));
        when(fileUploadResponse.getBody()).thenReturn(uuidResponse);

        // Mockear el método de carga de archivo en el cliente
        when(fileManagerRestClient.uploadFile(eq(file), isNull(), isNull())).thenReturn(fileUploadResponse);

        // Mockear la consulta al repositorio de gastos
        Optional<ExpenseEntity> existingExpenseOpt = Optional.of(new ExpenseEntity());
        when(expenseRepository.findById(request.getId())).thenReturn(existingExpenseOpt);
        ExpenseCategoryEntity expenseCategoryEntity = new ExpenseCategoryEntity();
        expenseCategoryEntity.setId(1);

        // Crear y configurar una entidad de gasto válida (simulando que ya existe)
        ExpenseEntity validExpenseEntity = new ExpenseEntity();
        validExpenseEntity.setAmount(new BigDecimal("100.00"));
        validExpenseEntity.setCategory(expenseCategoryEntity);
        validExpenseEntity.setDescription("Test Expense");
        validExpenseEntity.setExpenseDate(LocalDate.now());
        validExpenseEntity.setExpenseType(ExpenseType.COMUN);
        validExpenseEntity.setInvoiceNumber("909090");
        validExpenseEntity.setProviderId(1);
        validExpenseEntity.setFileId(UUID.fromString(UUID.randomUUID().toString()));  // ID de archivo preexistente
        ExpenseInstallmentEntity expenseInstallmentEntity = new ExpenseInstallmentEntity();
        expenseInstallmentEntity.setPaymentDate(LocalDate.now());

        // Mockear las distribuciones de gasto
        ExpenseDistributionModel distributionModel1 = new ExpenseDistributionModel();
        distributionModel1.setProportion(new BigDecimal("5.00"));
        distributionModel1.setOwnerId(1);  // Asocia con el gasto

        ExpenseDistributionModel distributionModel2 = new ExpenseDistributionModel();
        distributionModel2.setProportion(new BigDecimal("5.00"));
        distributionModel2.setOwnerId(2);  // Asocia con el gasto

        List<ExpenseDistributionModel> expenseDistributions = Arrays.asList(distributionModel1, distributionModel2);

        // Mockear la persistencia de datos (para la actualización)
        when(expenseRepository.save(any())).thenReturn(validExpenseEntity);

        ExpenseCategoryModel categoryModel = new ExpenseCategoryModel();
        categoryModel.setId(1);
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(categoryModel);

        // Ejecutar el servicio
        DtoResponseExpense response = expenseService.putExpense(request, file,1);

        // Verificar el resultado
        assertNotNull(response);
        assertNotNull(response.getFileId());
        assertEquals("Test Expense", response.getDescription());

        // Verificar las interacciones
        verify(expenseRepository, times(2)).findById(eq(request.getId()));  // Verificar que se busque la entidad
        verify(expenseRepository, times(1)).save(any());  // Verificar que se guarde la entidad actualizada
        verify(fileManagerRestClient, times(1)).uploadFile(eq(file), isNull(), isNull());  // Verificar que se suba el archivo (si es necesario)
    }

    @Test
    void putExpenseValidRequestReturnsOkResponse() {
        // Crear el DTO de solicitud válido
        DtoRequestExpense request = createValidDtoRequestExpense();
        request.setId(1);  // Asignar un ID para simular que es una actualización

        // Crear y mockear el MultipartFile
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/jpeg");

        // Simular la respuesta de la carga del archivo (mock del client REST para subir archivos)
        ResponseEntity<UuidResponseDto> fileUploadResponse = mock(ResponseEntity.class);
        UuidResponseDto uuidResponse = mock(UuidResponseDto.class);
        when(uuidResponse.getUuid()).thenReturn(UUID.fromString(UUID.randomUUID().toString()));
        when(fileUploadResponse.getBody()).thenReturn(uuidResponse);

        // Mockear el método de carga de archivo en el cliente
        when(fileManagerRestClient.uploadFile(eq(file), isNull(), isNull())).thenReturn(fileUploadResponse);

        // Mockear la consulta al repositorio de gastos
        Optional<ExpenseEntity> existingExpenseOpt = Optional.of(new ExpenseEntity());
        when(expenseRepository.findById(request.getId())).thenReturn(existingExpenseOpt);

        ExpenseCategoryModel categoryModel = new ExpenseCategoryModel();
        categoryModel.setId(1);
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(categoryModel);
        // Crear y configurar una entidad de gasto válida (simulando que ya existe)
        ExpenseEntity validExpenseEntity = new ExpenseEntity();
        validExpenseEntity.setAmount(new BigDecimal("100.00"));
        validExpenseEntity.setCategory(new ExpenseCategoryEntity());
        validExpenseEntity.setDescription("Test Expense");
        validExpenseEntity.setExpenseDate(LocalDate.now());
        validExpenseEntity.setExpenseType(ExpenseType.COMUN);
        validExpenseEntity.setInvoiceNumber("909090");
        validExpenseEntity.setProviderId(1);
        validExpenseEntity.setFileId(UUID.fromString(UUID.randomUUID().toString()));  // ID de archivo preexistente
        ExpenseInstallmentEntity expenseInstallmentEntity = new ExpenseInstallmentEntity();
        expenseInstallmentEntity.setPaymentDate(LocalDate.now());

        // Mockear la persistencia de datos (para la actualización)
        when(expenseRepository.save(any())).thenReturn(validExpenseEntity);

        // Ejecutar el servicio
        DtoResponseExpense response = expenseService.putExpense(request, file,1);

        // Verificar el resultado
        assertNotNull(response);
        assertNotNull(response.getFileId());
        assertEquals("Test Expense", response.getDescription());

        // Verificar las interacciones
        verify(expenseRepository, times(2)).findById(eq(request.getId()));  // Verificar que se busque la entidad
        verify(expenseRepository, times(1)).save(any());  // Verificar que se guarde la entidad actualizada
        verify(fileManagerRestClient, times(1)).uploadFile(eq(file), isNull(), isNull());  // Verificar que se suba el archivo (si es necesario)
    }

    @Test
    void postExpenseDuplicateExpenseThrowsCustomException() {
        DtoRequestExpense request = createValidDtoRequestExpense();
        MultipartFile file = mock(MultipartFile.class);


        when(expenseRepository.findFirstByInvoiceNumberAndProviderId(anyString(), anyInt()))
                .thenReturn(Optional.of(new ExpenseEntity()));

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseService.postExpense(request, file,1);
        });

        assertEquals("The expense have already exist", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void postExpenseInvalidCategoryThrowsCustomException() {
        DtoRequestExpense request = createValidDtoRequestExpense();
        MultipartFile file = mock(MultipartFile.class);

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseService.postExpense(request, file,1);
        });

        assertEquals("The category does not exist", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void postExpenseInvalidFileTypeThrowsCustomException() {
        DtoRequestExpense request = createValidDtoRequestExpense();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("application/octet-stream");

        when(expenseRepository.findFirstByInvoiceNumberAndProviderId(anyString(), anyInt()))
                .thenReturn(Optional.empty());

        ExpenseCategoryModel categoryModel = new ExpenseCategoryModel();
        categoryModel.setId(1);
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(categoryModel);

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseService.postExpense(request, file,1);
        });

        assertEquals("the file must be an image or pdf", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void postExpenseInvalidDistributionsThrowsIllegalArgumentException() {

        DtoRequestExpense request = createValidDtoRequestExpense();
        request.setTypeExpense(ExpenseType.INDIVIDUAL.name());
        request.getDistributions().get(0).setProportion(new BigDecimal("11.00"));

        when(expenseRepository.findFirstByInvoiceNumberAndProviderId(anyString(), anyInt()))
                .thenReturn(Optional.empty());

        ExpenseCategoryModel categoryModel = new ExpenseCategoryModel();
        categoryModel.setId(1);
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(categoryModel);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            expenseService.postExpense(request, null,1);
        });

        assertEquals("the sum of distributions can't be less or more than 10.00", exception.getMessage());
    }

    private DtoRequestExpense createValidDtoRequestExpense() {
        DtoRequestExpense request = new DtoRequestExpense();
        request.setDescription("Test Expense");
        request.setProviderId(1);
        request.setExpenseDate(LocalDate.now());
        request.setInvoiceNumber("9243");
        request.setTypeExpense("COMUN");
        request.setCategoryId(1);
        request.setAmount(new BigDecimal("100.00"));
        request.setInstallments(1);

        DtoDistribution distribution = new DtoDistribution();
        distribution.setOwnerId(1);
        distribution.setProportion(new BigDecimal("10.00"));
        request.setDistributions(Collections.singletonList(distribution));

        return request;
    }

    @Test
    void deleteExpenseExpenseDoesNotExistThrowsCustomException() {
        Integer expenseId = 1;
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseService.deteleExpense(expenseId,1);
        });

        assertEquals("The expense does not exist", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void deleteExpenseExpenseHasRelatedInstallmentsThrowsCustomException() {
        Integer expenseId = 1;
        ExpenseEntity expenseEntity = new ExpenseEntity();
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expenseEntity));
        when(billExpenseInstallmentsRepository.findByExpenseId(expenseId))
                .thenReturn(Optional.of(Collections.singletonList(new BillExpenseInstallmentsEntity())));

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseService.deteleExpense(expenseId,1);
        });

        assertEquals("Expense has related bill installments", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void deleteExpenseNoRelatedInstallmentsPerformsLogicalDeletion() {
        Integer expenseId = 1;
        ExpenseEntity expenseEntity = new ExpenseEntity();
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expenseEntity));
        when(billExpenseInstallmentsRepository.findByExpenseId(expenseId)).thenReturn(Optional.empty());

        expenseService.deteleExpense(expenseId,1);

        verify(expenseRepository, times(1)).save(expenseEntity);
        assertFalse(expenseEntity.getEnabled());
    }

    @Test
    void createCreditNoteForExpenseExpenseAlreadyHasCreditNoteThrowsCustomException() {
        Integer expenseId = 1;
        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setNoteCredit(true);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expenseEntity));

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseService.createCreditNoteForExpense(expenseId,1);
        });

        assertEquals("The expense have a note of credit", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void createCreditNoteForExpenseSuccessful() {
        Integer expenseId = 1;
        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setAmount(new BigDecimal("100.00"));
        expenseEntity.setNoteCredit(false);
        expenseEntity.setDistributions(new ArrayList<>());
        expenseEntity.setInstallmentsList(new ArrayList<>());
        ExpenseCategoryEntity category = new ExpenseCategoryEntity();
        category.setId(1);
        expenseEntity.setCategory(category);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expenseEntity));
        when(expenseInstallmentRepository.save(any())).thenReturn(new ExpenseInstallmentEntity());
        BillExpenseInstallmentsEntity installmentEntity = new BillExpenseInstallmentsEntity();
        when(billExpenseInstallmentsRepository.findByExpenseId(expenseId))
                .thenReturn(Optional.of(Collections.singletonList(installmentEntity)));

        DtoResponseExpense dtoResponseExpense = expenseService.createCreditNoteForExpense(expenseId,1);
        verify(expenseRepository, times(2)).save(any(ExpenseEntity.class));
        Assertions.assertEquals(expenseEntity.getCategory().getId(), dtoResponseExpense.getDtoCategory().getId());
    }

    @Test
    void mapDtoToListExpenseDistributionModelsValidDistributionsReturnsExpenseDistModel() throws Exception {
        DtoRequestExpense request = new DtoRequestExpense();
        List<DtoDistribution> distributions = new ArrayList<>();
        DtoDistribution distribution1 = new DtoDistribution();
        distribution1.setOwnerId(1);
        distribution1.setProportion(new BigDecimal("5.00"));
        distributions.add(distribution1);

        DtoDistribution distribution2 = new DtoDistribution();
        distribution2.setOwnerId(2);
        distribution2.setProportion(new BigDecimal("5.00"));
        distributions.add(distribution2);

        request.setDistributions(distributions);

        // Use reflection to access the private method
        Method method = ExpenseService.class.getDeclaredMethod("mapDtoToListExpenseDistModel", DtoRequestExpense.class, Integer.class);
        method.setAccessible(true);
        List<ExpenseDistributionModel> result = (List<ExpenseDistributionModel>) method.invoke(expenseService, request,1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getOwnerId());
        assertEquals(new BigDecimal("5.00"), result.get(0).getProportion());
        assertEquals(2, result.get(1).getOwnerId());
        assertEquals(new BigDecimal("5.00"), result.get(1).getProportion());
    }

    @Test
    void mapDtoToListExpenseDistModelInvalidProportionThrowsIllegalArgumentException() throws Exception {
        DtoRequestExpense request = new DtoRequestExpense();
        List<DtoDistribution> distributions = new ArrayList<>();
        DtoDistribution distribution1 = new DtoDistribution();
        distribution1.setOwnerId(1);
        distribution1.setProportion(new BigDecimal("6.00"));
        distributions.add(distribution1);

        DtoDistribution distribution2 = new DtoDistribution();
        distribution2.setOwnerId(2);
        distribution2.setProportion(new BigDecimal("5.00"));
        distributions.add(distribution2);

        request.setDistributions(distributions);

        Method method = ExpenseService.class.getDeclaredMethod("mapDtoToListExpenseDistModel", DtoRequestExpense.class, Integer.class);
        method.setAccessible(true);
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            method.invoke(expenseService, request, 1);
        });

        assertTrue(exception.getCause() instanceof IllegalArgumentException);

        // Assert the expected message of the IllegalArgumentException
        IllegalArgumentException cause = (IllegalArgumentException) exception.getCause();
        assertEquals("the sum of distributions can't be less or more than 10.00", cause.getMessage());
    }


    /////////////////////EXPENSES BY ID///////////////////////////////
    @Test
    void getExpenseByIdValidIdReturnsDtoExpenseQuery() {
        Integer expenseId = 1;
        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setEnabled(true);
        expenseEntity.setId(expenseId);
        expenseEntity.setAmount(new BigDecimal("100.00"));
        expenseEntity.setCategory(new ExpenseCategoryEntity());
        expenseEntity.setExpenseDate(LocalDate.now());
        expenseEntity.setExpenseType(ExpenseType.COMUN);
        expenseEntity.setProviderId(1);
        expenseEntity.setDistributions(new ArrayList<>());
        expenseEntity.setInstallmentsList(new ArrayList<>());

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expenseEntity));
        when(modelMapper.map(expenseEntity, DtoExpenseQuery.class)).thenReturn(new DtoExpenseQuery());

        DtoExpenseQuery result = expenseService.getExpenseById(expenseId);

        assertNotNull(result);
        verify(expenseRepository, times(1)).findById(expenseId);
    }

    @Test
    void getExpenseByIdExpenseNotEnabledThrowsCustomException() {
        Integer expenseId = 1;
        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setEnabled(false);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expenseEntity));

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseService.getExpenseById(expenseId);
        });

        assertEquals("The expense does not exist", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getExpenseByIdExpenseDoesNotExistThrowsCustomException() {
        Integer expenseId = 1;

//        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseService.getExpenseById(expenseId);
        });

        assertEquals("The expense does not exist", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }


    ////////////////////EXPENSES BY DATE///////////////////////////////
    @Test
    void getExpensesValidDateRangeReturnsDtoExpenseQueryList() {
        String dateFrom = "2023-01-01";
        String dateTo = "2023-12-31";
        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setId(1);
        expenseEntity.setEnabled(true);
        expenseEntity.setAmount(BigDecimal.TEN);
        expenseEntity.setExpenseType(ExpenseType.COMUN);
        expenseEntity.setCategory(new ExpenseCategoryEntity());
        expenseEntity.setDistributions(new ArrayList<>());
        expenseEntity.setInstallmentsList(new ArrayList<>());
        expenseEntity.setExpenseDate(LocalDate.parse("2023-06-01"));
        List<ExpenseEntity> expenseEntityList = Collections.singletonList(expenseEntity);
        when(expenseRepository.findAllByDate(any(LocalDate.class), any(LocalDate.class))).thenReturn(expenseEntityList);
//        when(modelMapper.map(expenseEntity, DtoExpenseQuery.class)).thenReturn(dtoExpenseQuery);

        List<DtoExpenseQuery> result = expenseService.getExpenses( dateFrom, dateTo);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(expenseRepository, times(1)).findAllByDate(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getExpensesInvalidDateRangeThrowsCustomException() {
        String dateFrom = "2023-12-31";
        String dateTo = "2023-01-01";

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseService.getExpenses( dateFrom, dateTo);
        });

        assertEquals("The date range is not correct", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getExpensesNullDateRangeThrowsCustomException() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseService.getExpenses(null,null);
        });

        assertEquals("The date range is required", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getExpensesExpenseNotEnabledIsNotIncludedInResult() {
        // Arrange
        String dateFrom = "2023-01-01";
        String dateTo = "2023-12-31";

        // Crear una entidad de gasto no habilitada
        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setEnabled(false); // Asegúrate de que esté deshabilitada

        // Lista que contiene la entidad de gasto no habilitada
        List<ExpenseEntity> expenseEntityList = Collections.singletonList(expenseEntity);

        // Mock para la respuesta de los propietarios
        OwnerDto[] ownerDtoArray = new OwnerDto[0]; // o crea un array de OwnerDto si lo necesitas
        ResponseEntity<OwnerDto[]> responseEntity = new ResponseEntity<>(ownerDtoArray, HttpStatus.OK);

        // Crear las listas vacías para proveedores y propietarios
        List<ProviderDTO> providerDTOS = new ArrayList<>();
        List<OwnerDto> ownerDtos = new ArrayList<>();

        // Simulamos las respuestas de los mocks
        when(expenseRepository.findAllByDate(any(LocalDate.class), any(LocalDate.class))).thenReturn(expenseEntityList);
//        when(ownerRestClient.getOwnerPlot()).thenReturn(responseEntity);
        when(providerService.getProviders()).thenReturn(providerDTOS);

        // Act
        List<DtoExpenseQuery> result = expenseService.getExpenses( dateFrom, dateTo);

        // Assert
        assertNotNull(result);  // Verifica que el resultado no sea nulo
        assertTrue(result.isEmpty());  // Asegura que la lista esté vacía, ya que el gasto está deshabilitado

        // Verifica que el método del repositorio fue llamado una vez
        verify(expenseRepository, times(1)).findAllByDate(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getExpensesInvalidDateFormatThrowsCustomException() {
        String dateFrom = "invalid-date";
        String dateTo = "2023-12-31";

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseService.getExpenses( dateFrom, dateTo);
        });

        assertEquals("The date format is not correct", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    //////////////////// MAP ENTITY TO DTO EXPENSE /////////////////////

    @Test
    void mapModelToDtoExpense_ExpenseModelWithDistributionsAndInstallments_ReturnsDtoExpenseQuery() {
        // Crear ExpenseModel y sus dependencias
        ExpenseModel expenseModel = new ExpenseModel();
        expenseModel.setId(1);
        expenseModel.setAmount(new BigDecimal("100.00"));
        expenseModel.setExpenseType(ExpenseType.COMUN);
        expenseModel.setExpenseDate(LocalDate.now());
        expenseModel.setFileId(UUID.fromString("00000000-0000-0000-0000-000000000123"));

        ExpenseCategoryModel category = new ExpenseCategoryModel();
        category.setDescription("Category Description");
        category.setId(1);
        expenseModel.setCategory(category);

        // Crear ExpenseDistributionModel
        ExpenseDistributionModel distributionModel = new ExpenseDistributionModel();
        distributionModel.setOwnerId(1);
        distributionModel.setProportion(new BigDecimal("0.5"));
        expenseModel.setDistributions(List.of(distributionModel));

        // Crear ExpenseInstallmentModel
        ExpenseInstallmentModel installmentModel = new ExpenseInstallmentModel();
        installmentModel.setInstallmentNumber(1);
        installmentModel.setPaymentDate(LocalDate.now());
        expenseModel.setInstallmentsList(List.of(installmentModel));

        // Crear OwnerDto
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setId(1);
        ownerDto.setName("Juan");
        ownerDto.setLastName("Perez");
        List<OwnerDto> ownerDtos = List.of(ownerDto);

        // Configurar mocks
        Map<Integer, String> providerMap = new HashMap<>();
        providerMap.put(1, "Sin proveedor");

        DtoExpenseQuery expectedDtoExpenseQuery = new DtoExpenseQuery();

        // Llamar al método público directamente
        DtoExpenseQuery result = expenseService.mapModelToDtoExpense(expenseModel, ownerDtos, providerMap);

        // Verificar el resultado
        assertNotNull(result);
        assertEquals(expenseModel.getId(), result.getId());
        assertEquals(expenseModel.getAmount().setScale(2, RoundingMode.HALF_UP), result.getAmount());
        assertEquals(expenseModel.getExpenseType().name(), result.getExpenseType());
        assertEquals(expenseModel.getExpenseDate(), result.getExpenseDate());
        assertEquals("00000000-0000-0000-0000-000000000123", result.getFileId());
        assertEquals("Category Description", result.getCategory());

        // Verificar proveedor
        assertEquals("Sin proveedor", result.getProvider());
        assertEquals(expenseModel.getProviderId(), result.getProviderId());

        // Verificar distribuciones
        assertEquals(1, result.getDistributionList().size());
        assertEquals("Perez Juan", result.getDistributionList().get(0).getOwnerFullName());
        assertEquals(expenseModel.getAmount().multiply(distributionModel.getProportion()).setScale(2, RoundingMode.HALF_UP),
                result.getDistributionList().get(0).getAmount());

        // Verificar cuotas
        assertEquals(1, result.getInstallmentList().size());
        assertEquals(1, result.getInstallmentList().get(0).getInstallmentNumber());
        assertEquals(expenseModel.getInstallmentsList().get(0).getPaymentDate(), result.getInstallmentList().get(0).getPaymentDate());
    }

    @Test
    void mapEntityToDtoExpense_ExpenseEntityWithDistributionsAndInstallments_ReturnsDtoExpenseQuery() {
        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setProviderId(1);
        expenseEntity.setExpenseDate(LocalDate.now());
        expenseEntity.setFileId(UUID.fromString("00000000-0000-0000-0000-000000000123"));
        expenseEntity.setAmount(new BigDecimal("100.00"));

        ExpenseCategoryEntity category = new ExpenseCategoryEntity();
        category.setDescription("Category Description");
        expenseEntity.setCategory(category);

        // Crear ExpenseDistributionEntity
        ExpenseDistributionEntity distributionEntity = new ExpenseDistributionEntity();
        distributionEntity.setOwnerId(1);
        distributionEntity.setProportion(new BigDecimal("0.5"));
        expenseEntity.setDistributions(List.of(distributionEntity));

        // Crear ExpenseInstallmentEntity y asegurarse de que la lista no sea nula
        ExpenseInstallmentEntity installmentEntity = new ExpenseInstallmentEntity();
        installmentEntity.setInstallmentNumber(1);
        installmentEntity.setPaymentDate(LocalDate.now());
        installmentEntity.setEnabled(Boolean.TRUE);
        expenseEntity.setInstallmentsList(new ArrayList<>()); // Inicializar como lista vacía
        expenseEntity.getInstallmentsList().add(installmentEntity); // Agregar el installment

        // Configurar mocks
        List<ProviderDTO> providerDTOS = new ArrayList<>();
        DtoExpenseQuery dtoExpenseQuery = new DtoExpenseQuery();
        when(modelMapper.map(expenseEntity, DtoExpenseQuery.class)).thenReturn(dtoExpenseQuery);
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setId(1);
        ownerDto.setName("Juan");
        ownerDto.setLastName("Perez");
        List<OwnerDto> ownerDtos = List.of(ownerDto);
        // Llamar al método público directamente
        Map<Integer, String> providerMap = new HashMap<>();
        providerMap.put(1, "Provider Name");  // Añadir la información del proveedor
        DtoExpenseQuery result = expenseService.mapEntityToDtoExpense(expenseEntity, providerMap, ownerDtos);

        // Verificar el resultado
        assertNotNull(result);
        assertEquals("Provider Name", result.getProvider());
        assertEquals(expenseEntity.getExpenseDate(), result.getExpenseDate());
        assertEquals("00000000-0000-0000-0000-000000000123", result.getFileId());
        assertEquals("Category Description", result.getCategory());

        // Verificar distribuciones
        assertEquals(1, result.getDistributionList().size());
        assertEquals("Juan Perez", result.getDistributionList().get(0).getOwnerFullName());
        assertEquals(new BigDecimal("0.5").multiply(expenseEntity.getAmount()), result.getDistributionList().get(0).getAmount());

        // Verificar cuotas
        assertEquals(1, result.getInstallmentList().size());
        assertEquals(1, result.getInstallmentList().get(0).getInstallmentNumber());
        assertEquals(expenseEntity.getInstallmentsList().get(0).getPaymentDate(), result.getInstallmentList().get(0).getPaymentDate());
    }

    @Test
    void fetchValidExpenseModel_ShouldThrowException_WhenProviderIdIsNull() {
        requestExpense.setProviderId(null);
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(new ExpenseCategoryModel());
        CustomException exception = assertThrows(CustomException.class,
                () -> expenseService.postExpense(requestExpense, null, 1));
        assertEquals("Provider ID cannot be null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void fetchValidExpenseModel_ShouldThrowException_WhenExpenseDateIsNull() {
        requestExpense.setExpenseDate(null);
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(new ExpenseCategoryModel());
        CustomException exception = assertThrows(CustomException.class,
                () -> expenseService.postExpense(requestExpense, null, 1));
        assertEquals("Expense date cannot be null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void fetchValidExpenseModel_ShouldThrowException_WhenInvoiceNumberIsNull() {
        requestExpense.setInvoiceNumber(null);
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(new ExpenseCategoryModel());
        CustomException exception = assertThrows(CustomException.class,
                () -> expenseService.postExpense(requestExpense, null, 1));
        assertEquals("Invoice number cannot be null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void fetchValidExpenseModel_ShouldThrowException_WhenTypeExpenseIsNullOrEmpty() {
        requestExpense.setTypeExpense(null);
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(new ExpenseCategoryModel());
        CustomException exception = assertThrows(CustomException.class,
                () -> expenseService.postExpense(requestExpense, null, 1));
        assertEquals("Type of expense cannot be empty", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void fetchValidExpenseModel_ShouldThrowException_WhenTypeExpenseIsInvalid() {
        requestExpense.setTypeExpense("INVALID");
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(new ExpenseCategoryModel());
        CustomException exception = assertThrows(CustomException.class,
                () -> expenseService.postExpense(requestExpense, null, 1));
        assertEquals("Type of expense must be one of: COMUN, EXTRAORDINARIO, INDIVIDUAL", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void fetchValidExpenseModel_ShouldThrowException_WhenAmountIsNullOrZero() {
        requestExpense.setAmount(null);
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(new ExpenseCategoryModel());
        CustomException exception = assertThrows(CustomException.class,
                () -> expenseService.postExpense(requestExpense, null, 1));
        assertEquals("Amount must be greater than zero", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void fetchValidExpenseModel_ShouldThrowException_WhenAmountExceedsMax() {
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(new ExpenseCategoryModel());
        requestExpense.setAmount(BigDecimal.valueOf(1_000_000_000)); 
        CustomException exception = assertThrows(CustomException.class,
                () -> expenseService.postExpense(requestExpense, null, 1));
        assertEquals("Amount can't be greater than max amount", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void fetchValidExpenseModel_ShouldThrowException_WhenInstallmentsAreZero() {
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(new ExpenseCategoryModel());
        requestExpense.setInstallments(0);
        CustomException exception = assertThrows(CustomException.class,
                () -> expenseService.postExpense(requestExpense, null, 1));
        assertEquals("Installments must be greater than zero", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void fetchValidExpenseModel_ShouldThrowException_WhenDistributionsAreEmptyForIndividualExpense() {
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(new ExpenseCategoryModel());
        requestExpense.setTypeExpense(ExpenseType.INDIVIDUAL.toString());
        requestExpense.setDistributions(Collections.emptyList());
        CustomException exception = assertThrows(CustomException.class,
                () -> expenseService.postExpense(requestExpense, null, 1));
        assertEquals("Distributions cannot be empty", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void fetchValidExpenseModel_ShouldThrowException_WhenFileTypeIsInvalid() {
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(new ExpenseCategoryModel());
        MockMultipartFile invalidFile = new MockMultipartFile("file", "test.txt", "text/plain", new byte[0]);
        CustomException exception = assertThrows(CustomException.class,
                () -> expenseService.postExpense(requestExpense, invalidFile, 1));
        assertEquals("the file must be an image or pdf", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
    @Test
    void getExpenseByPaymentDateRange_WithValidDates_ReturnsExpenseList() {
        // Arrange
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);
        ExpenseEntity expenseEntity1 = new ExpenseEntity();
        expenseEntity1.setId(1);
        expenseEntity1.setDescription("Expense 1");
        expenseEntity1.setAmount(BigDecimal.valueOf(100));
        expenseEntity1.setExpenseDate(LocalDate.now());
        expenseEntity1.setExpenseType(ExpenseType.COMUN);
        ExpenseEntity expenseEntity2 = new ExpenseEntity();
        expenseEntity2.setId(2);
        expenseEntity2.setDescription("Expense 2");
        expenseEntity2.setAmount(BigDecimal.valueOf(100));
        expenseEntity2.setExpenseDate(LocalDate.now());
        expenseEntity2.setExpenseType(ExpenseType.COMUN);

        List<ExpenseEntity> entities = Arrays.asList(expenseEntity1, expenseEntity2);

        when(expenseRepository.findAllByPaymentDate(from, to)).thenReturn(entities);

        // Act
        List<ExpenseModel> result = expenseService.getExpenseByPaymentDateRange(from, to);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Expense 1", result.get(0).getDescription());
        assertEquals("Expense 2", result.get(1).getDescription());
        verify(expenseRepository).findAllByPaymentDate(from, to);
    }

    @Test
    void getExpenseByPaymentDateRange_WithEmptyResult_ReturnsEmptyList() {
        // Arrange
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        when(expenseRepository.findAllByPaymentDate(from, to)).thenReturn(Collections.emptyList());

        // Act
        List<ExpenseModel> result = expenseService.getExpenseByPaymentDateRange(from, to);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(expenseRepository).findAllByPaymentDate(from, to);
    }
    @Test
    void updateExpenseEntity_ReduceInstallments_DisablesExtraInstallments() {
        // Arrange
        ExpenseEntity existingExpense = new ExpenseEntity();
        ExpenseCategoryEntity category = new ExpenseCategoryEntity();
        category.setId(1);

        ExpenseModel expenseModel = new ExpenseModel();
        expenseModel.setDescription("Test Expense");
        expenseModel.setExpenseDate(LocalDate.now());
        expenseModel.setAmount(BigDecimal.TEN);
        expenseModel.setExpenseType(ExpenseType.COMUN);
        expenseModel.setCategory(modelMapper.map(category, ExpenseCategoryModel.class));
        expenseModel.setInstallments(2);
        expenseModel.setLastUpdatedUser(1);

        // Inicializamos la lista de cuotas en el modelo
        ExpenseInstallmentModel installment1Model = new ExpenseInstallmentModel();
        installment1Model.setInstallmentNumber(1);
        installment1Model.setPaymentDate(LocalDate.of(2023, 1, 15));

        ExpenseInstallmentModel installment2Model = new ExpenseInstallmentModel();
        installment2Model.setInstallmentNumber(2);
        installment2Model.setPaymentDate(LocalDate.of(2023, 2, 15));

        expenseModel.setInstallmentsList(new ArrayList<>(Arrays.asList(installment1Model, installment2Model)));

        ExpenseInstallmentEntity installment1 = new ExpenseInstallmentEntity();
        installment1.setPaymentDate(LocalDate.of(2023, 1, 15));
        installment1.setInstallmentNumber(1);
        installment1.setEnabled(true);

        ExpenseInstallmentEntity installment2 = new ExpenseInstallmentEntity();
        installment2.setPaymentDate(LocalDate.of(2023, 2, 15));
        installment2.setInstallmentNumber(2);
        installment2.setEnabled(true);

        ExpenseInstallmentEntity installment3 = new ExpenseInstallmentEntity();
        installment3.setPaymentDate(LocalDate.of(2023, 3, 15));
        installment3.setInstallmentNumber(3);
        installment3.setEnabled(true);

        existingExpense.setInstallmentsList(new ArrayList<>(Arrays.asList(
                installment1, installment2, installment3
        )));
        existingExpense.setCategory(category);


        expenseService.updateExpenseEntity(existingExpense, expenseModel, new ArrayList<>(), 1);


        assertEquals(3, existingExpense.getInstallmentsList().size());
        assertTrue(existingExpense.getInstallmentsList().get(0).getEnabled());
        assertTrue(existingExpense.getInstallmentsList().get(1).getEnabled());
        assertFalse(existingExpense.getInstallmentsList().get(2).getEnabled());
    }


}
