package ar.edu.utn.frc.tup.lc.iv.client;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.fileManager.UuidResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * Client for interacting with the file manager service.
 */
@Service
public class FileManagerRestClient {
    /**
     * RestTemplate for making REST API calls.
     */
    private final RestTemplate restTemplate;

    /**
     * Root URL for the file manager API.
     */
    @Value("${app.api-filemanager}")
    private String rootUrl;

    /**
     * Constructs a FileManagerRestClient with the specified RestTemplate.
     *
     * @param restTemplateParam the RestTemplate to use for making REST API calls
     */
    public FileManagerRestClient(RestTemplate restTemplateParam) {
        this.restTemplate = restTemplateParam;
    }

    /**
     * Uploads a file to the file manager service.
     *
     * @param file       the file to be uploaded
     * @param hashMd5    the MD5 hash of the file (optional)
     * @param hashSha256 the SHA-256 hash of the file (optional)
     * @return a ResponseEntity containing a UuidResponseDto with
     * @throws CustomException if an unexpected error occurs during file upload
     */
    public ResponseEntity<UuidResponseDto> uploadFile(MultipartFile file, String hashMd5, String hashSha256) {
        String url = rootUrl + "/fileManager/savefile";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Body with file and optional hashes
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource()); // Convert MultipartFile to Resource
        if (hashMd5 != null) {
            body.add("hashMd5", hashMd5);
        }
        if (hashSha256 != null) {
            body.add("hashSha256", hashSha256);
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // Send POST request
            ResponseEntity<UuidResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    UuidResponseDto.class
            );
            return response;
        } catch (HttpClientErrorException e) {
            handleClientError(e);
            throw new CustomException("Unexpected error during file upload", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
    /**
     * Handles client-side errors and throws appropriate CustomExceptions
     * based on the status code and message from the server response.
     *
     * @param e the HttpClientErrorException thrown by the RestTemplate
     * @throws CustomException if a specific client-side error occurs
     */
    private void handleClientError(HttpClientErrorException e) {
        System.out.println(e.getResponseBodyAsString());
        HttpStatus statusCode = (HttpStatus) e.getStatusCode();
        String responseBody = e.getResponseBodyAsString();
        switch (statusCode) {
            case NOT_FOUND:
                throw new CustomException("File not found: " + responseBody, HttpStatus.NOT_FOUND, e);
            case CONFLICT:
                throw new CustomException("File integrity conflict: " + responseBody, HttpStatus.CONFLICT, e);
            case BAD_REQUEST:
                throw new CustomException("Invalid request: " + responseBody, HttpStatus.BAD_REQUEST, e);
            default:
                throw new CustomException("An unexpected error occurred: " + responseBody, HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}
