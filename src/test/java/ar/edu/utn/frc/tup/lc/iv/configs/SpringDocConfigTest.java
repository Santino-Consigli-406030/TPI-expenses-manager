package ar.edu.utn.frc.tup.lc.iv.configs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Tests for the SpringDocConfig class.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringDocConfigTest {

   /**
 * The TestRestTemplate used to make HTTP requests in tests.
 */
@Autowired
private TestRestTemplate restTemplate;

    @Test
    void getDocumentation() throws IOException {
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("/docs", String.class);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertNotNull(responseEntity.getBody());

        Path specs = Paths.get("docs/api_doc");
        Files.createDirectories(specs);
        Files.writeString(specs.resolve("swagger.json"), responseEntity.getBody());
    }
}
