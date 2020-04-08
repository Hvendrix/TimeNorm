package com.example.timenorm.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface OperationDefaultDao {
    @Query(
        "SELECT CASE WHEN tbl_name = '${TimeTrackContract.OperationDefaultEntry.TABLE_NAME}'" +
                "THEN 1 ELSE 0 END FROM sqlite_master WHERE tbl_name = '${TimeTrackContract.OperationDefaultEntry.TABLE_NAME}'" +
                "AND type = 'table'"
    )
    fun tableExist(): Int


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOperationDefault(operationDefault: OperationDefault)


    @Query("SELECT * FROM ${TimeTrackContract.OperationDefaultEntry.TABLE_NAME} WHERE rowId = :id")
    fun checkOperationDefaultExist(id: Long): Int

    @Query("SELECT COUNT (*) FROM ${TimeTrackContract.OperationDefaultEntry.TABLE_NAME}")
    fun numbersOfRows(): Long

    @Query("SELECT * FROM ${TimeTrackContract.OperationDefaultEntry.TABLE_NAME} WHERE rowId = :id")
    fun getOneOperationDefault(id: Long): OperationDefault

    @Query("SELECT * FROM ${TimeTrackContract.OperationDefaultEntry.TABLE_NAME}")
    fun getAllOperationDefault(): List<OperationDefault>

//    @Query("DELETE FROM ${TimeTrackContract.OperationDefaultEntry.TABLE_NAME}")
//    fun deleteAllOperationDefault()

}