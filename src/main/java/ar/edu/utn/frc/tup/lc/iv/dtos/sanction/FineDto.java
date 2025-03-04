package ar.edu.utn.frc.tup.lc.iv.dtos.sanction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data transfer object for fines.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FineDto {
    /**
     * The unique identifier for the fine.
     */
    @JsonProperty("fine_id")
    private Integer id;

    /**
     * The identifier for the plot associated with the fine.
     */
    @JsonProperty("plot_id")
    private Integer plotId;

    /**
     * A description of the fine.
     */
    @JsonProperty("description")
    private String description;

    /**
     * The amount of the fine.
     */
    @JsonProperty("amount")
    private BigDecimal amount;

}
