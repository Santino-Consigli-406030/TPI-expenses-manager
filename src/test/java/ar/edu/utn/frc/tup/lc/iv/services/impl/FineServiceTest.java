package ar.edu.utn.frc.tup.lc.iv.services.impl;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ar.edu.utn.frc.tup.lc.iv.client.SanctionRestClient;
import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.sanction.FineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class FineServiceTest {

    @Mock
    private SanctionRestClient sanctionRestClient;
    @InjectMocks
    private FineService fineService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetFineByPeriod_success() throws Exception {
        FineDto fineDto = new FineDto();
        fineDto.setAmount(BigDecimal.valueOf(100));
        fineDto.setDescription("Fine");
        ResponseEntity<FineDto[]> responseEntity = new ResponseEntity<>(new FineDto[]{fineDto}, HttpStatus.OK);
        when(sanctionRestClient.getFines(any(PeriodDto.class))).thenReturn(responseEntity);
        List<FineDto> fines = fineService.getFineByPeriod(new PeriodDto());
        assertNotNull(fines);
        assertEquals(1, fines.size());
        assertEquals(100, fines.get(0).getAmount().intValue());  // Suponiendo que `getAmount()` es un getter en FineDto
    }

    @Test
    public void testGetFineByPeriod_serviceUnavailable() {
        // Configurar el comportamiento para simular un error
        ResponseEntity<FineDto[]> responseEntity = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        when(sanctionRestClient.getFines(any(PeriodDto.class))).thenReturn(responseEntity);
        assertThrows(CustomException.class, () -> fineService.getFineByPeriod(new PeriodDto()));
    }

    @Test
    public void testGetFineByPeriod_emptyResponse() {
        ResponseEntity<FineDto[]> responseEntity = new ResponseEntity<>(new FineDto[0], HttpStatus.OK);
        when(sanctionRestClient.getFines(any(PeriodDto.class))).thenReturn(responseEntity);
        List<FineDto> fines = fineService.getFineByPeriod(new PeriodDto());
        assertTrue(fines.isEmpty());
    }
}
