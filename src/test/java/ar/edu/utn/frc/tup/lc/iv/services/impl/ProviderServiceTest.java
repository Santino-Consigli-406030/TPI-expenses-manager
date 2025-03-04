package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.client.ProviderRestClient;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.ProviderDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProviderServiceTest {

    @Mock
    private ProviderRestClient providerRestClient;

    @InjectMocks
    private ProviderService providerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProviders_SuccessfulResponse() {
        ProviderDTO[] mockProviders = {
                new ProviderDTO(1, "Provider A"),
                new ProviderDTO(2, "Provider B")
        };
        when(providerRestClient.getAllProviders())
                .thenReturn(new ResponseEntity<>(mockProviders, HttpStatus.OK));
        List<ProviderDTO> providers = providerService.getProviders();
        assertNotNull(providers);
        assertEquals(2, providers.size());
        assertEquals(1, providers.get(0).getId());
        assertEquals("Provider A", providers.get(0).getDescription());
        assertEquals(2, providers.get(1).getId());
        assertEquals("Provider B", providers.get(1).getDescription());
        verify(providerRestClient, times(1)).getAllProviders();
    }

    @Test
    void testGetProviders_EmptyResponse() {
        when(providerRestClient.getAllProviders())
                .thenReturn(new ResponseEntity<>(new ProviderDTO[0], HttpStatus.OK));
        List<ProviderDTO> providers = providerService.getProviders();
        assertNotNull(providers);
        assertTrue(providers.isEmpty());
        verify(providerRestClient, times(1)).getAllProviders();
    }

    @Test
    void testGetProviders_ErrorResponse() {
        when(providerRestClient.getAllProviders())
                .thenReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
        List<ProviderDTO> providers = providerService.getProviders();
        assertNotNull(providers);
        assertTrue(providers.isEmpty());
        verify(providerRestClient, times(1)).getAllProviders();
    }
}