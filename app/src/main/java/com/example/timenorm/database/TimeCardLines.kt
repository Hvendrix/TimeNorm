package com.example.timenorm.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = TimeTrackContract.TimeCardLinesEntry.TABLE_NAME)
data class TimeCardLines(
    @ColumnInfo(name = TimeTrackContract.TimeCardLinesEntry.OPERATION_NAME)
    val operationName: String,
    @ColumnInfo(name = TimeTrackContract.TimeCardLinesEntry.DELTA_TIME)
    val deltaTime: String,
    @ColumnInfo(name = TimeTrackContract.TimeCardLinesEntry.CARD_ID)
    val cardId: Long,
    @ColumnInfo(name = TimeTrackContract.TimeCardLinesEntry.DEF_OPER_ID)
    val defOperId: Long,
    @ColumnInfo(name = TimeTrackContract.TimeCardLinesEntry.COMMENT)
    val comment: String,
    @ColumnInfo(name = TimeTrackContract.TimeCardLinesEntry.START_TIME)
    val startTime: String,
    @ColumnInfo(name = TimeTrackContract.TimeCardLinesEntry.STOP_TIME)
    val stopTime: String,
    @ColumnInfo(name = TimeTrackContract.TimeCardLinesEntry.COUNT_PRODUCT)
    val countProduct: Int
) {
    @PrimaryKey(autoGenerate = true)
    var rowId: Long = 0
}