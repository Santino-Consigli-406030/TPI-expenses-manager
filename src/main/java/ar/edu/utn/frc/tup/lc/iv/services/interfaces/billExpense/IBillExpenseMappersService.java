package ar.edu.utn.frc.tup.lc.iv.services.interfaces.billExpense;

import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.response.BillExpenseDto;
import ar.edu.utn.frc.tup.lc.iv.entities.BillRecordEntity;
import ar.edu.utn.frc.tup.lc.iv.models.BillRecordModel;
/**
 * Service class for managing mapper necessary for BillRecord Service.
 */
public interface IBillExpenseMappersService {
    /**
     * Converts a BillRecordModel to a BillRecordEntity.
     *
     * @param billRecordModel the model to convert
     * @return the converted BillRecordEntity
     */
    BillRecordEntity billRecordModelToEntity(BillRecordModel billRecordModel);

    /**
     * Converts a BillRecordModel to a BillExpenseDto.
     *
     * @param billRecordModel the model to convert
     * @return the converted BillExpenseDto
     */
    BillExpenseDto billRecordModelToDto(BillRecordModel billRecordModel);

    /**
     * Converts a BillRecordEntity to a BillRecordModel.
     *
     * @param entity the entity to convert
     * @return the converted BillRecordModel
     */
    BillRecordModel entityToBillRecordModel(BillRecordEntity entity);
}
