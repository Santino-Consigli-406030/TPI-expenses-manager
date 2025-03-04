package ar.edu.utn.frc.tup.lc.iv.client;

import ar.edu.utn.frc.tup.lc.iv.dtos.owner.OwnerDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.owner.PlotDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
public class OwnerRestClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OwnerRestClient ownerRestClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getOwnerPlot_ShouldReturnMappedOwnerDtos() {
        // Mock API response
        List<Map<String, Object>> mockResponse = List.of(
                Map.of(
                        "owner", Map.of(
                                "id", 1,
                                "name", "John",
                                "lastname", "Doe",
                                "dni", "12345678",
                                "dateBirth", "1990-01-01",
                                "businessName", "Business Name",
                                "active", true,
                                "ownerType", "INDIVIDUAL",
                                "taxStatus", "REGISTERED",
                                "files", List.of(
                                        Map.of("name", "document.pdf", "uuid", "123e4567-e89b-12d3-a456-426614174000")
                                )
                        ),
                        "plot", List.of(
                                Map.of(
                                        "id", 101,
                                        "total_area_in_m2", Double.valueOf(500),
                                        "built_area_in_m2", Double.valueOf(500),
                                        "plot_state", "AVAILABLE",
                                        "plot_type", "RESIDENTIAL",
                                        "files", List.of(
                                                Map.of("name", "plot_plan.pdf", "uuid", "123e4567-e89b-12d3-a456-426614174001")
                                        )
                                )
                        ),
                        "user", Map.of(
                                "id", 1001,
                                "name", "Admin",
                                "lastname", "User",
                                "username", "admin",
                                "email", "admin@example.com",
                                "active", true,
                                "avatar_url", "https://example.com/avatar.png",
                                "datebirth", "1980-01-01",
                                "create_date", "2023-01-01",
                                "roles", List.of("ADMIN")
                        )
                )
        );

        // Configure mock behavior
        when(restTemplate.getForObject(anyString(), any()))
                .thenReturn(mockResponse);

        // Call the method under test
        ResponseEntity<OwnerDto[]> response = ownerRestClient.getOwnerPlot();

        // Assertions
        assertEquals(1, response.getBody().length);
        OwnerDto ownerDto = response.getBody()[0];
        assertEquals(1, ownerDto.getId());
        assertEquals("John", ownerDto.getName());
        assertEquals("Doe", ownerDto.getLastName());
        assertEquals("12345678", ownerDto.getDni());
        assertEquals(1, ownerDto.getPlots().size());

        PlotDto plotDto = ownerDto.getPlots().get(0);
        assertEquals(101, plotDto.getId());
        assertEquals(500, plotDto.getFieldSize());
        assertEquals(1001, ownerDto.getUserId());
    }
}