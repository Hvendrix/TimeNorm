package com.example.timenorm.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = TimeTrackContract.TimeCardEntry.TABLE_NAME)
data class TimeCard(
    @ColumnInfo(name = TimeTrackContract.TimeCardEntry.BUSINESS_UNIT_NAME)
    val businessUnitName: String,
    @ColumnInfo(name = TimeTrackContract.TimeCardEntry.DATA_FIELD)
    val dataField: String
) {
    @PrimaryKey(autoGenerate = true)
    var rowId: Long = 0
}