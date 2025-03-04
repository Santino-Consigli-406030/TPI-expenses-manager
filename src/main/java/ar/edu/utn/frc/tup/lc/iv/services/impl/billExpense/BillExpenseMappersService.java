package ar.edu.utn.frc.tup.lc.iv.services.impl.billExpense;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.response.BillExpenseDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.response.BillOwnerDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.response.ItemDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseQuery;
import ar.edu.utn.frc.tup.lc.iv.dtos.sanction.FineDto;
import ar.edu.utn.frc.tup.lc.iv.entities.BillExpenseFineEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.BillExpenseInstallmentsEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.BillExpenseOwnerEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.BillRecordEntity;
import ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType;
import ar.edu.utn.frc.tup.lc.iv.models.BillExpenseFineModel;
import ar.edu.utn.frc.tup.lc.iv.models.BillExpenseInstallmentModel;
import ar.edu.utn.frc.tup.lc.iv.models.BillExpenseOwnerModel;
import ar.edu.utn.frc.tup.lc.iv.models.BillRecordModel;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.billExpense.IBillExpenseMappersService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * Service class for managing mapper necessary for BillRecord Service.
 */
@Service
@RequiredArgsConstructor
public class BillExpenseMappersService implements IBillExpenseMappersService {
    /**
     * Mapper for converting between models and entities.
     */
    private final ModelMapper modelMapper;
    /**
     * Service for handling bill expense installments.
     */
    private final BillExpInstallmentService billExpInstallmentService;


    /**
     * Maps a {@link BillRecordModel} to a {@link BillRecordEntity}.
     * This method also ensures that the relationships
     * between {@link BillRecordEntity} and its
     * children ({@link BillExpenseOwnerEntity},
     * {@link BillExpenseFineEntity}, {@link BillExpenseInstallmentsEntity})
     * are properly set before the entity is persisted.
     *
     * @param billRecordModel The model to map.
     * @return {@link BillRecordEntity} The mapped entity
     * with properly assigned relationships.
     */
    @Override
    public BillRecordEntity billRecordModelToEntity(BillRecordModel billRecordModel) {
        BillRecordEntity billRecordEntity = modelMapper.map(billRecordModel, BillRecordEntity.class);

        // Ensure that each child (BillExpenseOwnerEntity)
        // has a reference to its parent (BillRecordEntity)
        for (BillExpenseOwnerEntity ownerEntity : billRecordEntity.getBillExpenseOwner()) {
            if (ownerEntity.getBillRecord() == null) {
                ownerEntity.setBillRecord(billRecordEntity);
            }

            // Ensure each fine (BillExpenseFineEntity) has a
            // reference to its owner (BillExpenseOwnerEntity)
            for (BillExpenseFineEntity fineEntity : ownerEntity.getBillExpenseFines()) {
                if (fineEntity.getBillExpenseOwner() == null) {
                    fineEntity.setBillExpenseOwner(ownerEntity);
                }
            }

            // Ensure each installment (BillExpenseInstallmentsEntity) has
            // a reference to its owner (BillExpenseOwnerEntity)
            for (BillExpenseInstallmentsEntity installmentEntity : ownerEntity.getBillExpenseInstallments()) {
                if (installmentEntity.getBillExpenseOwner() == null) {
                    installmentEntity.setBillExpenseOwner(ownerEntity);
                }
            }
        }

        return billRecordEntity;
    }

    /**
     * Maps a {@link BillRecordModel} to a {@link BillExpenseDto}.
     *
     * @param billRecordModel The model to map.
     * @return {@link BillExpenseDto} The mapped DTO.
     */
    @Override
    public BillExpenseDto billRecordModelToDto(BillRecordModel billRecordModel) {
        try {
            Map<Integer, DtoExpenseQuery> installmentsType = billExpInstallmentService.getInstallmentAndExpenseType(
                    billRecordModel.getId()
            );
            BillExpenseDto billExpenseDto = new BillExpenseDto();
            billExpenseDto.setId(billRecordModel.getId());
            billExpenseDto.setStartDate(billRecordModel.getStart());
            billExpenseDto.setEndDate(billRecordModel.getEnd());
            billExpenseDto.setOwners(new ArrayList<>());

            // Map each BillExpenseOwnerModel to BillOwnerDto
            for (BillExpenseOwnerModel ownerModel : billRecordModel.getBillExpenseOwner()) {
                billExpenseDto.getOwners().add(billOwnerModelToDto(ownerModel, installmentsType));
            }

            return billExpenseDto;
        } catch (Exception ex) {
            throw new CustomException("Error occurred processing the bill record. The record was created or already exists, "
                    + "but the return process failed.", HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }
    }

    /**
     * Maps a {@link BillRecordEntity} to a {@link BillRecordModel}.
     * This method uses the ModelMapper to convert the entity to the model.
     *
     * @param entity The entity to map.
     * @return {@link BillRecordModel} The mapped model.
     */
    @Override
    public BillRecordModel entityToBillRecordModel(BillRecordEntity entity) {
        return modelMapper.map(entity, BillRecordModel.class);
    }

    /**
     * Maps a {@link BillExpenseOwnerModel} to a {@link BillOwnerDto}. This
     * method processes the installments and fines associated with the owner,
     * classifies the expenses by type, and sums the amounts by category.
     *
     * @param ownerModel The model to map.
     * @param installmentsType A map containing the expense type and category
     * information for each installment.
     * @return {@link BillOwnerDto} The mapped DTO.
     * @throws CustomException if the expense type or category is not found for
     * an installment, or if an invalid expense type is encountered.
     */
    private BillOwnerDto billOwnerModelToDto(BillExpenseOwnerModel ownerModel, Map<Integer, DtoExpenseQuery> installmentsType) {
        BillOwnerDto ownerDto = BillOwnerDto.builder()
                .id(ownerModel.getOwnerId())
                .fieldSize(ownerModel.getFieldSize())
                .expensesCommon(new ArrayList<>())
                .expensesExtraordinary(new ArrayList<>())
                .expensesIndividual(new ArrayList<>())
                .fines(new ArrayList<>())
                .notesOfCredit(new ArrayList<>())
                .build();

        // Map para agrupar y sumar los montos por categoría
        Map<String, BigDecimal> commonExpensesByCategory = new HashMap<>();
        Map<String, BigDecimal> extraordinaryExpensesByCategory = new HashMap<>();
        Map<String, BigDecimal> individualExpensesByCategory = new HashMap<>();
        Map<String, BigDecimal> notesOfCreditByCategory = new HashMap<>();

        // Procesar los installments para clasificar por tipo y sumar por categoría
        for (BillExpenseInstallmentModel installmentModel : ownerModel.getBillExpenseInstallments()) {
            DtoExpenseQuery info = installmentsType.get(installmentModel.getId());
            if (info == null) {
                throw new CustomException("Expense Type or Category not found"
                        + " for installment ID: " + installmentModel.getId(), HttpStatus.BAD_REQUEST);
            }
            // Validación del tipo de expensa
            String expenseTypeStr = info.getExpenseType();
            ExpenseType expenseType;

            try {
                expenseType = ExpenseType.valueOf(expenseTypeStr);
            } catch (IllegalArgumentException e) {
                throw new CustomException("Invalid Expense Type: " + expenseTypeStr
                        + " for installment ID: " + installmentModel.getId(), HttpStatus.BAD_REQUEST, e);
            }

            // Procesar el tipo de expensa
            switch (expenseType) {
                case COMUN -> commonExpensesByCategory.merge(info.getCategory(),
                        installmentModel.getAmount(), BigDecimal::add);
                case EXTRAORDINARIO -> extraordinaryExpensesByCategory.merge(info.getCategory(),
                        installmentModel.getAmount(), BigDecimal::add);
                case INDIVIDUAL -> individualExpensesByCategory.merge(info.getCategory(),
                        installmentModel.getAmount(), BigDecimal::add);
                case NOTE_OF_CREDIT -> notesOfCreditByCategory.merge(info.getCategory(),
                        installmentModel.getAmount(), BigDecimal::add);
                default -> throw new CustomException("Expense type undefined", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // Convertir los mapas a listas de ItemDto
        ownerDto.setExpensesCommon(mapToItemDtoList(commonExpensesByCategory));
        ownerDto.setExpensesExtraordinary(mapToItemDtoList(extraordinaryExpensesByCategory));
        ownerDto.setExpensesIndividual(mapToItemDtoList(individualExpensesByCategory));
        ownerDto.setNotesOfCredit(mapToItemDtoList(notesOfCreditByCategory));

        // Mapear las multas
        for (BillExpenseFineModel fineModel : ownerModel.getBillExpenseFines()) {
            ownerDto.getFines().add(billFineModelToDto(fineModel));
        }

        return ownerDto;
    }


    /**
     * Converts a map of expenses by category to a list of {@link ItemDto}.
     *
     * @param expensesByCategory A map with the category as the key and the
     * total amount as the value.
     * @return A list of {@link ItemDto} objects representing each expense
     * category and its total amount.
     */
    private List<ItemDto> mapToItemDtoList(Map<String, BigDecimal> expensesByCategory) {
        return expensesByCategory.entrySet().stream()
                .map(entry -> builderItemDto(null, entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());
    }

    /**
     * Maps a {@link BillExpenseFineModel} to an {@link ItemDto}.
     * This method uses the builderItemDto method
     * to create an {@link ItemDto} from the fine model.
     *
     * @param fineModel The model to map.
     * @return {@link FineDto} The mapped DTO, containing
     * the fine ID, plot id,amount, and description.
     */
    private FineDto billFineModelToDto(BillExpenseFineModel fineModel) {
        return FineDto.builder()
                .id(fineModel.getFineId())
                .plotId(fineModel.getPlotId())
                .description(fineModel.getDescription())
                .amount(fineModel.getAmount())
                .build();
    }
    /**
     * Creates an {@link ItemDto} object using the provided id,
     * amount, and description.
     * This is a helper method to standardize the creation
     * of {@link ItemDto} from various models.
     *
     * @param id The ID of the item.
     * @param amount The amount related to the item.
     * @param description The description of the item.
     * @return {@link ItemDto} The built DTO containing
     * the id, amount, and description.
     */
    private ItemDto builderItemDto(Integer id, BigDecimal amount, String description) {
        return ItemDto.builder()
                .id(id)
                .amount(amount)
                .description(description)
                .build();
    }
}
