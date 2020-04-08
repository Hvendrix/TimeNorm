package com.example.timenorm.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface TimeCardLinesDao {
    @Query(
        "SELECT CASE WHEN tbl_name = '${TimeTrackContract.TimeCardLinesEntry.TABLE_NAME}'" +
                "THEN 1 ELSE 0 END FROM sqlite_master WHERE tbl_name = '${TimeTrackContract.TimeCardLinesEntry.TABLE_NAME}'" +
                "AND type = 'table'"
    )
    fun tableExist(): Int

//"select * from '${TimeTrackContract.TimeCardEntry.TABLE_NAME}'" +
//    "join '${TimeTrackContract.TimeCardLinesEntry.TABLE_NAME}'" +
//    "on '${TimeTrackContract.TimeCardEntry.TABLE_NAME}'.rowId = '${TimeTrackContract.TimeCardLinesEntry.TABLE_NAME}'.carId"


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTimeLineCard(timeCardLines: TimeCardLines)


    @Query("SELECT * FROM ${TimeTrackContract.TimeCardLinesEntry.TABLE_NAME} WHERE rowId = :id")
    fun checkTimeCardLineExist(id: Long): Int

    @Query("SELECT COUNT (*) FROM ${TimeTrackContract.TimeCardLinesEntry.TABLE_NAME}")
    fun numbersOfRows(): Long

    @Query("SELECT * FROM ${TimeTrackContract.TimeCardLinesEntry.TABLE_NAME} WHERE rowId = :id")
    fun getOneTimeCardLines(id: Long): TimeCardLines

    @Query("SELECT * FROM ${TimeTrackContract.TimeCardLinesEntry.TABLE_NAME}")
    fun getAllTimeCardLines(): List<TimeCardLines>

    @Query("UPDATE ${TimeTrackContract.TimeCardLinesEntry.TABLE_NAME} SET ${TimeTrackContract.TimeCardLinesEntry.OPERATION_NAME} = :operName, ${TimeTrackContract.TimeCardLinesEntry.COMMENT} = :comment, ${TimeTrackContract.TimeCardLinesEntry.COUNT_PRODUCT} = :count WHERE rowId = :id")
    fun updateOneTimeCardLines(id: Long, operName: String, comment: String, count: Int)

}
