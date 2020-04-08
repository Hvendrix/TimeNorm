package com.example.timenorm

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.room.Room
import com.example.timenorm.database.*
import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.edit_text_layout.*
import timber.log.Timber
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {


    //переменные из CardChoice
    companion object {
        const val TOTAL_COUNT = "total_count"
        const val TOTAL_ARRAY = "total_array"
        const val TOTAL_AUTO = "total_auto"
    }

    //создание базы данных
    var timeTrackDb: TimeTrackDb? = null

    var cardId = 0L
    var fileNum = 0L

    //строка всей базы данных вывода в файл
    var AllTimeCardLines = ""


    //секундомер
    var deltaTime = 0L
    var strDeltaTime = ""
    var operationInField = ""
    var commentInField = ""
    var arrComment = arrayOf<String>("", "", "", "", "", "", "", "", "", "", "", "")

    //данные из CardChoiceActivity
    var operationListNum = 0L
    var operationArr = longArrayOf()
    lateinit var operationAuto: Array<String>

    //Зафиксированные операции
    var operationGridList = ArrayList<String>()
    var operationGridNum = ArrayList<Long>()
    //Адаптер Grid для операций
    var adapter: ArrayAdapter<String>? = null


    //объявление массива кнопок и полей
    var arrBtnStart: Array<AppCompatButton> = arrayOf()
    var arrBtnStop: Array<AppCompatButton> = arrayOf()
    var arrBtnReset: Array<AppCompatButton> = arrayOf()
    var arrBtnComment: Array<AppCompatButton> = arrayOf()
    var arrChron: Array<Chronometer> = arrayOf()
    var arrTxtDeltaTime: Array<TextView> = arrayOf()
    var arrTxtOper: Array<TextView> = arrayOf()
    var arrCountProduct: Array<TextView> = arrayOf()

    //данные для хронометров
    var arrStartTime = arrayOf<String>("", "", "", "", "", "", "", "", "", "", "", "")
    var arrTimeWhenStopped = longArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    var arrResetCheck = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

    var check = 0

    private val PICKFILE_RESULT_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Stetho.initializeWithDefaults(this)

        Timber.plant(Timber.DebugTree())

        //создание базы данных
        timeTrackDb = Room.databaseBuilder(
            this,
            TimeTrackDb::class.java,
            TimeTrackContract.DATABASE_NAME
        )
            .build()


        check = 0

        Stetho.initializeWithDefaults(this)
        AndroidThreeTen.init(this)


        checkDatabaseTableExist()


        //заполнение карточки
        getIdFromCardChoice()
        //createTimeCard(strBu, "segodnya")

        //вывод карточки в поля
        readTimeCardInField()


        //массив полей операций
        arrTxtOper = arrayOf(
            txtOper1,
            txtOper2,
            txtOperEdit1,
            txtOperEdit2,
            txtOperEdit3,
            txtOperEdit4,
            txtOperEdit5,
            txtOperEdit6,
            txtOperEdit7,
            txtOperEdit8,
            txtOperEdit9,
            txtOperEdit10
        )
        arrBtnStart = arrayOf(
            btnStart1,
            btnStart2,
            btnStartEdit1,
            btnStartEdit2,
            btnStartEdit3,
            btnStartEdit4,
            btnStartEdit5,
            btnStartEdit6,
            btnStartEdit7,
            btnStartEdit8,
            btnStartEdit9,
            btnStartEdit10
        )
        arrBtnStop = arrayOf(
            btnStop1,
            btnStop2,
            btnStopEdit1,
            btnStopEdit2,
            btnStopEdit3,
            btnStopEdit4,
            btnStopEdit5,
            btnStopEdit6,
            btnStopEdit7,
            btnStopEdit8,
            btnStopEdit9,
            btnStopEdit10
        )
        arrBtnReset = arrayOf(
            btnReset1,
            btnReset2,
            btnResetEdit1,
            btnResetEdit2,
            btnResetEdit3,
            btnResetEdit4,
            btnResetEdit5,
            btnResetEdit6,
            btnResetEdit7,
            btnResetEdit8,
            btnResetEdit9,
            btnResetEdit10
        )

        arrChron = arrayOf(
            chron1,
            chron2,
            chronEdit1,
            chronEdit2,
            chronEdit3,
            chronEdit4,
            chronEdit5,
            chronEdit6,
            chronEdit7,
            chronEdit8,
            chronEdit9,
            chronEdit10
        )
        arrCountProduct = arrayOf(
            countEditTxt1,
            countEditTxt2,
            countEdit1,
            countEdit2,
            countEdit3,
            countEdit4,
            countEdit5,
            countEdit6,
            countEdit7,
            countEdit8,
            countEdit9,
            countEdit10
        )
        arrTxtDeltaTime = arrayOf(
            txtDeltaTime1,
            txtDeltaTime2,
            txtDeltaTimeEdit1,
            txtDeltaTimeEdit2,
            txtDeltaTimeEdit3,
            txtDeltaTimeEdit4,
            txtDeltaTimeEdit5,
            txtDeltaTimeEdit6,
            txtDeltaTimeEdit7,
            txtDeltaTimeEdit8,
            txtDeltaTimeEdit9,
            txtDeltaTimeEdit10
        )
        arrBtnComment = arrayOf(
            btnCommTxt1,
            btnCommTxt2,
            btnComm1,
            btnComm2,
            btnComm3,
            btnComm4,
            btnComm5,
            btnComm6,
            btnComm7,
            btnComm8,
            btnComm9,
            btnComm10
        )


        table_data.visibility = View.INVISIBLE
        txtComment.visibility = View.GONE
        ClearButton.visibility = View.GONE

        //Grid Заполнение операциями
        adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            operationGridList
        )
        gridView.setAdapter(adapter)
        gridView.isExpanded = true


        //Кнопки
        gridView.setOnItemClickListener { adapterView, view, position, id ->

            Toast.makeText(
                this@MainActivity,
                "Вы нажали " + operationGridList.get(position),
                Toast.LENGTH_LONG
            ).show()

            var rId = 1L
            rId = operationGridNum[id.toInt()]
            showToast(id.toString())
            editLineGrid(rId)


            var numOfPos = operationGridList.get(position)[0].toString()
            showToast(numOfPos)
            // var x = y.toInt() - 1
            // showToast(x.toString())

//            var x = y.toIntOrNull()
//            var z = x!!.toInt()
//            showToast(y.toString())
//            showToast(operationGridList.get(position)[0].toString())

            //operationGridList[x] = txtComment.text.toString()
        }

        //авто заполнение
        ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            operationAuto
        ).also { adapter ->
            txtOperEdit1.setAdapter(adapter)
            txtOperEdit2.setAdapter(adapter)
            txtOperEdit3.setAdapter(adapter)
            txtOperEdit4.setAdapter(adapter)
            txtOperEdit5.setAdapter(adapter)
            txtOperEdit6.setAdapter(adapter)
            txtOperEdit7.setAdapter(adapter)
            txtOperEdit8.setAdapter(adapter)
            txtOperEdit9.setAdapter(adapter)
            txtOperEdit10.setAdapter(adapter)

        }

        //вызов функций кнопок
        for (index in arrTxtOper.indices) {
            readOperationDefaultInField(arrTxtOper[index], operationArr[index])
            hideKeyboard(arrTxtOper[index])
        }

        for (index in arrTxtOper.indices) {
            time(index)
        }

        sendBtn.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, "$AllTimeCardLines")
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, "Поделиться"))

//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "file/*"
//            startActivityForResult(intent, PICKFILE_RESULT_CODE)
        }


//
//        button.setOnClickListener {
//            //showToast(operationGridList[0])
//
//
////            gridView.setOnTouchListener { v, event ->
////                event.action == MotionEvent.ACTION_MOVE
////            }
//
////            var showTime = ""
////
////            var startTimeMilli =SystemClock.elapsedRealtime()
////            var endTimeMilli = SystemClock.elapsedRealtime() +1000
////            var hourS = (startTimeMilli - endTimeMilli)/ 1000 / 60 / 60
////            var minS = (startTimeMilli- endTimeMilli) /1000/60
////            var secS = (startTimeMilli- endTimeMilli) /1000
////
////            showTime = ("$hourS : $minS : $secS ")
////
////            showToast(showTime)
////            var time = getInstance()
////            showToast(time.toString())
////
////            val sdf = SimpleDateFormat("dd.M.yyyy hh:mm:ss")
////            val currentDate = sdf.format(Date())
//
//
////            val currentDate = Date()
////            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
////            val timeText = timeFormat.format(currentDate)
////
////            showToast(timeText)
////
////            showToast(SystemClock.elapsedRealtime().toString())
//
////            var sdft = SimpleDateFormat("hh:mm:ss")
////            var date : LocalDate = LocalDate.now()
////            var text = date.format(DateTimeFormatter.ISO_DATE)
//            //var text = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
//
//
////            val sendIntent = Intent()
////            sendIntent.action = Intent.ACTION_SEND
////            sendIntent.putExtra(Intent.EXTRA_TEXT, "$AllTimeCardLines")
////            sendIntent.type = "text/plain"
////            startActivity(Intent.createChooser(sendIntent, "Поделиться"))
////
////            val intent = Intent(Intent.ACTION_GET_CONTENT)
////            intent.type = "file/*"
////            startActivityForResult(intent, PICKFILE_RESULT_CODE)
//
//
//
//
//        }


//        val sendIntent = Intent()
//        sendIntent.action = Intent.ACTION_SEND
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "$AllTimeCardLines")
//        sendIntent.type = "text/plain"
//        startActivity(.se
    }


    private fun editLineGrid(RowId: Long) {
        val dBuilder = AlertDialog.Builder(this)
        val dLayout = LinearLayout(this)
        val dTvName = TextView(this)
        val operationName = EditText(this)
        val operationComment = EditText(this)
        var countEdit = EditText(this)



        operationName.hint = "Введите название операции"
        operationName.setSingleLine()
        operationComment.hint = "Введите комментарий"
        operationComment.setSingleLine()
        countEdit.inputType = 2
        dTvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)



        Thread {
            if (timeTrackDb!!.timeCardLinesDao().checkTimeCardLineExist(RowId) <= 0) {

                this@MainActivity.runOnUiThread {
                    showToast("Нет операции с ID = $RowId")
                }
            }
            if (timeTrackDb!!.timeCardLinesDao().checkTimeCardLineExist(RowId) != 0) {
                val timeCardLines = timeTrackDb!!.timeCardLinesDao().getOneTimeCardLines(RowId)
                dTvName.text = "Изменение замера номер ${timeCardLines.rowId}"
                operationName.setText("${timeCardLines.operationName}")
                operationComment.setText("${timeCardLines.comment}")
                countEdit.setText("${timeCardLines.countProduct}")
            }
        }.start()

        dLayout.orientation = LinearLayout.VERTICAL
        dLayout.addView(dTvName)
        dLayout.addView(operationName)
        dLayout.addView(operationComment)
        dLayout.addView(countEdit)
        dLayout.setPadding(10, 10, 10, 10)

        dBuilder.setView(dLayout)

        dBuilder.setPositiveButton("Принять изменения") { dialogInterface: DialogInterface, i ->

            val oper = operationName.text.toString()
            var comm = operationComment.text.toString()
            var count = countEdit.text.toString()


            var intCount: Int = 0

            if (count != "") {
                intCount = count.toInt()
            }


            if (timeTrackDb == null) {
                showToast("Подключите(создайте) базу данных")
            }
            Thread {
                timeTrackDb!!.timeCardLinesDao().updateOneTimeCardLines(RowId, oper, comm, intCount)


                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        //вывод операций карточки
                        if (timeTrackDb == null) {
                            showToast("Подключите(создайте) базу данных")
                            return
                        }
                        //Простое данных добавление в поле и в файл
                        java.lang.Thread {
                            readAllTimeCardLineInField()
                            readAll()
                        }.start()
                    }
                }, 200)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        //новый способ вывода данных в файл
                        if (!(AllTimeCardLines == "")) {
                            var saveString = AllTimeCardLines
                            saveFile(saveString.toString())
                        }
                    }
                }, 800)

                this@MainActivity.runOnUiThread {

                }
            }.start()


        }

        dBuilder.setNegativeButton("Отменить") { dialogInterface: DialogInterface, i ->

            dialogInterface.dismiss()
        }

        dBuilder.create().show()


    }

    override fun onStart() {
        super.onStart()
        Timber.i("onStart")
    }

    //кнопки секундомеров
    private fun time(numOfKit: Int) {
        arrBtnStop[numOfKit].visibility = View.GONE
        arrBtnStart[numOfKit].setOnClickListener {
            arrChron[numOfKit].base = SystemClock.elapsedRealtime() + arrTimeWhenStopped[numOfKit]
            arrChron[numOfKit].start()
            if (arrTimeWhenStopped[numOfKit] == 0L) {
                val currentDate = Date()
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val timeText = timeFormat.format(currentDate)
                arrStartTime[numOfKit] = timeText
            }
            arrResetCheck[numOfKit] = 1

            showToast(arrStartTime[numOfKit])
            hideKeyboard(it)
            arrBtnStart[numOfKit].visibility = View.GONE
            arrBtnStop[numOfKit].visibility = View.VISIBLE
        }

        arrBtnStop[numOfKit].setOnClickListener {
            arrTimeWhenStopped[numOfKit] = arrChron[numOfKit].base - SystemClock.elapsedRealtime()
            arrChron[numOfKit].stop()
            arrBtnStart[numOfKit].visibility = View.VISIBLE
            arrBtnStop[numOfKit].visibility = View.GONE
        }
        arrBtnReset[numOfKit].setOnClickListener {
            if (arrResetCheck[numOfKit] == 1) {
                arrBtnStart[numOfKit].visibility = View.VISIBLE
                arrBtnStop[numOfKit].visibility = View.GONE
                arrChron[numOfKit].stop()
                var stopTime = arrTimeWhenStopped[numOfKit]
                if (arrTimeWhenStopped[numOfKit] == 0L) {
                    stopTime = arrChron[numOfKit].base - SystemClock.elapsedRealtime()
                }
                //var stopTime = arrChron[numOfKit].base - SystemClock.elapsedRealtime()
                arrChron[numOfKit].base = SystemClock.elapsedRealtime()
                arrTimeWhenStopped[numOfKit] = 0

                arrResetCheck[numOfKit] = 0

                deltaTime = (stopTime * (-1)) / 1000
                strDeltaTime = "$deltaTime"
                arrTxtDeltaTime[numOfKit].text = strDeltaTime

                var count = arrCountProduct[numOfKit].text.toString()

                var intCount: Int = 0

                if (count != "") {
                    intCount = count.toInt()
                }

                arrCountProduct[numOfKit].text = "0"


                //arrChron[numOfKit].base = 0L
                //arrChron[numOfKit].base = SystemClock.elapsedRealtime()

                val currentDate = Date()
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val timeEndText = timeFormat.format(currentDate)

                //showToast(timeFormat.format(strDeltaTime))

                showToast(arrStartTime[numOfKit])

                operationInField = arrTxtOper[numOfKit].text.toString()
                //commentInField = txtComment.text.toString()
                commentInField = arrComment[numOfKit]

                //showToast(arrComment[numOfKit])

                arrComment[numOfKit] = ""


                //сохранение операции и времени
                createTimeLineCard(
                    operationInField,
                    strDeltaTime,
                    cardId,
                    operationArr[numOfKit],
                    commentInField,
                    arrStartTime[numOfKit],
                    timeEndText,
                    intCount
                )

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        //вывод операций карточки
                        if (timeTrackDb == null) {
                            showToast("Подключите(создайте) базу данных")
                            return
                        }
                        //Простое данных добавление в поле и в файл
                        java.lang.Thread {
                            readAllTimeCardLineInField()
                            //readOperationDefaultInArray()
                            //readAllTimeCardLineInVal()
                            readAll()
                        }.start()
                    }
                }, 200)

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        //новый способ вывода данных в файл
                        if (!(AllTimeCardLines == "")) {
                            var saveString = AllTimeCardLines
                            saveFile(saveString.toString())
                        }
                    }
                }, 800)
            }
        }

        arrBtnComment[numOfKit].setOnClickListener {
            val dBuilder = AlertDialog.Builder(this)
            val dLayout = LinearLayout(this)
            val operationComment = EditText(this)


            operationComment.hint = "Введите название операции"
            operationComment.setSingleLine()

            operationComment.setText("${arrComment[numOfKit]}")

            dLayout.orientation = LinearLayout.VERTICAL
            dLayout.addView(operationComment)
            dLayout.setPadding(10, 10, 10, 10)
            dBuilder.setView(dLayout)

            dBuilder.setPositiveButton("Принять изменения") { dialogInterface: DialogInterface, i ->
                arrComment[numOfKit] = operationComment.text.toString()
            }

            dBuilder.setNegativeButton("Отменить") { dialogInterface: DialogInterface, i ->

                dialogInterface.dismiss()
            }

            dBuilder.create().show()
        }


    }


    //база данных
    //запись и считывание
    //работа со стандартными операциями
    private fun readOperationDefaultInField(textView: TextView, RowId: Long) {
        if (timeTrackDb == null) {
            showToast("Подключите(создайте) базу данных")
            return
        }

        Thread {
            if (timeTrackDb!!.operationDefaultDao().checkOperationDefaultExist(RowId) <= 0) {

                this@MainActivity.runOnUiThread {
                    showToast("Нет операции с ID = $RowId")
                }
            }
            val operationDefault = timeTrackDb!!.operationDefaultDao().getOneOperationDefault(RowId)

            this@MainActivity.runOnUiThread {
                textView.text = "${operationDefault.operationName}"
            }

        }.start()
    }

    private fun readOperationDefaultInArray() {
        showToast("ok")
        //val operationDefaultList: List<OperationDefault> = timeTrackDb!!.operationDefaultDao().getAllOperationDefault()
//
//       if (!operationDefaultList.isNotEmpty()) {
//           this@MainActivity.runOnUiThread {
//               showToast("Таблица пуста")
//           }
//       }
//       var index = 0L
//       var indexInt = 0
//       var listTest = ""
//       OperationDefaultList.forEach {
//
//           //index = it.rowId-1
//           //indexInt = index.toInt()
//           //operAutoCom[indexInt] = "${it.operationName}"
//           //listTest = "${it.operationName}"
//       }

        val operationDefaultList: List<OperationDefault> =
            timeTrackDb!!.operationDefaultDao().getAllOperationDefault()

//       if (!operationDefaultList.isNotEmpty()) {
//           this@MainActivity.runOnUiThread {
//               showToast("Таблица пуста")
//               table_data.text = ""
//           }
//       }
//       var listText = ""
//
//       operationDefaultList.forEach {
//           listText = "ID: ${it.rowId}\n" +
//                   "Операция: ${it.operationName}\n--\n" +
//                   listText
//       }
//       this@MainActivity.runOnUiThread {
//           table_data.text = listText
//       }

    }

    private fun checkDatabaseTableExist() {
        if (timeTrackDb == null) {
            showToast("Подключите(создайте) базу данных")
            return
        }
        Thread {
            //таблица стандартных операций
            if (timeTrackDb!!.operationDefaultDao().tableExist() == 1) {
                this@MainActivity.runOnUiThread {
                    //                    showToast(
//                        "${TimeTrackContract.DATABASE_NAME} -  база данных,\n" +
//                                "${TimeTrackContract.OperationDefaultEntry.TABLE_NAME}  - таблица подключена)"
//                    )
                }
            } else {
                this@MainActivity.runOnUiThread {
                    showToast(
                        "${TimeTrackContract.DATABASE_NAME} - база данных, \n" +
                                "${TimeTrackContract.OperationDefaultEntry.TABLE_NAME} таблица не существует"
                    )
                }
            }

            //таблица карточек
            if (timeTrackDb!!.timeCardDao().tableExist() == 1) {
                this@MainActivity.runOnUiThread {
                    //                    showToast(
//                        "${TimeTrackContract.DATABASE_NAME} -  база данных,\n" +
//                                "${TimeTrackContract.TimeCardEntry.TABLE_NAME}  - таблица подключена)"
//                    )
                }
            } else {
                this@MainActivity.runOnUiThread {
                    showToast(
                        "${TimeTrackContract.DATABASE_NAME} - база данных, \n" +
                                "${TimeTrackContract.TimeCardEntry.TABLE_NAME} таблица не существует"
                    )
                }
            }
            //операции и время карточки
            if (timeTrackDb!!.timeCardLinesDao().tableExist() == 1) {
                this@MainActivity.runOnUiThread {
                    //                    showToast(
//                        "${TimeTrackContract.DATABASE_NAME} -  база данных,\n" +
//                                "${TimeTrackContract.TimeCardLinesEntry.TABLE_NAME}  - таблица подключена)"
//                    )
                }
            } else {
                this@MainActivity.runOnUiThread {
                    showToast(
                        "${TimeTrackContract.DATABASE_NAME} - база данных, \n" +
                                "${TimeTrackContract.TimeCardLinesEntry.TABLE_NAME} таблица не существует"
                    )
                }
            }
        }.start()
    }

    //работа с карточками
    private fun readTimeCardInField() {
        if (timeTrackDb == null) {
            showToast("Подключите(создайте) базу данных")
            return
        }

        Thread {
            val timeCard: TimeCard = timeTrackDb!!.timeCardDao().getMaxCard()

            this@MainActivity.runOnUiThread {
                txtCardId.text = "Id: ${timeCard.rowId}"
                txtBU.text = "BU: ${timeCard.businessUnitName}"
                txtData.text = "Data: ${timeCard.dataField}"
                cardId = timeCard.rowId
            }

        }.start()
    }

    //работа с операциями каротчки
    private fun createTimeLineCard(
        strOper: String,
        strDeltaTime: String,
        cardId: Long,
        defOperId: Long,
        comment: String,
        startTime: String,
        stopTime: String,
        countProduct: Int
    ) {
        if (timeTrackDb == null) {
            showToast("Подключите(создайте) базу данных")
            return
        }
        Thread {
            timeTrackDb!!.timeCardLinesDao().insertTimeLineCard(
                TimeCardLines(
                    strOper,
                    strDeltaTime,
                    cardId,
                    defOperId,
                    comment,
                    startTime,
                    stopTime,
                    countProduct
                )
            )

            val numbersOfRows = timeTrackDb!!.timeCardLinesDao().numbersOfRows()

            this@MainActivity.runOnUiThread {
                //showToast("Число операций карточки(строк): $numbersOfRows")
            }
        }.start()
    }


    //заполнение Grid операциями
    private fun readAllTimeCardLineInField() {
        val TimeCardLinesList: List<TimeCardLines> =
            timeTrackDb!!.timeCardLinesDao().getAllTimeCardLines()

        if (!TimeCardLinesList.isNotEmpty()) {
            this@MainActivity.runOnUiThread {
                showToast("Таблица пуста")
            }
        }
        var listText = ""
        operationGridList.clear()
        operationGridNum.clear()

        TimeCardLinesList.forEach {
            operationGridList.add("${it.rowId}  ${it.operationName}     ${it.deltaTime}  ${it.startTime}    ${it.stopTime}  ${it.countProduct}")
            operationGridNum.add(it.rowId)
            listText = "ID: ${it.rowId}\n" +
                    "Операция: ${it.operationName}\n" +
                    "ID операции: ${it.defOperId}\n" +
                    "ID карточки: ${it.cardId}\n" +
                    "Время: ${it.deltaTime}\n" +
                    "Начало: ${it.startTime}\n" +
                    "Конец: ${it.stopTime}\n" +
                    "Комментарий: ${it.comment}\n--\n" +
                    listText
        }
        operationGridList.reverse()
        operationGridNum.reverse()
        this@MainActivity.runOnUiThread {
            //table_data.text = listText
            gridView.setAdapter(adapter)

        }


    }


    //асинхронный
    private class readAllTimeCardLineInFieldAsync(var context: MainActivity) :
        AsyncTask<Void, Void, List<TimeCardLines>>() {
        override fun doInBackground(vararg params: Void?): List<TimeCardLines> {
            return context.timeTrackDb!!.timeCardLinesDao().getAllTimeCardLines()
        }

        override fun onPostExecute(timeCardLinesList: List<TimeCardLines>?) {
            if (!timeCardLinesList!!.isNotEmpty()) {
                context.runOnUiThread {
                    context.showToast("Таблица пуста")
                    context.table_data.text = ""
                }
            }
            var listText = ""

            timeCardLinesList!!.forEach {
                listText = "ID: ${it.rowId}\n" +
                        "Операция: ${it.operationName}\n" +
                        "ID карточки: ${it.cardId}\n" +
                        "Время: ${it.deltaTime}\n" +
                        "Комментарий: ${it.comment}\n--\n" +
                        listText
            }
            context.runOnUiThread {
                context.table_data.text = listText
            }

        }

    }

    //Считывание только Lines
    private fun readAllTimeCardLineInVal() {
        val TimeCardLinesList: List<TimeCardLines> =
            timeTrackDb!!.timeCardLinesDao().getAllTimeCardLines()

        if (!TimeCardLinesList.isNotEmpty()) {
            this@MainActivity.runOnUiThread {
                showToast("Таблица пуста")
                AllTimeCardLines = ""
            }
        }
        var listText = ""

        TimeCardLinesList.forEach {
            listText += "ID: ${it.rowId};" +
                    "Операция: ${it.operationName};" +
                    "ID карточки: ${it.cardId};" +
                    "Время: ${it.deltaTime};\n"
        }
        this@MainActivity.runOnUiThread {
            AllTimeCardLines = listText
            showToast(AllTimeCardLines)
        }
    }

    //вывод в общую таблицу
    private fun readAll() {
        val TimeCardListList: List<TimeCardList> = timeTrackDb!!.timeCardListDao().getAll()

        if (!TimeCardListList.isNotEmpty()) {
            this@MainActivity.runOnUiThread {
                showToast("Таблица пуста")
                AllTimeCardLines = ""
            }
        }
        var listText = ""
        TimeCardListList.forEach {
            listText += "${it.rowId};" +
                    "${it.operationName};" +
                    "${it.defOperId}; " +
                    "${it.cardId};" +
                    "${it.deltaTime};" +
                    "${it.dataField};" +
                    "${it.businessUnitName};" +
                    "${it.comment};" +
                    "${it.startTime};" +
                    "${it.stopTime};" +
                    "${it.countProduct};\n"
            //}
        }
        this@MainActivity.runOnUiThread {
            AllTimeCardLines = listText
            //showToast(AllTimeCardLines)
        }
    }

    private fun readAllTest() {
        val TimeCardListList: List<TimeCardList> = timeTrackDb!!.timeCardListDao().getAll()

        if (!TimeCardListList.isNotEmpty()) {
            this@MainActivity.runOnUiThread {
                showToast("Таблица пуста")
                AllTimeCardLines = ""
            }
        }
        var listText = ""

        TimeCardListList.forEach {
            listText += "ID;" +
                    "Операция;" +
                    "ID операции; " +
                    "ID карточки;" +
                    "Время;" +
                    "Дата;" +
                    "Бизнес Юнит;" +
                    "Комментарий;" +
                    "Время начала;" +
                    "Время конца;\n"

        }


        this@MainActivity.runOnUiThread {
            AllTimeCardLines = listText
            //showToast(AllTimeCardLines)
        }
    }

    //создание файла
    private fun saveFile(saveString: String) {
        val output: Writer
        val file = createFile()


        output = BufferedWriter(FileWriter(file))
        output.write(saveString)

//        val sendIntent = Intent()
//        sendIntent.action = Intent.ACTION_SEND
//        sendIntent.putExtra("MY_FILE", file)
//        startActivity(Intent.createChooser(sendIntent, "Поделиться"))
        output.close()


    }

    private fun createFile(): File {
        fileNum++
        val fileName = "CardNum$cardId" +
                "checkNumber$fileNum" +
                "someSystemNumbers"

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (storageDir != null) {
            if (!storageDir.exists()) {
                storageDir.mkdir()
            }
        }
        return File.createTempFile(
            fileName,
            ".csv",
            storageDir
        )
    }


    //прочие функции
    //очищение поля текста
    fun clear(v: View) {
        txtComment.setText("")
    }

    //Диалог на потверждение выхода
    override fun onBackPressed() {
        openQuitDialog()
    }

    private fun openQuitDialog() {

        val quitDialog = AlertDialog.Builder(this).create()

        quitDialog.setTitle("Выход: Вы уверены?")

        quitDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Да", { dialogInterface, i ->
            finish()
        })

        quitDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отменить", { dialogInterface, i ->
            @Override
            fun onClick(dialog: DialogInterface, which: Int) {
                // TODO Auto-generated method stub
            }
        })

        quitDialog.show()

    }

    // данные из CardActivityMain
    private fun getIdFromCardChoice() {
        operationListNum = intent.getLongExtra(TOTAL_COUNT, 0)
        operationArr = intent.getLongArrayExtra(TOTAL_ARRAY)
        operationAuto = intent.getStringArrayExtra(TOTAL_AUTO)

    }

    //уведомление
    private fun showToast(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard(view: View) {
        //Убирает клавиатуру
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @SuppressLint("SimpleDateFormat")
    fun convertLongToDateString(systemTime: Long): String {
        return SimpleDateFormat("EEEE MMM-dd-yyyy' Time: 'HH:mm")
            .format(systemTime).toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICKFILE_RESULT_CODE -> if (resultCode == Activity.RESULT_OK) {
                val FilePath = data!!.data!!.path
                showToast("$FilePath")
            }
        }
    }


    //старые функции
    private fun oldstopBtn(
        chron: Chronometer,
        txtOper: TextView,
        txtDeltaTime: TextView,
        defOperId: Long,
        countProduct: Int
    ) {
        var stopTime = chron.base - SystemClock.elapsedRealtime()
        chron.stop()

        deltaTime = (stopTime * (-1)) / 1000
        strDeltaTime = "$deltaTime"
        txtDeltaTime.text = strDeltaTime

        val currentDate = Date()
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeText = timeFormat.format(currentDate)



        operationInField = txtOper.text.toString()
        commentInField = txtComment.text.toString()


        //сохранение операции и времени
        createTimeLineCard(
            operationInField,
            strDeltaTime,
            cardId,
            defOperId,
            commentInField,
            "startTime",
            "stopTime",
            countProduct
        )


        Timer().schedule(10000) {
            //do something
        }

        Timer().schedule(object : TimerTask() {
            override fun run() {
                //вывод операций карточки
                if (timeTrackDb == null) {
                    showToast("Подключите(создайте) базу данных")
                    return
                }
                //Простое данных добавление в поле и в файл
                java.lang.Thread {
                    readAllTimeCardLineInField()
                    //readOperationDefaultInArray()
                    //readAllTimeCardLineInVal()
                    readAll()
                }.start()
            }
        }, 200)



        chron.base = SystemClock.elapsedRealtime()


        //новый способ вывода данных в файл
        if (!(AllTimeCardLines == "")) {
            var test = AllTimeCardLines
            saveFile(test.toString())
        }
    }

    private fun createOperationDefault(string: String) {
        if (timeTrackDb == null) {
            showToast("Подключите(создайте) базу данных")
            return
        }
        Thread {
            timeTrackDb!!.operationDefaultDao().insertOperationDefault(OperationDefault(string))

            val numbersOfRows = timeTrackDb!!.operationDefaultDao().numbersOfRows()

            this@MainActivity.runOnUiThread {
                showToast("Число операций(строк): $numbersOfRows")
            }
        }.start()
    }

    //корзина
    fun trash() {
//
//        alert.setOnClickListener {
//            val simpleAlert = AlertDialog.Builder(this).create()
//
//            val editView = layoutInflater.inflate(R.layout.edit_text_layout, null)
//
//
//            simpleAlert.setView(editView)
//
//            simpleAlert.setTitle("Title")
//            simpleAlert.setMessage("Такие пироги")
//
//            simpleAlert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", { dialogInterface, i ->
//                showToast("press ok")
//                val text = simpleAlert.alert_dialog_edit_text.text
//
//                val text2 = simpleAlert.ed2.text
//                showToast(text.toString())
//                showToast(text2.toString())
//            })
//
//            simpleAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", { dialogInterface, i ->
//                showToast("press cancel")
//            })
//
//            simpleAlert.setButton(AlertDialog.BUTTON_NEUTRAL, "NEUTRAL", { dialogInterface, i ->
//                showToast("press neutral")
//            })
//
//            simpleAlert.show()
//        }

//        test += "asd"
//        test += "qwe"
//
//
//        var x = 0
////        for (i in test){
////            showToast(test[x])
////            x++
////        }
//        test.forEachIndexed { i, s ->
//            showToast(test[i])
//        }

        //    var num3 = System.currentTimeMillis()
//    var num4 = SystemClock.elapsedRealtime()
        //путь файла db
//    override fun getDatabasePath(name: String): File? {
//        //blah-blah
//    }

        //проверка ввода
//    if (editText.getText().toString().equals(""))
//    {
//// Здесь код, если EditText пуст
//    }
//    else
//    {
//// если есть текст, то здесь другой код
//    }

//таймер delay
        //        Timer().schedule(10000){
//            //do something
//        }


        //  fun formatNights(nights: List<SleepNight>, resources: Resources): Spanned {
//        val sb = StringBuilder()
//        sb.apply {
//            append(resources.getString(R.string.title))
//            nights.forEach {
//                append("<br>")
//                append(resources.getString(R.string.start_time))
//                append("\t${convertLongToDateString(it.startTimeMilli)}<br>")
//                if (it.endTimeMilli != it.startTimeMilli) {
//                    append(resources.getString(R.string.end_time))
//                    append("\t${convertLongToDateString(it.endTimeMilli)}<br>")
//                    append(resources.getString(R.string.quality))
//                    append("\t${convertNumericQualityToString(it.sleepQuality, resources)}<br>")
//                    append(resources.getString(R.string.hours_slept))
//                    // Hours
//                    append("\t ${it.endTimeMilli.minus(it.startTimeMilli) / 1000 / 60 / 60}:")
//                    // Minutes
//                    append("${it.endTimeMilli.minus(it.startTimeMilli) / 1000 / 60}:")
//                    // Seconds
//                    append("${it.endTimeMilli.minus(it.startTimeMilli) / 1000}<br><br>")
//                }
//            }
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            return Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
//        } else {
//            return HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
//        }
        //  }


        //    private fun geZoneId(): ZoneId {
//        return if (showLocal || Utils.isEmpty(getEventTimeZone()))
//            ZoneId.systemDefault()
//        else
//            ZoneId.of(getEventTimeZone())
//    }
//
    }


}
