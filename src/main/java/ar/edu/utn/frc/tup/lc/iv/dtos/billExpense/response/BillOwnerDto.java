package ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.response;

import ar.edu.utn.frc.tup.lc.iv.dtos.sanction.FineDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data transfer object for bill owners.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillOwnerDto {
    /**
     * The unique identifier for the owner.
     */
    @JsonProperty("owner_id")
    private Integer id;

    /**
     * The size of the field owned by the owner.
     */
    @JsonProperty("field_size")
    private Integer fieldSize;

    /**
     * The list of fines associated with the owner.
     */
    @JsonProperty("fines")
    private List<FineDto> fines;

    /**
     * The list of common expenses associated with the owner.
     */
    @JsonProperty("expenses_common")
    private List<ItemDto> expensesCommon;

    /**
     * The list of extraordinary expenses associated with the owner.
     */
    @JsonProperty("expenses_extraordinary")
    private List<ItemDto> expensesExtraordinary;

    /**
     * The list of individual expenses associated with the owner.
     */
    @JsonProperty("expenses_individual")
    private List<ItemDto> expensesIndividual;

    /**
     * The list of credit notes associated with the owner.
     */
    @JsonProperty("notes_credit")
    private List<ItemDto> notesOfCredit;
}
