package ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data transfer object for items.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    /**
     * The unique identifier of the item.
     */
    @JsonProperty("id")
    private Integer id;

    /**
     * The description of the item.
     */
    @JsonProperty("description")
    private String description;

    /**
     * The amount associated with the item.
     */
    @JsonProperty("amount")
    private BigDecimal amount;
}
