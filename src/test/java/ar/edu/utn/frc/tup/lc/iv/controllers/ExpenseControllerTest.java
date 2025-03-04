package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoCategory;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseQuery;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoRequestExpense;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoResponseDeleteExpense;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoResponseExpense;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseDistributionQuery;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoDistribution;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoInstallment;

import ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType.EXTRAORDINARIO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.ArgumentMatchers.anyInt;

/**
 * Test class for the ExpenseController.
 */
class ExpenseControllerTest {
   /**
 * MockMvc instance for performing HTTP requests in tests.
 */
private MockMvc mockMvc;

/**
 * ObjectMapper instance for JSON serialization and deserialization.
 */
private ObjectMapper objectMapper;

/**
 * Mocked service for handling expense-related operations.
 */
@Mock
private IExpenseService expenseService;

/**
 * Controller under test, with injected mocks.
 */
@InjectMocks
private ExpenseController expenseController;

/**
 * Initializes mocks and sets up MockMvc before each test.
 */
@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(expenseController).build();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
}
   /**
 * Test for posting an expense.
 * Ensures that the response contains the expected values.
 *
 * @throws Exception if an error occurs during the test
 */
@Test
void testPostExpense() throws Exception {
    DtoRequestExpense requestExpense = new DtoRequestExpense();

    MockMultipartFile file = new MockMultipartFile("file", "test.txt",
            MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());

    DtoResponseExpense responseExpense = new DtoResponseExpense();
    responseExpense.setDescription("Test Expense");
    responseExpense.setProviderId(1);
    responseExpense.setExpenseDate(LocalDate.of(2024, 10, 9));
    responseExpense.setFileId(UUID.randomUUID());
    responseExpense.setInvoiceNumber("12345");
    responseExpense.setExpenseType(ExpenseType.COMUN);
    responseExpense.setDtoCategory(new DtoCategory());
    responseExpense.setDtoDistributionList(Arrays.asList(new DtoDistribution()));
    responseExpense.setDtoInstallmentList(Arrays.asList(new DtoInstallment()));

    when(expenseService.postExpense(any(DtoRequestExpense.class), any(MultipartFile.class), anyInt()))
            .thenReturn(ResponseEntity.ok(responseExpense));

    mockMvc.perform(multipart("/expenses")
                    .file(file)
                    .file(new MockMultipartFile("expense", "",
                            MediaType.APPLICATION_JSON_VALUE,
                            objectMapper.writeValueAsString(requestExpense).getBytes()))
                    .file(new MockMultipartFile("userId", "",
                            MediaType.APPLICATION_JSON_VALUE, "1".getBytes()))
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description").value("Test Expense"))
            .andExpect(jsonPath("$.providerId").value(1))
            .andExpect(jsonPath("$.expenseDate").value("2024-10-09"))
            .andExpect(jsonPath("$.fileId").isNotEmpty())
            .andExpect(jsonPath("$.invoiceNumber").value(12345))
            .andExpect(jsonPath("$.expenseType").value("COMUN"))
            .andExpect(jsonPath("$.dtoCategory").isNotEmpty())
            .andExpect(jsonPath("$.dtoDistributionList").isArray())
            .andExpect(jsonPath("$.dtoInstallmentList").isArray());

    verify(expenseService, times(1)).postExpense(any(DtoRequestExpense.class), any(MultipartFile.class),anyInt());
}
/**
 * Test for updating an expense.
 * Ensures that the response contains the expected values.
 *
 * @throws Exception if an error occurs during the test
 */
@Test
void testPutExpense() throws Exception {
    DtoRequestExpense requestExpense = new DtoRequestExpense();

    // Archivo simulado
    MockMultipartFile file = new MockMultipartFile("file", "test.txt",
            MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());

    // Respuesta simulada del servicio
    DtoResponseExpense responseExpense = new DtoResponseExpense();
    responseExpense.setDescription("Test Expense");
    responseExpense.setProviderId(1);
    responseExpense.setExpenseDate(LocalDate.of(2024, 10, 9));
    responseExpense.setFileId(UUID.randomUUID());
    responseExpense.setInvoiceNumber("12345");
    responseExpense.setExpenseType(ExpenseType.COMUN);
    responseExpense.setDtoCategory(new DtoCategory());
    responseExpense.setDtoDistributionList(Arrays.asList(new DtoDistribution()));
    responseExpense.setDtoInstallmentList(Arrays.asList(new DtoInstallment()));

    // Simulación del servicio
    when(expenseService.putExpense(any(DtoRequestExpense.class), any(MultipartFile.class),anyInt()))
            .thenReturn(responseExpense);

    // Act & Assert
    mockMvc.perform(multipart("/expenses")
                    .file(file)
                    .file(new MockMultipartFile("expense", "",
                            MediaType.APPLICATION_JSON_VALUE,
                            objectMapper.writeValueAsString(requestExpense).getBytes()))
                    .file(new MockMultipartFile("userId", "",
                            MediaType.APPLICATION_JSON_VALUE, "1".getBytes()))
                    .with(request -> {
                        request.setMethod("PUT"); // Cambiar el método a PUT
                        return request;
                    })
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description").value("Test Expense"))
            .andExpect(jsonPath("$.providerId").value(1))
            .andExpect(jsonPath("$.expenseDate").value("2024-10-09"))
            .andExpect(jsonPath("$.fileId").isNotEmpty())
            .andExpect(jsonPath("$.invoiceNumber").value("12345"))
            .andExpect(jsonPath("$.expenseType").value("COMUN"))
            .andExpect(jsonPath("$.dtoCategory").isNotEmpty())
            .andExpect(jsonPath("$.dtoDistributionList").isArray())
            .andExpect(jsonPath("$.dtoInstallmentList").isArray());

    verify(expenseService, times(1)).putExpense(any(DtoRequestExpense.class), any(MultipartFile.class),anyInt());
}
    /**
 * Test for posting an expense without a file.
 * Ensures that the response contains the expected values.
 *
 * @throws Exception if an error occurs during the test
 */
@Test
void testPostExpenseWithoutFile() throws Exception {
    DtoRequestExpense requestExpense = new DtoRequestExpense();

    DtoResponseExpense responseExpense = getDtoResponseExpense();

    when(expenseService.postExpense(any(DtoRequestExpense.class), isNull(), anyInt()))
            .thenReturn(ResponseEntity.ok(responseExpense));

    mockMvc.perform(multipart("/expenses")
                    .file(new MockMultipartFile("expense", "",
                            MediaType.APPLICATION_JSON_VALUE,
                            objectMapper.writeValueAsString(requestExpense).getBytes()))
                    .file(new MockMultipartFile("userId", "",
                            MediaType.APPLICATION_JSON_VALUE, "1".getBytes()))
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description").value("Test Expense Without File"))
            .andExpect(jsonPath("$.providerId").value(2))
            .andExpect(jsonPath("$.expenseDate").value("2024-10-10"))
            .andExpect(jsonPath("$.fileId").doesNotExist())
            .andExpect(jsonPath("$.invoiceNumber").value(54321))
            .andExpect(jsonPath("$.expenseType").value("EXTRAORDINARIO"))
            .andExpect(jsonPath("$.dtoCategory").isNotEmpty())
            .andExpect(jsonPath("$.dtoDistributionList").isArray())
            .andExpect(jsonPath("$.dtoInstallmentList").isArray());

    verify(expenseService, times(1)).postExpense(any(DtoRequestExpense.class), isNull(),eq(1));
}
    /**
 * Creates a sample DtoResponseExpense object for testing purposes.
 *
 * @return a DtoResponseExpense object with pre-filled test data
 */
private static DtoResponseExpense getDtoResponseExpense() {
    DtoResponseExpense responseExpense = new DtoResponseExpense();
    responseExpense.setDescription("Test Expense Without File");
    responseExpense.setProviderId(2);
    responseExpense.setExpenseDate(LocalDate.of(2024, 10, 10));
    responseExpense.setInvoiceNumber("54321");
    responseExpense.setExpenseType(EXTRAORDINARIO);
    responseExpense.setDtoCategory(new DtoCategory());
    responseExpense.setDtoDistributionList(Arrays.asList(new DtoDistribution()));
    responseExpense.setDtoInstallmentList(Arrays.asList(new DtoInstallment()));
    return responseExpense;
}
   /**
 * Test for getting an expense by its ID.
 * Ensures that the response contains the expected values.
 *
 * @throws Exception if an error occurs during the test
 */
@Test
void testGetExpenseById() throws Exception {
    int expenseId = 1;
    DtoExpenseQuery expenseQuery = new DtoExpenseQuery();

    List<DtoExpenseDistributionQuery> expenseDistribution = new ArrayList<>();
    DtoExpenseDistributionQuery distribution = new DtoExpenseDistributionQuery();
    distribution.setAmount(new BigDecimal("100.00"));
    distribution.setOwnerId(1);
    distribution.setOwnerFullName("Owner");
    expenseDistribution.add(distribution);

    expenseQuery.setCategory("Category");
    expenseQuery.setId(1);
    expenseQuery.setAmount(new BigDecimal("100.00"));
    expenseQuery.setExpenseType(ExpenseType.COMUN.toString());
    expenseQuery.setProvider("Provider");
    expenseQuery.setExpenseDate(LocalDate.parse("2024-01-01"));
    expenseQuery.setDistributionList(expenseDistribution);

    // Ensure the mock is configured correctly
    when(expenseService.getExpenseById(anyInt())).thenReturn(expenseQuery);

    mockMvc.perform(get("/expenses/getById")
                    .param("expenseId", String.valueOf(expenseId))
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.category").value("Category"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.amount").value("100.00"))
            .andExpect(jsonPath("$.expenseType").value("COMUN"))
            .andExpect(jsonPath("$.provider").value("Provider"))
            .andExpect(jsonPath("$.expenseDate").value("2024-01-01"))
            .andExpect(jsonPath("$.distributionList[0].amount").value("100.0"))
            .andExpect(jsonPath("$.distributionList[0].ownerId").value(1))
            .andExpect(jsonPath("$.distributionList[0].ownerFullName").value("Owner"));

    verify(expenseService, times(1)).getExpenseById(expenseId);
}
   /**
 * Test for getting expenses by filters.
 * Ensures that the response contains the expected values.
 *
 * @throws Exception if an error occurs during the test
 */
@Test
void testGetExpenses() throws Exception {
    String expenseType = "COMUN";
    String category = "Category";
    String provider = "Provider";
    String dateFrom = "2024-01-01";
    String dateTo = "2024-12-31";

    List<DtoExpenseQuery> expenseQueryList = new ArrayList<>();
    DtoExpenseQuery expenseQuery = new DtoExpenseQuery();

    List<DtoExpenseDistributionQuery> expenseDistribution = new ArrayList<>();
    DtoExpenseDistributionQuery distribution = new DtoExpenseDistributionQuery();
    distribution.setAmount(new BigDecimal("100.00"));
    distribution.setOwnerId(1);
    distribution.setOwnerFullName("Owner");
    expenseDistribution.add(distribution);

    expenseQuery.setCategory("Category");
    expenseQuery.setId(1);
    expenseQuery.setAmount(new BigDecimal("100.00"));
    expenseQuery.setExpenseType(ExpenseType.COMUN.toString());
    expenseQuery.setProvider("Provider");
    expenseQuery.setExpenseDate(LocalDate.parse("2024-01-01"));
    expenseQuery.setDistributionList(expenseDistribution);

    expenseQueryList.add(expenseQuery);

    // Ensure the mock is configured correctly
    when(expenseService.getExpenses(dateFrom, dateTo))
            .thenReturn(expenseQueryList);

    mockMvc.perform(get("/expenses/getByFilters")
                    .param("expenseType", expenseType)
                    .param("category", category)
                    .param("provider", provider)
                    .param("dateFrom", dateFrom)
                    .param("dateTo", dateTo)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].category").value("Category"))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].amount").value("100.00"))
            .andExpect(jsonPath("$[0].expenseType").value("COMUN"))
            .andExpect(jsonPath("$[0].provider").value("Provider"))
            .andExpect(jsonPath("$[0].expenseDate").value("2024-01-01"))
            .andExpect(jsonPath("$[0].distributionList[0].amount").value("100.0"))
            .andExpect(jsonPath("$[0].distributionList[0].ownerId").value(1))
            .andExpect(jsonPath("$[0].distributionList[0].ownerFullName").value("Owner"));

    verify(expenseService, times(1)).getExpenses(dateFrom, dateTo);
}
    /**
 * Test for getting expenses by filters.
 * Ensures that the response contains the expected values.
 *
 * @throws Exception if an error occurs during the test
 */
@Test
void testGetExpensesByFilters() throws Exception {
    // Arrange
    String expenseType = "type";
    String category = "category";
    String provider = "provider";
    String dateFrom = "2023-01-01";
    String dateTo = "2023-12-31";
    List<DtoExpenseQuery> expectedResponse = List.of(new DtoExpenseQuery());

    when(expenseService.getExpenses(dateFrom, dateTo)).thenReturn(expectedResponse);

    MockMvc mockMvcTest = MockMvcBuilders.standaloneSetup(expenseController).build();

    // Act & Assert
    mockMvcTest.perform(get("/expenses/getByFilters")
                    .param("expenseType", expenseType)
                    .param("category", category)
                    .param("provider", provider)
                    .param("dateFrom", dateFrom)
                    .param("dateTo", dateTo))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").exists()); // Ajuste para verificar un campo específico en la respuesta

    verify(expenseService, times(1)).getExpenses(dateFrom, dateTo);
}
   /**
 * Test for updating an expense without a file.
 * Ensures that the response contains the expected values.
 *
 * @throws Exception if an error occurs during the test
 */
@Test
void testPutExpenseWithoutFile() throws Exception {
    // Arrange
    DtoRequestExpense requestExpense = new DtoRequestExpense();
    requestExpense.setDescription("Test Expense Without File");
    requestExpense.setProviderId(2);
    requestExpense.setExpenseDate(LocalDate.from(LocalDateTime.now()));
    requestExpense.setInvoiceNumber("54321");
    requestExpense.setTypeExpense(String.valueOf(EXTRAORDINARIO));

    DtoResponseExpense responseExpense = getDtoResponseExpense();

    when(expenseService.putExpense(any(DtoRequestExpense.class), isNull(), anyInt()))
            .thenReturn(responseExpense);

    MockMvc mockMvcTest = MockMvcBuilders.standaloneSetup(expenseController).build();

    // Act & Assert
    mockMvcTest.perform(multipart("/expenses")
                    .file(new MockMultipartFile("expense", "",
                            MediaType.APPLICATION_JSON_VALUE,
                            objectMapper.writeValueAsString(requestExpense).getBytes()))
                    .file(new MockMultipartFile("userId", "",
                            MediaType.APPLICATION_JSON_VALUE, "1".getBytes()))
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isOk());

}
    @Test
    void testDeleteExpenseByIdLogicOrThrowException() throws Exception {
        // Arrange
        int expenseId = 1;
        int userId=1;
        DtoResponseDeleteExpense expectedResponse = new DtoResponseDeleteExpense();

        doReturn(expectedResponse).when(expenseService).deteleExpense(anyInt(),anyInt());

        MockMvc mockMvcTest = MockMvcBuilders.standaloneSetup(expenseController).build();

        // Act & Assert
        mockMvcTest.perform(delete("/expenses")
                        .param("id", String.valueOf(expenseId))
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedResponse));

        verify(expenseService, times(1)).deteleExpense(eq(expenseId), eq(userId));
    }

   /**
 * Test for creating a note of credit for an expense.
 * Ensures that the response contains the expected values.
 *
 * @throws Exception if an error occurs during the test
 */
@Test
void testCreateNoteOfCredit() throws Exception {
    // Arrange
    int expenseId = 1;
    int userId = 1;
    DtoResponseExpense expectedResponse = new DtoResponseExpense();

    when(expenseService.createCreditNoteForExpense(expenseId, 1)).thenReturn(expectedResponse);

    MockMvc mockMvcTest = MockMvcBuilders.standaloneSetup(expenseController).build();

    // Act & Assert
    mockMvcTest.perform(delete("/expenses/note_credit")
                    .param("id", String.valueOf(expenseId))
                    .param("userId",String.valueOf(userId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(expectedResponse));

    verify(expenseService, times(1)).createCreditNoteForExpense(expenseId, userId);
}
}
