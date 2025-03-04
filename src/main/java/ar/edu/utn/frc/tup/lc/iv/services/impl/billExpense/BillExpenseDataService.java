package ar.edu.utn.frc.tup.lc.iv.services.impl.billExpense;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.owner.OwnerDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.owner.PlotDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.sanction.FineDto;
import ar.edu.utn.frc.tup.lc.iv.entities.BillRecordEntity;
import ar.edu.utn.frc.tup.lc.iv.models.BillExpenseFineModel;
import ar.edu.utn.frc.tup.lc.iv.models.BillExpenseOwnerModel;
import ar.edu.utn.frc.tup.lc.iv.models.BillRecordModel;
import ar.edu.utn.frc.tup.lc.iv.repositories.BillRecordRepository;
import ar.edu.utn.frc.tup.lc.iv.services.impl.FineService;
import ar.edu.utn.frc.tup.lc.iv.services.impl.OwnerService;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.billExpense.IBillExpenseDataService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * Service class for managing data necessary for BillRecord Service.
 */
@Service
@RequiredArgsConstructor
public class BillExpenseDataService implements IBillExpenseDataService {
    /**
     * Repository for accessing bill records.
     */
    private final BillRecordRepository billRecordRepository;

    /**
     * Service for managing owners.
     */
    private final OwnerService ownerService;

    /**
     * Service for managing fines.
     */
    private final FineService fineService;

    /**
     * Mapper for converting entities to models and vice versa.
     */
    private final ModelMapper modelMapper;

    /**
     * Retrieves a BillRecord from the database for
     *  the specified period if it exists.
     *
     * @param periodDto {@link PeriodDto} Contains
     *  the start and end date for the period.
     * @return {@link BillRecordModel} The BillRecord
     *  for the period, or null if no record exists.
     */
    @Override
    public BillRecordModel getBillRecord(PeriodDto periodDto) {
        BillRecordModel result = null;
        Optional<BillRecordEntity> optBillRecordEntity = billRecordRepository
                .findFirstByStartAndEndAndEnabledTrue(periodDto.getStartDate(), periodDto.getEndDate());
        if (optBillRecordEntity.isPresent()) {
            result = modelMapper.map(optBillRecordEntity.get(), BillRecordModel.class);
        }
        return result;
    }

    /**
     * Checks if there is an existing BillRecord
     *  that overlaps with the specified period.
     *
     * @param periodDto {@link PeriodDto} Contains
     *  the start and end date for the period.
     * @return true if there is an overlapping BillRecord, false otherwise.
     */
    @Override
    public boolean existBillRecordInPeriod(PeriodDto periodDto) {
        return !billRecordRepository.findAnyByStartAndEnd(periodDto.getStartDate(), periodDto.getEndDate()).isEmpty();
    }

    /**
     * Generates a list of owners, calculates
     *  their total field size, and assigns applicable fines.
     * Uses a Map for faster lookup of fines by plotId to optimize performance.
     *
     * @param periodDto  {@link PeriodDto} Contains
     *  the start and end date for the period.
     * @param createUser User Id.
     * @return List of {@link BillExpenseOwnerModel} with owners
     *  and their corresponding fines.
     */
    @Override
    public List<BillExpenseOwnerModel> constraintBillExpenseOwners(PeriodDto periodDto, Integer createUser) {
        List<OwnerDto> ownersDto = getOwners();
        List<FineDto> finesDto = getFines(periodDto);
        List<BillExpenseOwnerModel> result = new ArrayList<>();

        // Create a Map that relates each plotId to its list of fines for fast lookup
        Map<Integer, List<FineDto>> plotIdToFinesMap = finesDto.stream()
                .collect(Collectors.groupingBy(FineDto::getPlotId));

        for (OwnerDto owner : ownersDto) {
            BillExpenseOwnerModel billExpenseOwnerModel = new BillExpenseOwnerModel();
            billExpenseOwnerModel.setOwnerId(owner.getId());
            billExpenseOwnerModel.setFieldSize(owner.getPlots().stream().mapToInt(PlotDto::getFieldSize).sum());
            billExpenseOwnerModel.setBillExpenseFines(new ArrayList<>());
            billExpenseOwnerModel.setBillExpenseInstallments(new ArrayList<>());

            billExpenseOwnerModel.setCreatedUser(createUser);
            billExpenseOwnerModel.setLastUpdatedUser(createUser);

            // For each plot owned by the current owner, fetch the related fines from the Map
            for (PlotDto plot : owner.getPlots()) {
                // Get the list of fines for the current plotId from the Map,
                // or an empty list if none exist
                List<FineDto> finesForPlot = plotIdToFinesMap.getOrDefault(plot.getId(), Collections.emptyList());

                // Add each fine related to this plot to the owner's list of fines
                for (FineDto fine : finesForPlot) {
                    BillExpenseFineModel billExpenseFineModel = new BillExpenseFineModel();
                    billExpenseFineModel.setFineId(fine.getId());
                    billExpenseFineModel.setAmount(fine.getAmount());
                    billExpenseFineModel.setDescription(fine.getDescription());
                    billExpenseFineModel.setPlotId(fine.getPlotId());

                    billExpenseFineModel.setCreatedUser(createUser);
                    billExpenseFineModel.setLastUpdatedUser(createUser);

                    billExpenseOwnerModel.getBillExpenseFines().add(billExpenseFineModel);
                }
            }

            result.add(billExpenseOwnerModel);
        }

        return result;
    }

    /**
     * Retrieves a list of owners from the owner service using a REST client.
     *
     * @return List of {@link OwnerDto} with owner details.
     */
    public List<OwnerDto> getOwners() {
        List<OwnerDto> result;
        try {
            result = ownerService.getOwners();
        } catch (CustomException e) {
            throw new CustomException(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE, e);
        }
        if (result.isEmpty()) {
            throw new CustomException("No owners found", HttpStatus.NOT_FOUND);
        }
        return result;
    }

    /**
     * Retrieves a list of fines applicable within the
     *  specified period from the sanctions service.
     *
     * @param periodDto {@link PeriodDto} Contains the start
     *  and end date for the period.
     * @return List of {@link FineDto} with fine details.
     */
    public List<FineDto> getFines(PeriodDto periodDto) {
        List<FineDto> result;
        try {
            result = fineService.getFineByPeriod(periodDto);
        } catch (CustomException e) {
            throw new CustomException(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE, e);
        }
        return result;
    }
}
