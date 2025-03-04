package ar.edu.utn.frc.tup.lc.iv.client;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.fileManager.FileResponseDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.fileManager.UuidResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;





public class FileManagerRestClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private FileManagerRestClient fileManagerRestClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fileManagerRestClient = new FileManagerRestClient(restTemplate);
    }

    @Test
    public void testUploadFile() {
        UuidResponseDto uuidResponseDto = new UuidResponseDto(UUID.randomUUID());
        ResponseEntity<UuidResponseDto> responseEntity = new ResponseEntity<>(uuidResponseDto, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(UuidResponseDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<UuidResponseDto> response = fileManagerRestClient.uploadFile(file, "md5hash", "sha256hash");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(uuidResponseDto, response.getBody());
    }

    @Test
    public void testUploadFileThrowsCustomException() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(UuidResponseDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(CustomException.class, () -> {
            fileManagerRestClient.uploadFile(file, "md5hash", "sha256hash");
        });
    }
}