package ar.edu.utn.frc.tup.lc.iv.dtos.fileManager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data transfer object for file responses.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileResponseDto {

    /**
     * Unique identifier (UUID) for the file.
     */
    private UUID uuid;

    /**
     * The sha256 hash of the file.
     */
    private String sha256;

    /**
     * The name of the file.
     */
    private String fileName;

    /**
     * The MIME type of the file.
     */
    private String mimeType;

    /**
     * The file extension (e.g., txt, pdf, jpg).
     */
    private String extension;

    /**
     * The byte array representing the file's content.
     */
    private byte[] bytes;
}
