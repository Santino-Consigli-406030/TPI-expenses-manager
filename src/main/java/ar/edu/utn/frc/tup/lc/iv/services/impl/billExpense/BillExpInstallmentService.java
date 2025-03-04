package ar.edu.utn.frc.tup.lc.iv.services.impl.billExpense;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseQuery;
import ar.edu.utn.frc.tup.lc.iv.repositories.BillExpenseInstallmentsRepository;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.billExpense.IBillExpInstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * Service class for managing installment Bill Record.
 */
@Service
@RequiredArgsConstructor
public class BillExpInstallmentService implements IBillExpInstallmentService {
    /**
     * Repository for accessing BillExpenseInstallments data.
     */
    private final BillExpenseInstallmentsRepository billExpenseInstallmentsRepository;
    /**
     * Retrieves a Map of installment IDs and their associated expense types.
     *
     * @param id The ID of the BillRecord.
     * @return Map with the installment ID as the key
     *  and the DtoExpenseQuery with contain expense type and category.
     */
    @Override
    public Map<Integer, DtoExpenseQuery> getInstallmentAndExpenseType(Integer id) {
        List<Object[]> repo = billExpenseInstallmentsRepository.findInstallmentIdAndExpenseTypeByBillRecordId(id);
        return repo.stream().collect(Collectors.toMap(row -> (Integer) row[0], row -> {
            DtoExpenseQuery info = new DtoExpenseQuery();
            info.setExpenseType((String) row[1]);
            info.setCategory((String) row[2]);
            return info;
        }));
    }
}
