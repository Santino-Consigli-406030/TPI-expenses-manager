package ar.edu.utn.frc.tup.lc.iv.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.util.List;

/**
 * Entity class for the bill expense owner.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bills_expense_owners")
public class BillExpenseOwnerEntity extends AuditEntity {
    /**
     * Represents an owner of a bill expense.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The bill record associated with this bill expense owner.
     */
    @ManyToOne
    @JoinColumn(name = "bill_record_id", nullable = false)
    private BillRecordEntity billRecord;

    /**
     * The ID of the owner.
     */
    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    /**
     * The size of the field.
     */
    @Column(name = "field_size")
    private Integer fieldSize;

    /**
     * The list of bill expense fines associated with this bill expense owner.
     */
    @OneToMany(mappedBy = "billExpenseOwner", cascade = CascadeType.ALL)
    private List<BillExpenseFineEntity> billExpenseFines;

    /**
     * The list of bill expense installments associated with this bill expense owner.
     */
    @OneToMany(mappedBy = "billExpenseOwner", cascade = CascadeType.ALL)
    private List<BillExpenseInstallmentsEntity> billExpenseInstallments;
}
