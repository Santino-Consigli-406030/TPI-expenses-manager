package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.client.OwnerRestClient;
import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.owner.OwnerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OwnerServiceTest {
    private OwnerService ownerService;
    private OwnerRestClient ownerRestClient;

    @BeforeEach
    public void setUp() {
        // Crear los mocks
        ownerRestClient = mock(OwnerRestClient.class);

        // Inyectar el mock en el servicio
        ownerService = new OwnerService(ownerRestClient);
    }

    @Test
    public void testGetOwners_success() throws Exception {
        // Configurar el comportamiento esperado para el mock de OwnerRestClient
        OwnerDto ownerDto = new OwnerDto();  // Asume que esta clase tiene un constructor adecuado.
        ownerDto.setName("John Doe");
        ownerDto.setId(1);
        ResponseEntity<OwnerDto[]> responseEntity = new ResponseEntity<>(new OwnerDto[]{ownerDto}, HttpStatus.OK);

        // Mock de la llamada al Rest Client
        when(ownerRestClient.getOwnerPlot()).thenReturn(responseEntity);

        // Llamar al método y verificar el resultado
        List<OwnerDto> owners = ownerService.getOwners();
        assertNotNull(owners);
        assertEquals(1, owners.size());
        assertEquals("John Doe", owners.get(0).getName());  // Verificar que el nombre del propietario es correcto
    }

    @Test
    public void testGetOwners_serviceUnavailable() {
        // Configurar el comportamiento para simular un error
        ResponseEntity<OwnerDto[]> responseEntity = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);

        // Mock de la llamada al Rest Client
        when(ownerRestClient.getOwnerPlot()).thenReturn(responseEntity);

        // Ejecutar la prueba
        assertTrue(ownerService.getOwners().isEmpty());
    }

    @Test
    public void testGetOwners_emptyResponse() {
        // Configurar el comportamiento para una respuesta vacía
        ResponseEntity<OwnerDto[]> responseEntity = new ResponseEntity<>(new OwnerDto[0], HttpStatus.OK);

        // Mock de la llamada al Rest Client
        when(ownerRestClient.getOwnerPlot()).thenReturn(responseEntity);

        // Llamar al método y verificar el resultado
        List<OwnerDto> owners = ownerService.getOwners();
        assertTrue(owners.isEmpty());
    }

    @Test
    void testGetOwnerByUserId_found() {
        // Crear datos simulados
        OwnerDto owner1 = new OwnerDto(1, "John", "Doe", "12345678", null, 101);
        OwnerDto owner2 = new OwnerDto(2, "Jane", "Smith", "87654321", null, 102);

        // Configurar el mock del RestClient
        when(ownerRestClient.getOwnerPlot())
                .thenReturn(new ResponseEntity<>(new OwnerDto[]{owner1, owner2}, HttpStatus.OK));

        // Llamar al método
        OwnerDto result = ownerService.getOwnerByUserId(101);

        // Verificar que se encuentra el owner correcto
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getLastName());
        assertEquals(101, result.getUserId());
    }

    @Test
    void testGetOwnerByUserId_notFound() {
        // Crear datos simulados
        OwnerDto owner1 = new OwnerDto(1, "John", "Doe", "12345678", null, 101);
        OwnerDto owner2 = new OwnerDto(2, "Jane", "Smith", "87654321", null, 102);

        // Configurar el mock del RestClient
        when(ownerRestClient.getOwnerPlot())
                .thenReturn(new ResponseEntity<>(new OwnerDto[]{owner1, owner2}, HttpStatus.OK));

        // Llamar al método
        OwnerDto result = ownerService.getOwnerByUserId(103);

        // Verificar que no se encuentra ningún owner
        assertNull(result);
    }
}