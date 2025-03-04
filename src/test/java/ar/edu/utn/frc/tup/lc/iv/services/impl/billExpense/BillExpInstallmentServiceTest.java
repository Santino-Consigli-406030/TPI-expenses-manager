package ar.edu.utn.frc.tup.lc.iv.services.impl.billExpense;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoExpenseQuery;
import ar.edu.utn.frc.tup.lc.iv.repositories.BillExpenseInstallmentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;





public class BillExpInstallmentServiceTest {

    @Mock
    private BillExpenseInstallmentsRepository billExpenseInstallmentsRepository;

    @InjectMocks
    private BillExpInstallmentService billExpInstallmentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetInstallmentAndExpenseType() {
        Integer billRecordId = 1;
        List<Object[]> repoData = Arrays.asList(
                new Object[]{1, "Food", "Groceries"},
                new Object[]{2, "Transport", "Fuel"}
        );

        when(billExpenseInstallmentsRepository.findInstallmentIdAndExpenseTypeByBillRecordId(billRecordId)).thenReturn(repoData);

        Map<Integer, DtoExpenseQuery> result = billExpInstallmentService.getInstallmentAndExpenseType(billRecordId);

        assertEquals(2, result.size());

        DtoExpenseQuery expense1 = result.get(1);
        assertEquals("Food", expense1.getExpenseType());
        assertEquals("Groceries", expense1.getCategory());

        DtoExpenseQuery expense2 = result.get(2);
        assertEquals("Transport", expense2.getExpenseType());
        assertEquals("Fuel", expense2.getCategory());
    }
}
