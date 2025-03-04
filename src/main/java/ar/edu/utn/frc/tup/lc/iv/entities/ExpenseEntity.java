package ar.edu.utn.frc.tup.lc.iv.entities;

import ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Entity class for the expense.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "expenses")
public class ExpenseEntity extends AuditEntity {
    /**
     * The precision for the amount field in the expense entity.
     */
    private static final int AMOUNT_PRESISION = 11;

    /**
     * The maximum length for the invoice number field in the expense entity.
     */
    private static final int INVOICE_NUMBER_LENGTH = 50;
    /**
     * The unique identifier for the expense.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The description of the expense.
     */
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * The identifier of the provider associated with the expense.
     */
    @Column(name = "provider_id", nullable = false)
    private Integer providerId;

    /**
     * The date when the expense was made.
     */
    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    /**
     * The unique identifier of the file associated with the expense.
     */
    @Column(name = "file_id", nullable = false)
    private UUID fileId;

    /**
     * The invoice number for the expense.
     */
    @Column(name = "invoice_number", length = INVOICE_NUMBER_LENGTH)
    private String invoiceNumber;

    /**
     * The type of the expense.
     */
    @Column(name = "expense_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;

    /**
     * The category of the expense.
     */
    @ManyToOne
    @JoinColumn(name = "expense_category_id", nullable = false)
    private ExpenseCategoryEntity category;

    /**
     * The amount of the expense.
     */
    @Column(name = "amount", nullable = false, precision = AMOUNT_PRESISION, scale = 2)
    private BigDecimal amount;

    /**
     * The number of installments for the expense.
     */
    @Column(name = "installments")
    private Integer installments;

    /**
     * Indicates if the expense is a note credit.
     */
    @Column(name = "note_credit", nullable = false)
    private Boolean noteCredit;

    /**
     * The list of distributions associated with the expense.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
    private List<ExpenseDistributionEntity> distributions;

    /**
     * The list of installments associated with the expense.
     */
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
    private List<ExpenseInstallmentEntity> installmentsList;
}
