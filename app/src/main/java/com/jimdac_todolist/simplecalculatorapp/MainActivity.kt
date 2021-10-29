package com.jimdac_todolist.simplecalculatorapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.jimdac_todolist.simplecalculatorapp.model.History
import java.lang.NumberFormatException
import java.math.BigInteger

class MainActivity : AppCompatActivity() {

    private val expressionTextView: TextView by lazy {
        findViewById(R.id.expressionTextView)
    }

    private val resultTextView: TextView by lazy {
        findViewById(R.id.resultTextView)
    }

    private val historyLayout: ConstraintLayout by lazy {
        findViewById(R.id.history_layout)
    }

    private var isOperator: Boolean = false
    private var hasOperator: Boolean = false
    lateinit var db: AppDataBase
    
    //리사이클러뷰 사용을 위한 간단한 어뎁터 사용
    lateinit var historyAdapter : HistoryAdapter

//    private val historyLinearLayout : LinearLayout by lazy {
//        findViewById(R.id.historyLinearLayout)
//    }

    private val recyclerView:RecyclerView by lazy {
        findViewById(R.id.history_recyclerview)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = Room.databaseBuilder(applicationContext, AppDataBase::class.java, "historyDB").build()

    }

    private fun numberButtonClicked(number: String) {

//        if (isOperator){
//            expressionTextView.append(" ")
//        }

        val expressionText = expressionTextView.text.split(" ")
        Log.d("TAG", expressionText.toString())
        if (expressionText.last().length >= 15) {
            Toast.makeText(this@MainActivity, "15자리 까지만 사용할 수 있습니다.", Toast.LENGTH_LONG).show()
            return

        } else if (expressionText.last().isEmpty() && number == "0") {
            Toast.makeText(this@MainActivity, "0은 제일 앞에 올 수 없습니다.", Toast.LENGTH_LONG).show()
            return
        }

        expressionTextView.append(number)
        isOperator = false

        resultTextView.text = calculateExpression()

    }

    fun buttonClicked(v: View) {
        when (v.id) {
            R.id.button0 -> {
                numberButtonClicked("0")
            }
            R.id.button1 -> {
                numberButtonClicked("1")
            }
            R.id.button2 -> {
                numberButtonClicked("2")
            }
            R.id.button3 -> {
                numberButtonClicked("3")
            }
            R.id.button4 -> {
                numberButtonClicked("4")
            }
            R.id.button5 -> {
                numberButtonClicked("5")
            }
            R.id.button6 -> {
                numberButtonClicked("6")
            }
            R.id.button7 -> {
                numberButtonClicked("7")
            }
            R.id.button8 -> {
                numberButtonClicked("8")
            }
            R.id.button9 -> {
                numberButtonClicked("9")
            }
            R.id.buttonDivider -> {
                operatorButtonClicked("/")
            }
            R.id.buttonMinus -> {
                operatorButtonClicked("-")
            }
            R.id.buttonModulo -> {
                operatorButtonClicked("%")
            }
            R.id.buttonPlus -> {
                operatorButtonClicked("+")
            }
            R.id.buttonMulti -> {
                operatorButtonClicked("x")
            }
        }
    }

    private fun calculateExpression(): String {

        val expressionText = expressionTextView.text.split(" ")
        if (expressionText.size > 2) {
            return when (expressionText[1]) {
                "/" -> {
                    (expressionText[0].toBigInteger() / expressionText[2].toBigInteger()).toString()
                }
                "-" -> {
                    (expressionText[0].toBigInteger() - expressionText[2].toBigInteger()).toString()
                }
                "%" -> {
                    (expressionText[0].toBigInteger() % expressionText[2].toBigInteger()).toString()
                }
                "+" -> {
                    (expressionText[0].toBigInteger() + expressionText[2].toBigInteger()).toString()
                }
                "x" -> {
                    (expressionText[0].toBigInteger() * expressionText[2].toBigInteger()).toString()
                }
                else -> {
                    ""
                }
            }
        }
        return ""
    }

    fun resultButtonClicked(v: View) {

        val expressionText = expressionTextView.text.split(" ")

        if (expressionText.size <= 2) {
            return
        }
        if (expressionText[2].isEmpty()) {
            Toast.makeText(this@MainActivity, "아직 완성되지 않은 수식입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (expressionText[0].isBigInteger().not() || expressionText[2].isBigInteger().not()) {
            Toast.makeText(this@MainActivity, "입력한 수에서 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val resultText = resultTextView.text.toString()
        val tempExpressionText = expressionTextView.text.toString()

        expressionTextView.text = resultText
        resultTextView.text = ""

        isOperator = false
        hasOperator = false

        //todo DB에 넣어주는 부분(비동기처리)
        Thread {
            db.historyDao().insertHistory(History(null,tempExpressionText,resultText))
        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun operatorButtonClicked(operator: String) {

        if (expressionTextView.text.isEmpty()) {
            return
        }

        when {
            isOperator -> {
                expressionTextView.text = "${expressionTextView.text.dropLast(2)}$operator "
            }
            hasOperator -> {
                Toast.makeText(this@MainActivity, "연산자는 한번만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                expressionTextView.append(" $operator ")
            }
        }
        //SpannableStringBuilder는 텍스트의 글자의 일부나 전체의 스타일을 변경하고 싶을 때 사용한다.
        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.green)), expressionTextView.length() - 2,
            expressionTextView.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        expressionTextView.text = ssb

        hasOperator = true
        isOperator = true
    }

    fun clearButtonClicked(v: View) {
        hasOperator = false
        isOperator = false
        expressionTextView.text = ""
        resultTextView.text = ""
    }

    fun String.isBigInteger(): Boolean {
        return try {
            this.toBigInteger()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    @SuppressLint("SetTextI18n")
    fun historyButtonClicked(v: View) {
        historyLayout.isVisible = true
        //todo 디비에서 모든 기록 가져오기
        //todo 뷰에 모든 기록 할당하기


//        historyLinearLayout.removeAllViews()
//        Thread{
//            val dbHistory : List<History> = db.historyDao().getAll()
//
//            runOnUiThread {
//                dbHistory.reversed().forEach {
//                    val view = layoutInflater.inflate(R.layout.history_row,null,false)
//                    view.findViewById<TextView>(R.id.expression_text_row).text = it.expression
//                    view.findViewById<TextView>(R.id.result_text_row).text = "= ${it.result}"
//
//                    historyLinearLayout.addView(view)
//                }
//            }
//        }.start()

        recyclerView.removeAllViews()
        Thread{
            val dbHistory : List<History> = db.historyDao().getAll()
            historyAdapter = HistoryAdapter(dbHistory.reversed())
            runOnUiThread {
                recyclerView.adapter = historyAdapter
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            }

        }.start()
    }

    fun closeHistoryButtonClicked(v: View) {
        historyLayout.isVisible = false
    }

    fun historyClearButtonClicked(v: View) {
        //todo 디비에서 모든 기록 삭제
        //todo 뷰에서 모든 기록 삭제

        //historyLinearLayout.removeAllViews()
//        Thread {
//            db.historyDao().deleteAll()
//            Log.d("TAG", "historyClearButtonClicked: 기록삭제중")
//        }.start()


        Thread{
            db.historyDao().deleteAll()
            runOnUiThread {
                historyAdapter.setData(emptyList())
            }
        }.start()

    }
}


class HistoryAdapter(private var dataSet: List<History>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val expressionTextView: TextView
        val resultTextView: TextView
        init {
            expressionTextView = view.findViewById(R.id.expression_text_row)
            resultTextView = view.findViewById(R.id.result_text_row)
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.history_row, viewGroup, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.expressionTextView.text = dataSet[position].expression.toString()
        viewHolder.resultTextView.text = dataSet[position].result.toString()
    }


    override fun getItemCount() = dataSet.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData : List<History>){
        dataSet = newData
        notifyDataSetChanged()
    }
}
