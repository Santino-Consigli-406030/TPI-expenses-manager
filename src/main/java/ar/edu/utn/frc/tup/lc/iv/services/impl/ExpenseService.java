package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.client.FileManagerRestClient;
import ar.edu.utn.frc.tup.lc.iv.client.OwnerRestClient;
import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoCategory;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoDistribution;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseDistributionQuery;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseInstallment;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseQuery;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoInstallment;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoRequestExpense;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoResponseDeleteExpense;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoResponseExpense;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.ProviderDTO;
import ar.edu.utn.frc.tup.lc.iv.dtos.fileManager.UuidResponseDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.owner.OwnerDto;
import ar.edu.utn.frc.tup.lc.iv.entities.BillExpenseInstallmentsEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseCategoryEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseDistributionEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseInstallmentEntity;
import ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseCategoryModel;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseDistributionModel;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseInstallmentModel;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseModel;
import ar.edu.utn.frc.tup.lc.iv.repositories.BillExpenseInstallmentsRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.ExpenseDistributionRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.ExpenseInstallmentRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.ExpenseRepository;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IExpenseService;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IProviderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing expenses.
 */
@Service
@RequiredArgsConstructor
public class ExpenseService implements IExpenseService {

    /**
     * Repository for managing expense entities.
     */

    private final ExpenseRepository expenseRepository;
    /**
     * Repository for managing expense distribution entities.
     */

    private final ExpenseDistributionRepository expenseDistributionRepository;
    /**
     * Repository for managing expense installment entities.
     */

    private final ExpenseInstallmentRepository expenseInstallmentRepository;
    /**
     * Model mapper for converting between entities and DTOs.
     */

    private final ModelMapper modelMapper;
    /**
     * Service for managing expense categories.
     */
    private final ExpenseCategoryService expenseCategoryService;
    /**
     * REST client for managing file operations.
     */
    private final FileManagerRestClient fileManagerRestClient;
    /**
     * Repository for managing bill expense installments entities.
     */
    private final BillExpenseInstallmentsRepository billExpenseInstallmentsRepository;
    /**
     * REST client for managing owner operations.
     */
    private final OwnerRestClient ownerRestClient;
    /**
     * Service for managing provider operations.
     */
    private final IProviderService providerService;

    /**
     * Service for managing owner operations.
     */
    private final OwnerService ownerService;

    /**
     * Constant whit represent the max value of amount in the Expense.
     */
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("999999999.99");

    /**
     * Creates a new expense based on the provided request and file.
     *
     * @param request The DTO containing expense details.
     * @param file    The file associated with the expense (can be null).
     * @param userId  The User ID
     * @return ResponseEntity containing the created expense details.
     * @throws CustomException if the expense is not valid or already exists.
     */
    @Transactional
    @Override
    public ResponseEntity<DtoResponseExpense> postExpense(DtoRequestExpense request, MultipartFile file, Integer userId) {
        Boolean expenseValid = fetchValidExpenseModel(request, file, true);
        if (expenseValid) {
            ExpenseModel expenseModel = mapDtoToExpenseModel(request, userId);
            List<ExpenseInstallmentModel> expenseInstallmentModels = mapDtoToListExpenseInstallmentModel(request, userId);
            List<ExpenseDistributionModel> expenseDistributionModels = new ArrayList<>();
            if (ExpenseType.valueOf(request.getTypeExpense()).equals(ExpenseType.INDIVIDUAL)) {
                expenseDistributionModels = mapDtoToListExpenseDistModel(request, userId);
            }
            expenseModel.setInstallmentsList(expenseInstallmentModels);
            expenseModel.setDistributions(expenseDistributionModels);
            expenseModel.setFileId(null);
            if (file != null && !file.isEmpty()) {
                ResponseEntity<UuidResponseDto> fileId = fileManagerRestClient.uploadFile(file, null, null);
                expenseModel.setFileId(Objects.requireNonNull(fileId.getBody()).getUuid());
            }
            DtoResponseExpense dtoResponseExpense = mapExpModelToDto(expenseModel);
            saveExpenseEntity(expenseModel, expenseInstallmentModels, expenseDistributionModels);
            return ResponseEntity.ok(dtoResponseExpense);
        } else {
            throw new CustomException("The expense is not valid", HttpStatus.CONFLICT);
        }

    }

    /**
     * Retrieves a list of expenses within a specified payment date range.
     *
     * @param from The start date of the payment date range (inclusive).
     * @param to   The end date of the payment date range (inclusive).
     * @return A list of ExpenseModel objects representing the expenses
     * within the specified date range.
     */
    @Override
    public List<ExpenseModel> getExpenseByPaymentDateRange(LocalDate from, LocalDate to) {
        List<ExpenseEntity> expenseEntities = expenseRepository.findAllByPaymentDate(from, to);
        List<ExpenseModel> result = new ArrayList<>();
        for (ExpenseEntity expenseEntity : expenseEntities) {
            result.add(modelMapper.map(expenseEntity, ExpenseModel.class));
        }
        return result;
    }

    /**
     * Updates an existing expense based on the provided request and file.
     *
     * @param request The DTO containing the updated expense details.
     * @param file    The file associated with the expense (can be null).
     * @return A DtoResponseExpense object containing the updated expense details.
     * @throws CustomException if the expense is not valid or does not exist.
     */
    @Transactional
    @Override
    public DtoResponseExpense putExpense(DtoRequestExpense request, MultipartFile file, Integer userId) {
        Boolean expenseValid = fetchValidExpenseModel(request, file, false);
        if (expenseValid) {
            Optional<ExpenseEntity> existingExpenseOpt = expenseRepository.findById(request.getId());
            if (existingExpenseOpt.isEmpty()) {
                throw new CustomException("Expense not found", HttpStatus.NOT_FOUND);
            }
            ExpenseEntity existingExpense = existingExpenseOpt.get();
            ExpenseModel expenseModel = mapDtoToExpenseModel(request, userId);
            List<ExpenseInstallmentModel> newInstallments = mapDtoToListExpenseInstallmentModel(request, userId);
            List<ExpenseDistributionModel> newDistributions = new ArrayList<>();
            if (ExpenseType.valueOf(request.getTypeExpense()).equals(ExpenseType.INDIVIDUAL)) {
                newDistributions = mapDtoToListExpenseDistModel(request, userId);
            }
            if (file != null && !file.isEmpty()) {
                ResponseEntity<UuidResponseDto> fileId = fileManagerRestClient.uploadFile(file, null, null);
                expenseModel.setFileId(Objects.requireNonNull(fileId.getBody()).getUuid());
            } else {
                expenseModel.setFileId(existingExpense.getFileId());
            }

            expenseModel.setInstallmentsList(newInstallments);
            expenseModel.setDistributions(newDistributions);
            updateExpenseEntity(existingExpense, expenseModel, newDistributions, userId);

            DtoResponseExpense dtoResponseExpense = mapExpModelToDto(expenseModel);
            return dtoResponseExpense;
        } else {
            throw new CustomException("The expense is not valid", HttpStatus.CONFLICT);
        }
    }

    /**
     * Updates an existing expense entity.
     *
     * @param existingExpense  The existing expense entity to be updated.
     * @param expenseModel     The expense model containing the updated data.
     * @param userId  The User ID
     * @param newDistributions The list of new expense distribution models.
     */
    public void updateExpenseEntity(ExpenseEntity existingExpense,
                                    ExpenseModel expenseModel,
                                    List<ExpenseDistributionModel> newDistributions, Integer userId) {
        if (existingExpense.getDistributions() == null) {
            existingExpense.setDistributions(new ArrayList<>());
        }
        if (existingExpense.getInstallmentsList() == null) {
            existingExpense.setInstallmentsList(new ArrayList<>());
        }

        // Actualiza los campos básicos de la entidad
        existingExpense.setDescription(expenseModel.getDescription());
        existingExpense.setExpenseDate(expenseModel.getExpenseDate());
        existingExpense.setAmount(expenseModel.getAmount());
        existingExpense.setExpenseType(expenseModel.getExpenseType());
        existingExpense.setCategory(modelMapper.map(expenseModel.getCategory(), ExpenseCategoryEntity.class));
        existingExpense.setFileId(expenseModel.getFileId());
        existingExpense.setLastUpdatedDatetime(LocalDateTime.now());
        existingExpense.setLastUpdatedUser(expenseModel.getLastUpdatedUser());
        existingExpense.setProviderId(expenseModel.getProviderId());
        existingExpense.setInvoiceNumber(expenseModel.getInvoiceNumber());

        // Procesa las distribuciones de gastos
        for (ExpenseDistributionEntity expenseDistributionEntity : existingExpense.getDistributions()) {
            if (newDistributions.stream().noneMatch(m -> m.getOwnerId().equals(expenseDistributionEntity.getOwnerId()))) {
                expenseDistributionEntity.setEnabled(false);
                expenseDistributionEntity.setLastUpdatedUser(userId);
            }
        }
        for (ExpenseDistributionModel expenseDistributionModel : newDistributions) {
            ExpenseDistributionEntity expenseDistributionEntityToEdit = existingExpense.getDistributions()
                    .stream().filter(m -> m.getOwnerId()
                            .equals(expenseDistributionModel.getOwnerId())).findFirst().orElse(null);

            if (expenseDistributionEntityToEdit == null) {

                ExpenseDistributionEntity expenseDistributionEntity = new ExpenseDistributionEntity();
                expenseDistributionEntity.setProportion(expenseDistributionModel.getProportion());
                expenseDistributionEntity.setOwnerId(expenseDistributionModel.getOwnerId());
                expenseDistributionEntity.setCreatedUser(userId);
                expenseDistributionEntity.setExpense(existingExpense);
                expenseDistributionEntity.setLastUpdatedUser(userId);
                existingExpense.getDistributions().add(expenseDistributionEntity);
            } else {

                expenseDistributionEntityToEdit.setProportion(expenseDistributionModel.getProportion());
                expenseDistributionEntityToEdit.setLastUpdatedUser(userId);
                expenseDistributionEntityToEdit.setEnabled(true);
            }
        }

        existingExpense.getInstallmentsList().removeIf(m -> Boolean.FALSE.equals(m.getEnabled()));

        int sizeExistingInstallments = existingExpense.getInstallmentsList().size();
        int sizeNewInstallments = expenseModel.getInstallmentsList().size();
        if (sizeNewInstallments != sizeExistingInstallments) {
            if (sizeNewInstallments > sizeExistingInstallments) {

                Optional<ExpenseInstallmentEntity> maxInstallment = existingExpense.getInstallmentsList()
                        .stream()
                        .max(Comparator.comparing(ExpenseInstallmentEntity::getPaymentDate));

                LocalDate maxDate = maxInstallment.map(ExpenseInstallmentEntity::getPaymentDate)
                        .orElse(LocalDate.now());
                for (int i = sizeExistingInstallments; i < sizeNewInstallments; i++) {
                    maxDate = maxDate.plusMonths(1);
                    ExpenseInstallmentEntity expenseInstallmentEntity = new ExpenseInstallmentEntity();
                    expenseInstallmentEntity.setInstallmentNumber(i + 1);
                    expenseInstallmentEntity.setPaymentDate(maxDate);
                    expenseInstallmentEntity.setCreatedUser(userId);
                    expenseInstallmentEntity.setLastUpdatedUser(userId);
                    expenseInstallmentEntity.setExpense(existingExpense);
                    existingExpense.getInstallmentsList().add(expenseInstallmentEntity);
                }
            } else {

                existingExpense.getInstallmentsList().sort(Comparator.comparing(ExpenseInstallmentEntity::getPaymentDate));
                int count = 1;
                for (ExpenseInstallmentEntity expenseInstallmentEntity : existingExpense.getInstallmentsList()) {
                    if (count > expenseModel.getInstallments()) {
                        expenseInstallmentEntity.setEnabled(false);
                    }
                    count++;
                }
            }
        }
        existingExpense.setInstallments(expenseModel.getInstallments());
        //Guardar la entidad principal actualizada
        expenseRepository.save(existingExpense);
    }

    /**
     * Saves an expense entity along with its associated installment.
     *
     * @param expenseModel The expense model to be saved.
     * @param expenseInstallmentModels  expense installment models
     *  associated with the expense.
     * @param expenseDistributionModels The list of expense distribution
     *  models to be associated with the expense.
     * @throws CustomException if required fields are missing in the expense entity.
     */
    private void saveExpenseEntity(ExpenseModel expenseModel,
                                   List<ExpenseInstallmentModel> expenseInstallmentModels,
                                   List<ExpenseDistributionModel> expenseDistributionModels) {
        ExpenseEntity expenseEntity = modelMapper.map(expenseModel, ExpenseEntity.class);
        expenseEntity.setDistributions(new ArrayList<>());
        expenseEntity.setInstallmentsList(new ArrayList<>());
        expenseEntity.setNoteCredit(false);
        if (expenseEntity.getAmount() == null || expenseEntity.getCategory() == null) {
            throw new CustomException("Missing required fields in ExpenseEntity", HttpStatus.BAD_REQUEST);
        }
        expenseEntity = expenseRepository.save(expenseEntity);

        for (ExpenseDistributionModel distributionModel : expenseDistributionModels) {
            ExpenseDistributionEntity distributionEntity = modelMapper.map(distributionModel, ExpenseDistributionEntity.class);
            distributionEntity.setExpense(expenseEntity);
            expenseDistributionRepository.save(distributionEntity);
            expenseEntity.getDistributions().add(distributionEntity);
        }

        for (ExpenseInstallmentModel installmentModel : expenseInstallmentModels) {
            ExpenseInstallmentEntity expenseInstallmentEntity = modelMapper.map(installmentModel, ExpenseInstallmentEntity.class);
            expenseInstallmentEntity.setExpense(expenseEntity);
            expenseInstallmentRepository.save(expenseInstallmentEntity);
            expenseEntity.getInstallmentsList().add(expenseInstallmentEntity);
        }

    }

    /**
     * Creates a DTO response from an expense model.
     *
     * @param expenseModel The expense model to convert.
     * @return A DtoResponseExpense object containing the expense details.
     */
    private DtoResponseExpense mapExpModelToDto(ExpenseModel expenseModel) {
        DtoResponseExpense dtoResponseExpense = new DtoResponseExpense();
        dtoResponseExpense.setExpenseDate(expenseModel.getExpenseDate());
        dtoResponseExpense.setExpenseType(expenseModel.getExpenseType());
        dtoResponseExpense.setDescription(expenseModel.getDescription());
        dtoResponseExpense.setFileId(expenseModel.getFileId());
        DtoCategory dtoCategory = new DtoCategory();
        dtoCategory.setId(expenseModel.getCategory().getId());
        dtoCategory.setDescription(expenseModel.getCategory().getDescription());
        dtoResponseExpense.setDtoCategory(dtoCategory);
        dtoResponseExpense.setInvoiceNumber(expenseModel.getInvoiceNumber());
        dtoResponseExpense.setProviderId(expenseModel.getProviderId());
        List<DtoInstallment> dtoInstallments = new ArrayList<>();
        for (ExpenseInstallmentModel expenseInstallmentModel : expenseModel.getInstallmentsList()) {
            DtoInstallment dtoInstallment = new DtoInstallment();
            dtoInstallment.setPaymentDate(expenseInstallmentModel.getPaymentDate());
            dtoInstallment.setInstallmentNumber(expenseInstallmentModel.getInstallmentNumber());
            dtoInstallments.add(dtoInstallment);
        }
        dtoResponseExpense.setDtoInstallmentList(dtoInstallments);
        List<DtoDistribution> dtoDistributions = new ArrayList<>();
        for (ExpenseDistributionModel expenseDistributionModel : expenseModel.getDistributions()) {
            DtoDistribution dtoDistribution = new DtoDistribution();
            dtoDistribution.setProportion(expenseDistributionModel.getProportion());
            dtoDistribution.setOwnerId(expenseDistributionModel.getOwnerId());
            dtoDistributions.add(dtoDistribution);
        }
        dtoResponseExpense.setDtoDistributionList(dtoDistributions);
        return dtoResponseExpense;

    }

    /**
     * Creates a list of expense installment models based on
     * the request and expense model.
     *
     * @param userId  The User ID
     * @param request The DTO containing expense details.
     * @return A list of ExpenseInstallmentModel objects.
     */
    private List<ExpenseInstallmentModel> mapDtoToListExpenseInstallmentModel(DtoRequestExpense request, Integer userId) {
        List<ExpenseInstallmentModel> expenseInstallmentModels = new ArrayList<>();
        Integer installments = 1;
        do {
            ExpenseInstallmentModel expenseInstallmentModel = new ExpenseInstallmentModel();
            expenseInstallmentModel.setInstallmentNumber(installments);
            expenseInstallmentModel.setEnabled(Boolean.TRUE);
            expenseInstallmentModel.setCreatedDatetime(LocalDateTime.now());
            expenseInstallmentModel.setCreatedUser(userId);
            if (installments.equals(1)) {
                expenseInstallmentModel.setPaymentDate(LocalDate.now());
            } else {
                expenseInstallmentModel.setPaymentDate(LocalDate.now().plusMonths(installments - 1));
            }

            expenseInstallmentModel.setLastUpdatedDatetime(LocalDateTime.now());
            expenseInstallmentModel.setLastUpdatedUser(userId);
            expenseInstallmentModels.add(expenseInstallmentModel);
            installments++;
        } while (installments <= request.getInstallments());

        return expenseInstallmentModels;

    }

    /**
     * Creates a list of expense distribution models based on the request.
     *
     * @param request The DTO containing expense details.
     * @return A list of ExpenseDistributionModel objects.
     * @param userId  The User ID
     * @throws IllegalArgumentException if the sum of distributions is not valid.
     */
    private List<ExpenseDistributionModel> mapDtoToListExpenseDistModel(DtoRequestExpense request, Integer userId) {
        List<ExpenseDistributionModel> expenseDistributionModels = new ArrayList<>();
        BigDecimal totalProportion = BigDecimal.ZERO;

        for (DtoDistribution dtoDistribution : request.getDistributions()) {
            totalProportion = totalProportion.add(dtoDistribution.getProportion());
            if (totalProportion.compareTo(new BigDecimal("10.00")) > 0 || totalProportion.compareTo(new BigDecimal("-10.00")) < 0) {
                throw new IllegalArgumentException("the sum of distributions can't be less or more than 10.00");
            }
            ExpenseDistributionModel expenseDistributionModel = new ExpenseDistributionModel();
            expenseDistributionModel.setEnabled(Boolean.TRUE);
            expenseDistributionModel.setCreatedUser(userId);
            expenseDistributionModel.setLastUpdatedDatetime(LocalDateTime.now());
            expenseDistributionModel.setCreatedDatetime(LocalDateTime.now());
            expenseDistributionModel.setLastUpdatedUser(userId);
            expenseDistributionModel.setOwnerId(dtoDistribution.getOwnerId());
            expenseDistributionModel.setProportion(dtoDistribution.getProportion());
            expenseDistributionModels.add(expenseDistributionModel);
        }

        return expenseDistributionModels;
    }

    /**
     * Creates an expense model from the provided request.
     *
     * @param userId  The User ID
     * @param request The DTO containing expense details.
     * @return An ExpenseModel object.
     */
    private ExpenseModel mapDtoToExpenseModel(DtoRequestExpense request, Integer userId) {
        ExpenseModel expenseModel = new ExpenseModel();
        expenseModel.setDescription(request.getDescription());
        expenseModel.setProviderId(request.getProviderId());
        expenseModel.setExpenseDate(request.getExpenseDate());
        expenseModel.setInvoiceNumber(request.getInvoiceNumber());
        expenseModel.setExpenseType(ExpenseType.valueOf(request.getTypeExpense()));
        ExpenseCategoryModel expenseCategoryModel = expenseCategoryService.getCategoryModel(request.getCategoryId());
        expenseModel.setCategory(expenseCategoryModel);
        expenseModel.setAmount(request.getAmount());
        expenseModel.setInstallments(request.getInstallments());
        expenseModel.setCreatedDatetime(LocalDateTime.now());
        expenseModel.setLastUpdatedDatetime(LocalDateTime.now());
        expenseModel.setCreatedUser(userId); //the user must be receipted from the frontend
        expenseModel.setLastUpdatedUser(userId);
        expenseModel.setEnabled(Boolean.TRUE);
        return expenseModel;

    }

    /**
     * @param request The DTO containing expense details.
     * @param file    The file associated with the expense (can be null).
     * @param isNew   Indicates if the expense is new or being updated.
     * @return true if the expense is valid, false otherwise.
     * @throws CustomException for various validation errors.
     */
    private Boolean fetchValidExpenseModel(DtoRequestExpense request, MultipartFile file, Boolean isNew) {
        if (isNew) {
            Optional<ExpenseEntity> expenseEntityValidateExist = expenseRepository
                    .findFirstByInvoiceNumberAndProviderId(request.getInvoiceNumber(), request.getProviderId());
            if (expenseEntityValidateExist.isPresent()) {
                throw new CustomException("The expense have already exist", HttpStatus.BAD_REQUEST);
            }
        }
        if (!isNew) {
            Optional<ExpenseEntity> expenseEntityOptional = expenseRepository.findById(request.getId());
            if (expenseEntityOptional.isEmpty()) {
                throw new CustomException("The expense not exist", HttpStatus.BAD_REQUEST);
            }
        }
        ExpenseCategoryModel expenseCategoryModel = expenseCategoryService.getCategoryModel(request.getCategoryId());
        if (expenseCategoryModel == null) {
            throw new CustomException("The category does not exist", HttpStatus.BAD_REQUEST);
        }
        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            request.setDescription("");
        }
        if (request.getProviderId() == null) {
            throw new CustomException("Provider ID cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (request.getExpenseDate() == null) {
            throw new CustomException("Expense date cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (request.getInvoiceNumber() == null) {
            throw new CustomException("Invoice number cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (request.getTypeExpense() == null || request.getTypeExpense().isEmpty()) {
            throw new CustomException("Type of expense cannot be empty", HttpStatus.BAD_REQUEST);
        }
        try {
            ExpenseType.valueOf(request.getTypeExpense());
        } catch (IllegalArgumentException e) {
            throw new CustomException("Type of expense must be one of: COMUN, EXTRAORDINARIO, INDIVIDUAL", HttpStatus.BAD_REQUEST, e);
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException("Amount must be greater than zero", HttpStatus.BAD_REQUEST);
        }
        if (request.getAmount().compareTo(MAX_AMOUNT) > 0) {
            throw new CustomException("Amount can't be greater than max amount", HttpStatus.BAD_REQUEST);
        }
        if (request.getInstallments() == null || request.getInstallments() <= 0) {
            throw new CustomException("Installments must be greater than zero", HttpStatus.BAD_REQUEST);
        }
        if (request.getDistributions().isEmpty() && request.getTypeExpense().equals(ExpenseType.INDIVIDUAL.toString())) {
            throw new CustomException("Distributions cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (file != null && (file.getContentType() == null
                || (!file.getContentType().startsWith("image/") && !"application/pdf".equals(file.getContentType())))) {
            throw new CustomException("the file must be an image or pdf", HttpStatus.BAD_REQUEST);
        }
        return true;
    }

    /**
     * Retrieves an expense by its ID.
     *
     * @param expenseId the ID of the expense to retrieve
     * @return a DtoExpenseQuery object containing the details of the expense
     * @throws CustomException if the expense does not exist or is not enabled
     */
    @Override
    public DtoExpenseQuery getExpenseById(Integer expenseId) {


        // Retrieve the expense entity from the repository
        ExpenseEntity expenseEntity = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new CustomException("The expense does not exist", HttpStatus.NOT_FOUND));

        // Check if the expense is enabled
        if (!expenseEntity.getEnabled()) {
            throw new CustomException("The expense does not exist", HttpStatus.NOT_FOUND);
        }
        Map<Integer, String> providerMap = providerService.getProviders()
                .stream()
                .collect(Collectors.toMap(
                        ProviderDTO::getId,
                        ProviderDTO::getDescription,
                        (existing, replacement) -> existing));
        List<OwnerDto> ownerDtos = ownerService.getOwners();
        // Map the entity to the DTO
        // Initialize the DTO object
        DtoExpenseQuery dtoExpenseQuery = mapEntityToDtoExpense(expenseEntity, providerMap, ownerDtos);

        return dtoExpenseQuery;
    }


    /**
     * Retrieves a list of expenses filtered by the given parameters.
     *
     * @param dateFrom the start date for filtering
     * @param dateTo the end date for filtering expenses
     * (required, format: YYYY-MM-DD)
     * @return a list of DtoExpenseQuery objects that match the filters
     * @throws CustomException if the date range is invalid
     */
    @Override
    public List<DtoExpenseQuery> getExpenses(String dateFrom, String dateTo) {
        List<DtoExpenseQuery> dtoExpenseQueryList = new ArrayList<>();

        if (dateFrom == null || dateTo == null) {
            throw new CustomException("The date range is required", HttpStatus.BAD_REQUEST);
        }

        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate from1;
        LocalDate to1;
        try {
            from1 = LocalDate.parse(dateFrom, formatter1);
            to1 = LocalDate.parse(dateTo, formatter1);
        } catch (DateTimeParseException e) {
            throw new CustomException("The date format is not correct", HttpStatus.BAD_REQUEST, e);
        }

        if (from1.isAfter(to1)) {
            throw new CustomException("The date range is not correct", HttpStatus.BAD_REQUEST);
        }

        Map<Integer, String> providerMap = providerService.getProviders()
                .stream()
                .collect(Collectors.toMap(
                        ProviderDTO::getId,
                        ProviderDTO::getDescription,
                        (existing, replacement) -> existing  // En caso de duplicados, mantener el existente
                ));

        // Retrieve all expenses from the database within the date range
        List<ExpenseEntity> expenseEntityList = expenseRepository.findAllByDate(from1, to1);

        // Llamada al cliente para obtener los propietarios
        List<OwnerDto> ownerDtos = ownerService.getOwners();

        // Aquí mapeamos las entidades de gasto a modelos
        List<ExpenseModel> expenseModelList = new ArrayList<>();
        for (ExpenseEntity expenseEntity : expenseEntityList) {
            // Solo mapeamos si la entidad de gasto está habilitada
            if (expenseEntity.getEnabled()) {
                expenseModelList.add(modelMapper.map(expenseEntity, ExpenseModel.class));
            }
        }

        // Filtramos los modelos de gasto y los convertimos a DtoExpenseQuery
        for (ExpenseModel expenseModel : expenseModelList) {
            DtoExpenseQuery dtoExpenseQuery = mapModelToDtoExpense(expenseModel, ownerDtos, providerMap);

            // Validar fechas
            try {
                LocalDate from = LocalDate.parse(dateFrom, formatter1);
                LocalDate expenseDate = dtoExpenseQuery.getExpenseDate();
                if (expenseDate.isBefore(from)) {
                    continue;
                }
            } catch (DateTimeParseException e) {
                throw new CustomException("The date format is not correct", HttpStatus.BAD_REQUEST, e);
            }

            try {
                LocalDate to = LocalDate.parse(dateTo, formatter1);
                LocalDate expenseDate = dtoExpenseQuery.getExpenseDate();
                if (expenseDate.isAfter(to)) {
                    continue;
                }
            } catch (DateTimeParseException e) {
                throw new CustomException("The date format is not correct", HttpStatus.BAD_REQUEST, e);
            }

            // Añadir el resultado válido a la lista
            dtoExpenseQueryList.add(dtoExpenseQuery);
        }

        return dtoExpenseQueryList;
    }


    /**
     * Maps an `ExpenseEntity` to a `DtoExpenseQuery`.
     *
     * @param expenseEntity The expense entity to map.
     * @param providerMap A map of provider IDs to provider descriptions.
     * @param owners A list of `OwnerDto` objects representing the owners.
     * @return A `DtoExpenseQuery` object containing the mapped expense details.
     */
    public DtoExpenseQuery mapEntityToDtoExpense(ExpenseEntity expenseEntity, Map<Integer, String> providerMap, List<OwnerDto> owners) {
        DtoExpenseQuery dtoExpenseQuery = modelMapper.map(expenseEntity, DtoExpenseQuery.class);

        // Set provider information
        if (expenseEntity.getProviderId() != null) {
            dtoExpenseQuery.setProvider("");
            dtoExpenseQuery.setProviderId(expenseEntity.getProviderId());
        } else {
            dtoExpenseQuery.setProvider("");
        }

        // Set basic expense information
        dtoExpenseQuery.setExpenseDate(expenseEntity.getExpenseDate());
        dtoExpenseQuery.setDescription(expenseEntity.getDescription());
        dtoExpenseQuery.setFileId(expenseEntity.getFileId() != null ? expenseEntity.getFileId().toString() : null);
        dtoExpenseQuery.setCategory(expenseEntity.getCategory().getDescription());
        dtoExpenseQuery.setCategoryId(expenseEntity.getCategory().getId());
        dtoExpenseQuery.setDistributionList(new ArrayList<>());
        dtoExpenseQuery.setInstallmentList(new ArrayList<>());

        // Set provider description from map
        if (expenseEntity.getProviderId() != null) {
            String providerDescription = providerMap.getOrDefault(expenseEntity.getProviderId(), "");
            dtoExpenseQuery.setProvider(providerDescription);
            dtoExpenseQuery.setProviderId(expenseEntity.getProviderId());
        } else {
            dtoExpenseQuery.setProvider("");
            dtoExpenseQuery.setProviderId(null);
        }

        // Map distributions
        for (ExpenseDistributionEntity distributionEntity : expenseEntity.getDistributions()) {
            OwnerDto owner = owners.stream().filter(m -> m.getId().equals(distributionEntity.getOwnerId())).findFirst().orElse(null);
            String ownerName = owner != null ? owner.getName() + " " + owner.getLastName() : "";
            BigDecimal amount = expenseEntity.getAmount().multiply(distributionEntity.getProportion());

            DtoExpenseDistributionQuery dtoExpenseDistributionQuery = new DtoExpenseDistributionQuery();
            dtoExpenseDistributionQuery.setOwnerFullName(ownerName);
            dtoExpenseDistributionQuery.setAmount(amount);
            dtoExpenseDistributionQuery.setOwnerId(distributionEntity.getOwnerId());
            dtoExpenseDistributionQuery.setProportion(distributionEntity.getProportion());

            dtoExpenseQuery.getDistributionList().add(dtoExpenseDistributionQuery);
        }

        // Verificar y mapear installments si no es nulo
        if (expenseEntity.getInstallmentsList() != null) {
            for (ExpenseInstallmentEntity installmentEntity : expenseEntity.getInstallmentsList().stream().filter(
                    ExpenseInstallmentEntity::getEnabled).toList()) {
                DtoExpenseInstallment dtoExpenseInstallment = new DtoExpenseInstallment();
                dtoExpenseInstallment.setInstallmentNumber(installmentEntity.getInstallmentNumber());
                dtoExpenseInstallment.setPaymentDate(installmentEntity.getPaymentDate());

                dtoExpenseQuery.getInstallmentList().add(dtoExpenseInstallment);
            }
        }

        return dtoExpenseQuery;
    }

    /**
     * Maps an {@link ExpenseModel} to a {@link DtoExpenseQuery}.
     *
     * @param expenseModel The expense model to map.
     * @param ownerDtos list of owner DTOs to use for mapping distributions.
     * @param providerMap A map of provider IDs to provider descriptions.
     * @return A {@link DtoExpenseQuery} object containing the mapped data.
     */
    public DtoExpenseQuery mapModelToDtoExpense(ExpenseModel expenseModel, List<OwnerDto> ownerDtos, Map<Integer, String> providerMap) {
        DtoExpenseQuery dtoExpenseQuery = new DtoExpenseQuery();
        dtoExpenseQuery.setId(expenseModel.getId());
        dtoExpenseQuery.setAmount(expenseModel.getAmount().setScale(2, RoundingMode.HALF_UP));
        dtoExpenseQuery.setExpenseType(expenseModel.getExpenseType().name());
        dtoExpenseQuery.setExpenseDate(expenseModel.getExpenseDate());
        dtoExpenseQuery.setFileId(expenseModel.getFileId() != null ? expenseModel.getFileId().toString() : null);
        dtoExpenseQuery.setCategory(expenseModel.getCategory().getDescription());
        dtoExpenseQuery.setCategoryId(expenseModel.getCategory().getId());
        // Set provider information
        if (expenseModel.getProviderId() != null) {
            String providerDescription = providerMap.getOrDefault(expenseModel.getProviderId(), "");
            dtoExpenseQuery.setProvider(providerDescription);
            dtoExpenseQuery.setProviderId(expenseModel.getProviderId());
        } else {
            dtoExpenseQuery.setProvider("Sin proveedor");
            dtoExpenseQuery.setProviderId(null);
        }


        dtoExpenseQuery.setDistributionList(new ArrayList<>());
        dtoExpenseQuery.setInstallmentList(new ArrayList<>());

        // Map distributions, continuando aunque no se encuentre el Owner
        if (expenseModel.getDistributions() != null) {
            for (ExpenseDistributionModel distributionModel : expenseModel.getDistributions()) {
                try {
                    // Intentar obtener el OwnerDto
                    Optional<OwnerDto> ownerDtoOptional = ownerDtos.stream()
                            .filter(m -> m.getId().equals(distributionModel.getOwnerId()))
                            .findFirst();

                    String ownerName = ownerDtoOptional
                            .map(ownerDto -> ownerDto.getLastName() + " " + ownerDto.getName())
                            .orElse("-");

                    // Calcular el monto
                    BigDecimal amount = BigDecimal.ZERO;
                    if (expenseModel.getAmount() != null && distributionModel.getProportion() != null) {
                        amount = expenseModel.getAmount().multiply(distributionModel.getProportion()).setScale(2, RoundingMode.HALF_UP);
                    }

                    DtoExpenseDistributionQuery dtoExpenseDistributionQuery = new DtoExpenseDistributionQuery();
                    dtoExpenseDistributionQuery.setOwnerFullName(ownerName);
                    dtoExpenseDistributionQuery.setAmount(amount);
                    dtoExpenseDistributionQuery.setOwnerId(distributionModel.getOwnerId());

                    dtoExpenseQuery.getDistributionList().add(dtoExpenseDistributionQuery);
                } catch (Exception e) {
                    System.err.println("Error al mapear la distribución: " + e.getMessage());
                    // Continuar el mapeo sin detener el flujo
                }
            }
        }

        // Map installments
        if (expenseModel.getInstallmentsList() != null) {
            for (ExpenseInstallmentModel installmentModel : expenseModel.getInstallmentsList()) {
                try {
                    DtoExpenseInstallment dtoExpenseInstallment = new DtoExpenseInstallment();
                    dtoExpenseInstallment.setInstallmentNumber(installmentModel.getInstallmentNumber());
                    dtoExpenseInstallment.setPaymentDate(installmentModel.getPaymentDate());

                    dtoExpenseQuery.getInstallmentList().add(dtoExpenseInstallment);
                } catch (Exception e) {
                    System.err.println("Error al mapear las cuotas: " + e.getMessage());
                }
            }
        }

        return dtoExpenseQuery;
    }


    /**
     * Deletes an expense logically by setting its enabled status to false.
     *
     * @param id The ID of the expense to be deleted.
     * @return A DtoResponseDeleteExpense object containing the deletion result.
     * @throws CustomException if the expense does not exist or has related bill
     *  installments.
     */
    @Override
    public DtoResponseDeleteExpense deteleExpense(Integer id, Integer userId) {
        Optional<ExpenseEntity> expenseEntityOptional = expenseRepository.findById(id);
        if (expenseEntityOptional.isEmpty()) {
            throw new CustomException("The expense does not exist", HttpStatus.BAD_REQUEST);
        }

        ExpenseEntity expenseEntity = expenseEntityOptional.get();
        Optional<List<BillExpenseInstallmentsEntity>> billExpenseInstallmentsEntity = billExpenseInstallmentsRepository
                .findByExpenseId(id);

        if (billExpenseInstallmentsEntity.map(List::isEmpty).orElse(true)) {
            performLogicalDeletion(expenseEntity);
            DtoResponseDeleteExpense dtoResponseDeleteExpense = new DtoResponseDeleteExpense();
            dtoResponseDeleteExpense.setExpense(expenseEntity.getDescription());
            dtoResponseDeleteExpense.setHttpStatus(HttpStatus.OK);
            dtoResponseDeleteExpense.setDescriptionResponse("Expense delete logic successfully");
            return dtoResponseDeleteExpense;
        } else {
            throw new CustomException("Expense has related bill installments", HttpStatus.CONFLICT);
        }
    }


    /**
     * Performs a logical deletion of an expense by setting its enabled
     * status to false.
     *
     * @param expenseEntity The expense entity to be logically deleted.
     */
    private void performLogicalDeletion(ExpenseEntity expenseEntity) {
        expenseEntity.setEnabled(Boolean.FALSE);
        expenseRepository.save(expenseEntity);
    }

    /**
     * Creates a credit note for an existing expense.
     *
     * @param id The ID of the expense for which to create a credit note.
     * @return A DtoResponseExpense object containing the created credit note details.
     * @throws CustomException if the expense already has a credit note or other
     * validation errors occur.
     */
    @Transactional
    @Override
    public DtoResponseExpense createCreditNoteForExpense(Integer id, Integer userId) {

        DtoResponseExpense dtoResponseExpense = new DtoResponseExpense();
        Optional<ExpenseEntity> expenseEntityOptional = expenseRepository.findById(id);
        if (expenseEntityOptional.get().getNoteCredit()) {
            throw new CustomException("The expense have a note of credit", HttpStatus.CONFLICT);
        }

        ExpenseEntity expenseEntity = expenseEntityOptional.get();
        Optional<List<BillExpenseInstallmentsEntity>> billExpenseInstallmentsEntity = billExpenseInstallmentsRepository
                .findByExpenseId(id);

        if (billExpenseInstallmentsEntity.isPresent()) {
            expenseEntityOptional.get().setNoteCredit(Boolean.TRUE);
            expenseRepository.save(expenseEntityOptional.get());
            int sizeOfInstallments = billExpenseInstallmentsEntity.get().size();
            LocalDate paymentDate = LocalDate.now();

            ExpenseEntity newExpenseEntity = createCreditNoteEntity(expenseEntity, userId);
            expenseRepository.save(newExpenseEntity);

            List<ExpenseInstallmentEntity> expenseInstallmentEntityList = createInstallments(newExpenseEntity,
                    sizeOfInstallments, paymentDate, userId);
            List<ExpenseDistributionEntity> newExpenseDistributionList = new ArrayList<>();
            if (!expenseEntity.getDistributions().isEmpty()) {

                for (ExpenseDistributionEntity originalDistribution : expenseEntity.getDistributions()) {
                    ExpenseDistributionEntity newDistribution = new ExpenseDistributionEntity();
                    newDistribution.setProportion(originalDistribution.getProportion());
                    newDistribution.setExpense(newExpenseEntity);
                    newDistribution.setLastUpdatedUser(userId);
                    newDistribution.setLastUpdatedDatetime(LocalDateTime.now());
                    newDistribution.setCreatedDatetime(LocalDateTime.now());
                    newDistribution.setCreatedUser(userId);
                    newDistribution.setEnabled(Boolean.TRUE);
                    newDistribution.setOwnerId(originalDistribution.getOwnerId());
                    newExpenseDistributionList.add(newDistribution);

                }

                expenseDistributionRepository.saveAll(newExpenseDistributionList);
            }
            dtoResponseExpense = mapExpEntityToDto(expenseEntity);
            saveInstallments(expenseInstallmentEntityList, newExpenseEntity);
        }
        return dtoResponseExpense;
    }

    /**
     * Converts an ExpenseEntity and related data to a DtoResponseExpense.
     *
     * @param expenseEntity The expense entity to convert.
     * @return A DtoResponseExpense object containing the expense details.
     */
    private DtoResponseExpense mapExpEntityToDto(ExpenseEntity expenseEntity) {
        DtoResponseExpense dtoResponseExpense = new DtoResponseExpense();
        dtoResponseExpense.setExpenseDate(expenseEntity.getExpenseDate());
        dtoResponseExpense.setExpenseType(ExpenseType.NOTE_OF_CREDIT);
        dtoResponseExpense.setFileId(expenseEntity.getFileId());
        dtoResponseExpense.setDescription(expenseEntity.getDescription());
        DtoCategory dtoCategory = new DtoCategory();
        dtoCategory.setId(expenseEntity.getCategory().getId());
        dtoCategory.setDescription(expenseEntity.getCategory().getDescription());
        dtoResponseExpense.setDtoCategory(dtoCategory);
        dtoResponseExpense.setInvoiceNumber(expenseEntity.getInvoiceNumber());
        dtoResponseExpense.setProviderId(expenseEntity.getProviderId());
        List<DtoInstallment> dtoInstallments = new ArrayList<>();
        for (ExpenseInstallmentEntity expenseInstallmentEntity : expenseEntity.getInstallmentsList()) {
            DtoInstallment dtoInstallment = new DtoInstallment();
            dtoInstallment.setPaymentDate(expenseInstallmentEntity.getPaymentDate());
            dtoInstallment.setInstallmentNumber(expenseInstallmentEntity.getInstallmentNumber());
            dtoInstallments.add(dtoInstallment);
        }
        dtoResponseExpense.setDtoInstallmentList(dtoInstallments);
        List<DtoDistribution> dtoDistributions = new ArrayList<>();
        for (ExpenseDistributionEntity expenseDistributionEntity : expenseEntity.getDistributions()) {
            DtoDistribution dtoDistribution = new DtoDistribution();
            dtoDistribution.setProportion(expenseDistributionEntity.getProportion());
            dtoDistribution.setOwnerId(expenseDistributionEntity.getOwnerId());
            dtoDistributions.add(dtoDistribution);
        }
        dtoResponseExpense.setDtoDistributionList(dtoDistributions);
        return dtoResponseExpense;
    }

    /**
     * Creates a new ExpenseEntity for a credit note based on an original expense.
     *
     * @param userId  The User ID
     * @param originalExpenseEntity The original expense entity.
     * @return A new ExpenseEntity representing the credit note.
     */
    private ExpenseEntity createCreditNoteEntity(ExpenseEntity originalExpenseEntity, Integer userId) {
        ExpenseEntity newExpenseEntity = new ExpenseEntity();
        newExpenseEntity.setExpenseType(ExpenseType.NOTE_OF_CREDIT);
        newExpenseEntity.setEnabled(Boolean.TRUE);
        newExpenseEntity.setNoteCredit(Boolean.TRUE);
        newExpenseEntity.setExpenseDate(LocalDate.now());
        newExpenseEntity.setDescription("Note of credit"); // description??
        newExpenseEntity.setDistributions(new ArrayList<>());
        newExpenseEntity.setAmount(originalExpenseEntity.getAmount().negate());
        newExpenseEntity.setFileId(originalExpenseEntity.getFileId());
        newExpenseEntity.setCategory(originalExpenseEntity.getCategory());
        newExpenseEntity.setInvoiceNumber(originalExpenseEntity.getInvoiceNumber());
        newExpenseEntity.setProviderId(originalExpenseEntity.getProviderId());
        newExpenseEntity.setCreatedDatetime(LocalDateTime.now());
        newExpenseEntity.setCreatedUser(userId);
        newExpenseEntity.setLastUpdatedDatetime(LocalDateTime.now());
        newExpenseEntity.setLastUpdatedUser(userId);
        newExpenseEntity.setInstallments(originalExpenseEntity.getInstallments());
        newExpenseEntity.setInstallmentsList(new ArrayList<>());

        return newExpenseEntity;
    }

    /**
     * Creates a list of ExpenseInstallmentEntity objects for a credit note.
     *
     * @param newExpenseEntity The new expense entity representing the credit note.
     * @param sizeOfInstallments The number of installments to create.
     * @param userId  The User ID
     * @param paymentDate The initial payment date for the installments.
     * @return A list of ExpenseInstallmentEntity objects.
     */
    private List<ExpenseInstallmentEntity> createInstallments(ExpenseEntity newExpenseEntity,
                                                              int sizeOfInstallments, LocalDate paymentDate, Integer userId) {
        List<ExpenseInstallmentEntity> expenseInstallmentEntityList = new ArrayList<>();

        for (int i = 0; i < sizeOfInstallments; i++) {
            ExpenseInstallmentEntity installment = new ExpenseInstallmentEntity();
            installment.setExpense(newExpenseEntity);
            installment.setInstallmentNumber(i + 1);
            installment.setCreatedDatetime(LocalDateTime.now());
            installment.setEnabled(Boolean.TRUE);
            installment.setPaymentDate(paymentDate.plusMonths(i));
            installment.setCreatedUser(userId);
            installment.setLastUpdatedDatetime(LocalDateTime.now());
            installment.setLastUpdatedUser(userId);

            expenseInstallmentEntityList.add(installment);
        }

        return expenseInstallmentEntityList;
    }

    /**
     * Saves a list of ExpenseInstallmentEntity objects and associates
     * them with a new expense entity.
     *
     * @param expenseInstallmentEntityList The list of expense installment
     * entities to save.
     * @param newExpenseEntity  The new expense entity .
     */
    private void saveInstallments(List<ExpenseInstallmentEntity> expenseInstallmentEntityList, ExpenseEntity newExpenseEntity) {
        for (ExpenseInstallmentEntity expenseInstallmentEntity : expenseInstallmentEntityList) {
            expenseInstallmentEntity.setExpense(newExpenseEntity);
            expenseInstallmentRepository.save(expenseInstallmentEntity);
        }
    }
}


