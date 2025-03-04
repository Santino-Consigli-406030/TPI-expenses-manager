package ar.edu.utn.frc.tup.lc.iv.dtos.owner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//NO TOCAR, YA DEFINIDO CONTRATO CON OWNERAPI

/**
 * Data transfer object for plots.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlotDto {
    /**
     * The unique identifier of the plot.
     */
    @JsonProperty("plot_id")
    private Integer id;

    /**
     * The size of the field in the plot.
     */
    @JsonProperty("field_size")
    private Integer fieldSize;
}
