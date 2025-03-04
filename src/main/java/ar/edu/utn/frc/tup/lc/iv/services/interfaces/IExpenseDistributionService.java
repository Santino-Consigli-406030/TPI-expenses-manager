package ar.edu.utn.frc.tup.lc.iv.services.interfaces;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.ExpenseOwnerVisualizerDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing expense distribution.
 */
@Service
public interface IExpenseDistributionService {


    /**
     * Finds expenses by owner ID within a specified date range.
     *
     * @param ownerId   the ID of the owner.
     * @param startDate the start date of the period to filter.
     * @param endDate   the end date of the period to filter.
     * @return a list of {@link ExpenseOwnerVisualizerDTO}.
     */
    List<ExpenseOwnerVisualizerDTO> findByOwnerId(Integer ownerId, String startDate, String endDate);
}
