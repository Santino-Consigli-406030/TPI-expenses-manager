package ar.edu.utn.frc.tup.lc.iv.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Entity class for the bill record.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bills_record")
public class BillRecordEntity extends AuditEntity {
    /**
     * The unique identifier for the bill record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The start date of the bill record.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate start;

    /**
     * The end date of the bill record.
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate end;

    /**
     * The list of bill expense owners associated with the bill record.
     */
    @OneToMany(mappedBy = "billRecord", cascade = CascadeType.ALL)
    private List<BillExpenseOwnerEntity> billExpenseOwner;
}
