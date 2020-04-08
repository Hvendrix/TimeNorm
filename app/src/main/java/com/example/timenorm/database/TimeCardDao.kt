package com.example.timenorm.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface TimeCardDao {
    @Query(
        "SELECT CASE WHEN tbl_name = '${TimeTrackContract.TimeCardEntry.TABLE_NAME}'" +
                "THEN 1 ELSE 0 END FROM sqlite_master WHERE tbl_name = '${TimeTrackContract.TimeCardEntry.TABLE_NAME}'" +
                "AND type = 'table'"
    )
    fun tableExist(): Int


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTimeCard(timeCard: TimeCard)


    @Query("SELECT * FROM ${TimeTrackContract.TimeCardEntry.TABLE_NAME} WHERE rowId = :id")
    fun checkTimeCardExist(id: Long): Int

    @Query("SELECT COUNT (*) FROM ${TimeTrackContract.TimeCardEntry.TABLE_NAME}")
    fun numbersOfRows(): Long

    @Query("SELECT * FROM ${TimeTrackContract.TimeCardEntry.TABLE_NAME} WHERE rowId = :id")
    fun getOneTimeCard(id: Long): TimeCard

    @Query("SELECT * FROM ${TimeTrackContract.TimeCardEntry.TABLE_NAME} ORDER BY rowId DESC LIMIT 1")
    fun getMaxCard(): TimeCard
}