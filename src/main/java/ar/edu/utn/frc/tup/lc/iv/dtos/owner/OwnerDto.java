package ar.edu.utn.frc.tup.lc.iv.dtos.owner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//NO TOCAR, YA DEFINIDO CONTRATO CON OWNERAPI

/**
 * Data transfer object for owners.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerDto {
    /**
     * The unique identifier of the owner.
     */
    @JsonProperty("owner_id")
    private Integer id;

    /**
     * The name of the owner.
     */
    @JsonProperty("name")
    private String name;

    /**
     * The last name of the owner.
     */
    @JsonProperty("last_name")
    private String lastName;

    /**
     * The DNI (Documento Nacional de Identidad) of the owner.
     */
    @JsonProperty("dni")
    private String dni;

    /**
     * The list of plots associated with the owner.
     */
    @JsonProperty("plots")
    private List<PlotDto> plots;
    /**
     * The unique identifier of the user.
     */
    @JsonProperty("user_id")
    private Integer userId;
}
