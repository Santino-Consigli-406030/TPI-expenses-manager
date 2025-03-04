package ar.edu.utn.frc.tup.lc.iv.dtos.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data transfer object for categories.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DtoCategory {
    /**
     * The unique identifier for the category.
     */
    private Integer id;

    /**
     * The description of the category.
     */
    private String description;

    /**
     * The last updated date and time of the category.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime lastUpdatedDatetime;

    /**
     * The state of the category.
     */
    private String state;
}
