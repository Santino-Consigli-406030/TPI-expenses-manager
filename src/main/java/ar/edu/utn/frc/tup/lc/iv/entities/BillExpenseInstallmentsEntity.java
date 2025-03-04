package ar.edu.utn.frc.tup.lc.iv.entities;

import ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entity class for the bill expense installment.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bills_expense_installments")
public class BillExpenseInstallmentsEntity extends AuditEntity {
    /**
     * Represents an amount of a bill expense.
     */
    private static final int AMOUNT_PRESISION = 11;

    /**
     * The unique identifier for the bill expense installment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The description of the bill expense installment.
     */
    @Column(name = "description")
    private String description;

    /**
     * The owner associated with the bill expense installment.
     */
    @ManyToOne
    @JoinColumn(name = "bill_expense_owner_id", nullable = false)
    private BillExpenseOwnerEntity billExpenseOwner;

    /**
     * The type of expense for the bill expense installment.
     */
    @Transient
    private ExpenseType expenseType;

    /**
     * The expense installment associated with this bill expense installment.
     */
    @ManyToOne
    @JoinColumn(name = "expense_installment_id", nullable = false)
    private ExpenseInstallmentEntity expenseInstallment;

    /**
     * The amount of the bill expense installment.
     */
    @Column(name = "amount", nullable = false, precision = AMOUNT_PRESISION, scale = 2)
    private BigDecimal amount;

}
