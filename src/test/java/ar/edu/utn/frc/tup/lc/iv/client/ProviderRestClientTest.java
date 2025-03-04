package ar.edu.utn.frc.tup.lc.iv.client;

import ar.edu.utn.frc.tup.lc.iv.client.ProviderRestClient;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.ProviderDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProviderRestClientTest {

    @Mock
    private RestTemplate restTemplate;


    private ProviderRestClient providerRestClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        providerRestClient = new ProviderRestClient(restTemplate,"");
    }

    @Test
    public void testGetAllProviders_Success() {
        // Mockear la respuesta de la API
        List<Map<String, Object>> apiResponse = List.of(
                Map.of("id", 1, "name", "Provider A"),
                Map.of("id", 2, "name", "Provider B"),
                Map.of("id", 3, "name", "Provider C")
        );

        // Configurar el mock de restTemplate
        when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(apiResponse);

        // Llamar al método a probar
        ResponseEntity<ProviderDTO[]> response = providerRestClient.getAllProviders();

        // Verificar los resultados
        ProviderDTO[] providers = response.getBody();
        assertNotNull(providers);
        assertEquals(3, providers.length);

        // Validar los valores de cada ProviderDTO
        assertEquals(1, providers[0].getId());
        assertEquals("Provider A", providers[0].getDescription());

        assertEquals(2, providers[1].getId());
        assertEquals("Provider B", providers[1].getDescription());

        assertEquals(3, providers[2].getId());
        assertEquals("Provider C", providers[2].getDescription());

        // Verificar que restTemplate fue llamado una vez
        verify(restTemplate, times(1)).getForObject(anyString(), eq(List.class));
    }


}