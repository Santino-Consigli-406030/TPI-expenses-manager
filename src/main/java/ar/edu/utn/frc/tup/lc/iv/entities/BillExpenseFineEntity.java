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
 * Entity class for the bill expense fine.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bills_expense_fines")
public class BillExpenseFineEntity extends AuditEntity {
    /**
     * The precision for the amount field in the bill expense fine.
     */
    private static final int AMOUNT_PRESISION = 11;
    /**
     * The unique identifier for the bill expense fine.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The description of the bill expense fine.
     */
    @Column(name = "description")
    private String description;

    /**
     * The owner associated with the bill expense fine.
     */
    @ManyToOne
    @JoinColumn(name = "bill_expense_owner_id", nullable = false)
    private BillExpenseOwnerEntity billExpenseOwner;

    /**
     * The unique identifier for the fine.
     */
    @Column(name = "fine_id", nullable = false)
    private Integer fineId;

    /**
     * The unique identifier for the plot.
     */
    @Column(name = "plot_id", nullable = false)
    private Integer plotId;

    /**
     * The amount of the bill expense fine.
     */
    @Column(name = "amount", nullable = false, precision = AMOUNT_PRESISION, scale = 2)
    private BigDecimal amount;
}
