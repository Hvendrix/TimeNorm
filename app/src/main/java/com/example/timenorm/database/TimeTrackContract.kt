package com.example.timenorm.database

import android.os.Environment

object TimeTrackContract {
    const val DATABASE_NAME = "timeTrackDb"
    //const val DATABASE_NAME = getExternalFile
    //const val DATABASE_NAME = "/sdcard/Android/data/com.example.timenorm/baza.db"
    //const val DATABASE_NAME = "/storage/emulated/0/Android/data/timeTrackDb.db"
    const val DATABASE_VERSION = 1

    object OperationDefaultEntry {
        const val TABLE_NAME = "operationDefault"
        const val OPERATION_NAME = "operations"
    }

    object OperationDefault2Entry {
        const val TABLE_NAME = "operationDefault2"
        const val OPERATION_NAME = "operations"
    }

    object TimeCardEntry {
        const val TABLE_NAME = "timeCard"
        const val BUSINESS_UNIT_NAME = "businessUnit"
        const val DATA_FIELD = "dataField"
    }

    object TimeCardLinesEntry {
        const val TABLE_NAME = "timeCardLines"
        const val OPERATION_NAME = "operations"
        const val DELTA_TIME = "deltaTime"
        const val CARD_ID = "cardId"
        const val DEF_OPER_ID = "defOperId"
        const val COMMENT = "comment"
        const val START_TIME = "startTime"
        const val STOP_TIME = "stopTime"
        const val COUNT_PRODUCT = "countProduct"
    }

    object TimeCardListEntry {
        const val TABLE_NAME = "timeCardList"

    }
}