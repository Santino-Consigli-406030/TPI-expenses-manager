package ar.edu.utn.frc.tup.lc.iv.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * Entity class for the expense distribution.
 */
@Data
@EqualsAndHashCode(callSuper = false)

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "expense_distribution")
public class ExpenseDistributionEntity extends AuditEntity {

    /**
     * The precision for the proportion field in the expense distribution.
     */
    private static final int PROPORTION_PRESISION = 3;
    /**
     * The unique identifier for the expense distribution.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The identifier of the owner associated with the expense distribution.
     */
    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    /**
     * The expense entity associated with the expense distribution.
     */
    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private ExpenseEntity expense;

    /**
     * The proportion of the expense distribution.
     */
    @Column(name = "proportion", nullable = false, precision = PROPORTION_PRESISION, scale = 2)
    private BigDecimal proportion;

    /**
     * Indicates whether the expense distribution is enabled.
     */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

}
