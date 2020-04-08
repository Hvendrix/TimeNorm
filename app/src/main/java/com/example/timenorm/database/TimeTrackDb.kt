package com.example.timenorm.database

import android.text.format.Time
import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [(OperationDefault::class), (TimeCard::class), (TimeCardLines::class), (TimeCardList::class)],
    version = TimeTrackContract.DATABASE_VERSION,
    exportSchema = false
)
abstract class TimeTrackDb : RoomDatabase() {

    abstract fun operationDefaultDao(): OperationDefaultDao

    abstract fun timeCardDao(): TimeCardDao

    abstract fun timeCardLinesDao(): TimeCardLinesDao

    abstract fun timeCardListDao(): TimeCardListDao
}