package ar.edu.utn.frc.tup.lc.iv.client;

import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.sanction.FineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;




public class SanctionRestClientTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private SanctionRestClient sanctionRestClient;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    sanctionRestClient = new SanctionRestClient(restTemplate);
  }

  @Test
  public void testGetFines() {
    PeriodDto periodDto = new PeriodDto();
    periodDto.setStartDate(LocalDate.now());
    periodDto.setEndDate(LocalDate.now().plusDays(1));
    FineDto[] fineDtos = new FineDto[]{new FineDto()};
    ResponseEntity<FineDto[]> responseEntity = new ResponseEntity<>(fineDtos, HttpStatus.OK);

    when(restTemplate.getForEntity(anyString(), eq(FineDto[].class), anyString(),anyString())).thenReturn(responseEntity);

    ResponseEntity<FineDto[]> response = sanctionRestClient.getFines(periodDto);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertArrayEquals(fineDtos, response.getBody());
  }
}