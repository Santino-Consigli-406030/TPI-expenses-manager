package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.ExpenseCategoryDTO;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.ExpenseOwnerVisualizerDTO;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.ProviderDTO;
import ar.edu.utn.frc.tup.lc.iv.dtos.owner.OwnerDto;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseDistributionEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseEntity;
import ar.edu.utn.frc.tup.lc.iv.repositories.ExpenseRepository;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IExpenseDistributionService;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing expense distributions.
 */
@Service
@RequiredArgsConstructor
public class ExpenseDistributionService implements IExpenseDistributionService {

    /**
     * Service class for managing Owner RestClient.
     */
    private final OwnerService ownerService;
    /**
     * Repository for managing expense entities.
     */
    private final ExpenseRepository expenseRepository;

    /**
     * Service class for managing providers RestClient.
     */
    private final IProviderService providerService;

    /**
     * @param userId         the ID of the user.
     * @param startDateString the start date in yyyy-MM-dd format.
     * @param endDateString   the end date in yyyy-MM-dd format.
     * @return a list of ExpenseOwnerVisualizerDTO for the owner.
     * @throws IllegalArgumentException in case of error
     */
    @Override
    public List<ExpenseOwnerVisualizerDTO> findByOwnerId(Integer userId, String startDateString, String endDateString) {
        OwnerDto owner = ownerService.getOwnerByUserId(userId);
        if (owner == null || owner.getId() <= 0) {
            throw new IllegalArgumentException("El ID del propietario no puede ser nulo o negativo.");
        }
        Integer ownerId = owner.getId();
        // Formateador de fechas en formato yyyy-MM-dd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<ProviderDTO> providerList = providerService.getProviders();
        // Crear un Map para acceso rápido a las descripciones de proveedores
        Map<Integer, String> providerDescriptions = providerList.stream()
                .collect(Collectors.toMap(ProviderDTO::getId, ProviderDTO::getDescription));

        LocalDate startDate;
        LocalDate endDate;

        // Validar y convertir la fecha de inicio
        try {
            startDate = LocalDate.parse(startDateString, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha de inicio debe estar en el formato yyyy-MM-dd.", e);
        }

        // Validar y convertir la fecha de fin
        try {
            endDate = LocalDate.parse(endDateString, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha de fin debe estar en el formato yyyy-MM-dd.", e);
        }

        List<ExpenseEntity> expenses = expenseRepository.findAllByDate(startDate, endDate);
        if (expenses == null || expenses.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron gastos en el rango de fechas proporcionado.");
        }

        List<ExpenseOwnerVisualizerDTO> expenseOwnerVisualizerDTOList = new ArrayList<>();
        for (ExpenseEntity expense : expenses) {
            if (expense.getDistributions().isEmpty()) {
                expenseOwnerVisualizerDTOList.add(expenseOwnerVisualizerDTOBuilder(
                        expense, ownerId, BigDecimal.valueOf(1), providerDescriptions));
            } else {
                Optional<ExpenseDistributionEntity> expenseOwner = expense.getDistributions().stream()
                        .filter(m -> m.getOwnerId().equals(ownerId) && m.getEnabled())
                        .findFirst();
                if (expenseOwner.isPresent()) {
                    expenseOwnerVisualizerDTOList.add(expenseOwnerVisualizerDTOBuilder(
                            expense, ownerId, expenseOwner.get().getProportion(), providerDescriptions));
                }
            }
        }
        return expenseOwnerVisualizerDTOList;
    }

    private ExpenseOwnerVisualizerDTO expenseOwnerVisualizerDTOBuilder(
            ExpenseEntity expense,
            Integer ownerId,
            BigDecimal proportion,
            Map<Integer, String> providerDescriptions) {

        if (expense == null) {
            throw new IllegalArgumentException("El gasto no puede ser nulo.");
        }
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("El ID del propietario no puede ser nulo o negativo.");
        }
        if (proportion == null || proportion.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La proporción no puede ser nula o negativa.");
        }

        return ExpenseOwnerVisualizerDTO.builder()
                .id(ownerId)
                .expenseId(expense.getId())
                .description(expense.getDescription())
                .providerId(expense.getProviderId())
                .providerDescription(providerDescriptions.getOrDefault(expense.getProviderId(), "Sin proveedor"))
                .expenseDate(expense.getExpenseDate())
                .fileId(expense.getFileId())
                .invoiceNumber(expense.getInvoiceNumber())
                .expenseType(expense.getExpenseType())
                .category(ExpenseCategoryDTO.builder()
                        .id(expense.getCategory().getId())
                        .description(expense.getCategory().getDescription())
                        .build())
                .proportion(proportion)
                .amount(expense.getAmount().multiply(proportion).setScale(2, RoundingMode.HALF_UP))
                .installments(expense.getInstallments())
                .enabled(expense.getEnabled())
                .build();
    }
}
