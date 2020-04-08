package com.example.timenorm

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.*
import androidx.room.Room
import com.example.timenorm.database.*
import kotlinx.android.synthetic.main.activity_card_choice.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class CardChoiceActivity : AppCompatActivity() {

    var timeTrackDb: TimeTrackDb? = null

    var createBu = ""

    //Передаваемые данные в MainActivity
    var operationListNum = 0L
    var operationArr = longArrayOf()
    var operationAuto = emptyArray<String>()
    var checkNabor = 0
    var checkCreateNabor = 0
    var checkAuto = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_choice)

        //создание базы данных
        timeTrackDb = Room.databaseBuilder(
            this,
            TimeTrackDb::class.java,
            TimeTrackContract.DATABASE_NAME
        )
            .build()





        Timer().schedule(object : TimerTask() {
            override fun run() {
                operationIn()

            }
        }, 800)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                operationIn()
                java.lang.Thread {
                    //readOperationDefaultInArray()
                    //readAllTimeCardLineInVal()
                    createOperationAuto()
                    checkAuto = 1
                }.start()
            }
        }, 2000)

        checkNabor = 1


        btnCount1.setOnClickListener {
            operationArr = longArrayOf(2, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
            checkNabor = 1
        }

        btnCount2.setOnClickListener {
            operationArr = longArrayOf(2, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
            checkNabor = 2
        }

        btnSetId.setOnClickListener {
            if (checkAuto == 1) {
                val sdf = SimpleDateFormat("dd.M.yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())
                createBu = txtBuEdit.text.toString()
                createTimeCard(createBu, currentDate)
                setId()
            }

        }

        btnAddOper.setOnClickListener {
            createOperationDefault(newOperEdit.text.toString())
        }


    }


    //работа со стандартными операциями
    private fun createOperationDefault(string: String) {
        if (timeTrackDb == null) {
            showToast("Подключите(создайте) базу данных")
            return
        }
        Thread {
            timeTrackDb!!.operationDefaultDao().insertOperationDefault(OperationDefault(string))

            val numbersOfRows = timeTrackDb!!.operationDefaultDao().numbersOfRows()

            this@CardChoiceActivity.runOnUiThread {
                //showToast("Число операций(строк): $numbersOfRows")
            }
        }.start()
    }

    private fun createOperationAuto() {
        val OperationDefaultList: List<OperationDefault> =
            timeTrackDb!!.operationDefaultDao().getAllOperationDefault()

        if (!OperationDefaultList.isNotEmpty()) {
            this@CardChoiceActivity.runOnUiThread {
                showToast("Таблица пуста")
            }
        }

        OperationDefaultList.forEach {
            operationAuto += "${it.operationName}"
        }


    }

    private fun operationIn() {
        val OperationDefaultList: List<OperationDefault> =
            timeTrackDb!!.operationDefaultDao().getAllOperationDefault()

        if (!OperationDefaultList.isNotEmpty()) {
            this@CardChoiceActivity.runOnUiThread {
                showToast("Таблица пуста")
                showToast("Стандартные операции загружены")
                createOperationDefault("Погрузка")
                createOperationDefault("Выгрузка")
                createOperationDefault("Пересчет товаров")
                createOperationDefault("")
                createOperationDefault("")

            }
        }
//        var RowId = 0L
//        if (timeTrackDb!!.operationDefaultDao().checkOperationDefaultExist(RowId) <= 0) {
//
//            this@CardChoiceActivity.runOnUiThread {
//
//            }
//        }
    }


    fun setId() {
        if ((checkNabor == 0) && (checkCreateNabor == 0)) {
            showToast("Выберите набор операций")
            return
        }
        val numIntent = Intent(this, MainActivity::class.java)

        numIntent.putExtra(MainActivity.TOTAL_COUNT, operationListNum)
        numIntent.putExtra(MainActivity.TOTAL_ARRAY, operationArr)
        numIntent.putExtra(MainActivity.TOTAL_AUTO, operationAuto)

        startActivity(numIntent)

    }

    private fun showToast(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

    private fun createTimeCard(strBU: String, strData: String) {


        if (timeTrackDb == null) {
            showToast("Подключите(создайте) базу данных")
            return
        }
        Thread {
            timeTrackDb!!.timeCardDao().insertTimeCard(TimeCard(strBU, strData))

            val numbersOfRows = timeTrackDb!!.timeCardDao().numbersOfRows()

            this@CardChoiceActivity.runOnUiThread {
                //showToast("Число карточек(строк): $numbersOfRows")
            }
        }.start()


    }

    private fun checkPassForClear() {
//        val dBuilder = Builder(this)
//        val dLayout = LinearLayout(this)
//        val textHint = TextView(this)
//        val enterPass = EditText(this)
//        val yes = Button(this)
//        var RowId = 1L
//
//        textHint.text = "Введите пароль, чтобы очистить список стандартных операций\n" +
//                "!!ВНИМАНИЕ!! Все введенные Вами операции будут удалены из базы данных\n" +
//                "Все замеры останутся"
//        enterPass.hint = "Введите пароль"
//        enterPass.setSingleLine()
//        textHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
//
//
//        yes.text = "Удалить Стандартные операции"
//
//        yes.setOnClickListener {
//            var check = enterPass.text.toString()
//
//            if (check == "") {
//                if (timeTrackDb == null) {
//                    showToast("Подключите(создайте) базу данных")
//                }
//                Thread {
//                        //timeTrackDb!!.operationDefaultDao().deleteAllOperationDefault()
//                    this.deleteDatabase("timeTrackDb.db")
//
//                        showToast("Приложение закрывается. Ждите...")
////                        Timer().schedule(object : TimerTask() {
////                            override fun run() {
////                                finish()
////                            }
////                        }, 8000)
//
//                }.start()
//
//
//
//            } else {
//                showToast("Вы ввели неправильный пароль. Напишите еще раз")
//            }
//        }
//
//
//
//
//        dLayout.orientation = LinearLayout.VERTICAL
//        dLayout.addView(textHint)
//        dLayout.addView(enterPass)
//        dLayout.addView(yes)
//
//        dLayout.setPadding(10, 10, 10, 10)
//
//        dBuilder.setView(dLayout)
//
//        dBuilder.setNegativeButton("Отменить") { dialogInterface: DialogInterface, i ->
//
//            dialogInterface.dismiss()
//        }
//
//        dBuilder.create().show()
//

    }

//    private fun readTimeCardInField() {
//        if (timeTrackDb == null) {
//            showToast("Подключите(создайте) базу данных")
//            return
//        }
//        Thread {
//
//            val timeCard: TimeCard = timeTrackDb!!.timeCardDao().getMaxCard()
//
//            this@CardChoiceActivity.runOnUiThread {
//                txtCardIdCh.text = "Id: ${timeCard.rowId}"
//                txtBUCh.text = "BU: ${timeCard.businessUnitName}"
//                txtDataCh.text = "Data: ${timeCard.dataField}"
//                //cardId = getRowId
//            }
//
//        }.start()
//    }


}
