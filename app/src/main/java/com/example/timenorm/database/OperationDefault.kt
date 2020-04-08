package com.example.timenorm.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = TimeTrackContract.OperationDefaultEntry.TABLE_NAME)
data class OperationDefault(
    @ColumnInfo(name = TimeTrackContract.OperationDefaultEntry.OPERATION_NAME)
    val operationName: String
) {
    @PrimaryKey(autoGenerate = true)
    var rowId: Long = 0
}

