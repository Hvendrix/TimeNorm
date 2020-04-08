package com.example.timenorm.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface TimeCardListDao {
    @Query(
        "SELECT * FROM '${TimeTrackContract.TimeCardEntry.TABLE_NAME}'" +
                "join '${TimeTrackContract.TimeCardLinesEntry.TABLE_NAME}'" +
                "on '${TimeTrackContract.TimeCardEntry.TABLE_NAME}'.rowId = '${TimeTrackContract.TimeCardLinesEntry.TABLE_NAME}'.cardId"
    )
    fun getAll(): List<TimeCardList>
}
