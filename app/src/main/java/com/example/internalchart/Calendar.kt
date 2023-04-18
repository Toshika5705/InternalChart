package com.example.internalchart


import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar

class Calendar : AppCompatActivity() {
    private var pieChart: PieChart? = null
    var Male: String = ""
    var Female: String = ""

    lateinit var txt1: TextView
    lateinit var txt2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar)

        txt1 = findViewById<TextView>(R.id.date_start)
        txt2 = findViewById<TextView>(R.id.date_end)
        pieChart = findViewById<PieChart>(R.id.piechart)

        //7天前 和 當天 前取
        settime()

        //日期選擇器  後取
        startandend()
        //圖表
       // setupPieChart()
    }

    //顯示當前日期
    private fun settime() {

        //轉Start 與 時間
        //val df = SimpleDateFormat("yyyy-M-dd")
        //val c = Calendar.getInstance()
        //val i = df.format(c.time)
        //系統時間類
        val calendar = Calendar.getInstance()
        //年
        val year: Int = calendar.get(Calendar.YEAR)
        //月
        val month: Int = calendar.get(Calendar.MONTH) + 1
        //日
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

        //設定當前時間
        txt1.setText(String.format("%d-%d-%d", year, month, day))
        txt2.setText(String.format("%d-%d-%d", year, month, day))

        val d1: String = txt1.text.toString()
        val d2: String = txt2.text.toString()

        Parameter(d1, d2)

    }

    //開始&結束 日期選擇器
    private fun startandend() {
        var d1: String = ""
        var d2: String = ""

        //建立日期 & 建立時間戳 &
        var calendar = Calendar.getInstance()
        var df = SimpleDateFormat("yyyy-M-dd")

        try {
            //2022/06/01
            txt1.setOnClickListener(View.OnClickListener {
                val datePicker = DatePickerDialog(this, { view, year, month, dayOfMonth ->
                    txt1.setText(String.format("%d-%d-%d", year, month + 1, dayOfMonth))
                    d1 = txt1.text.toString()
                    d2 = txt2.text.toString()
                    Parameter(d1, d2)
                }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
                //抓取結束時間 setMaxDate後面不能選
                datePicker.getDatePicker().setMaxDate(df.parse(txt2.text.toString()).time)
                datePicker.show()
            })
            // 2022-06-01
            txt2.setOnClickListener(View.OnClickListener {
                val datePicker = DatePickerDialog(this, { view, year, month, dayOfMonth ->
                    txt2.setText(String.format("%d-%d-%d", year, month + 1, dayOfMonth))
                    d1 = txt1.text.toString()
                    d2 = txt2.text.toString()
                    Parameter(d1, d2)
                }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
                //抓取開始時間 setMinDate前面不能選
                datePicker.getDatePicker().setMinDate(df.parse(txt1.text.toString()).time)
                datePicker.show()

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //API
    private fun Parameter(d1: String, d2: String) {
        //更新參數 //內部測試抓不到
        /* val  Getparameter =  String.format("http://localhost:5000/api/GetAppChart?key=NTkxOTE0ODlCRjZBNDlGRURCMTIzMDQ0QTRBQUFDMEE=&groupID=000001&number=0&start=%s&end=%2s",
                 d1, d2) */
        val Getparameter = String.format("https://app.hugsys.com/api/group/getappchart/000001?key=NTkxOTE0ODlCRjZBNDlGRURCMTIzMDQ0QTRBQUFDMEE=&number=0&start=%s&end=%2s",
                d1, d2)

        /**建立連線 */
        val client = OkHttpClient().newBuilder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()

        /**設置傳送需求 */
        val request: Request = Request.Builder()
                .url(Getparameter)
                .build()

        /**設置回傳 */
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                /**如果傳送過程有發生錯誤 */
                e.message
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                /**取得回傳 */
                try {
                    val jsonArray = JSONArray(response.body!!.string())
                    //列
                    val jsonObject = jsonArray.getJSONObject(0)
                    Male = "" + jsonObject.getInt("Male")
                    Female = "" + jsonObject.getInt("Female")

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                runOnUiThread{
                    setupPieChart()
                }
            }
        })
    }

    private fun setupPieChart() {

        pieChart!!.isDrawHoleEnabled = true
        pieChart!!.setUsePercentValues(true)
        pieChart!!.setEntryLabelTextSize(16f)
        pieChart!!.setEntryLabelColor(Color.BLACK)
        //大標題
        pieChart!!.centerText = "性別比例"
        pieChart!!.setCenterTextSize(36f)
        pieChart!!.description.isEnabled = false

        val l = pieChart!!.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.isEnabled = true

        loadPieChartData()
    }

    private fun loadPieChartData() {

        try {
            Thread.sleep(0)
            val f1 = Male.toFloat()
            val f2 = Female.toFloat()

            val entries: ArrayList<PieEntry> = ArrayList()
            entries.add(PieEntry(f1, "男"))
            entries.add(PieEntry(f2, "女"))

            val colors: ArrayList<Int> = ArrayList()
            for (color in ColorTemplate.MATERIAL_COLORS) {
                colors.add(color)
            }

            for (color in ColorTemplate.VORDIPLOM_COLORS) {
                colors.add(color)
            }
            //小標題
            val dataSet = PieDataSet(entries, "性別比例")
            dataSet.colors = colors

            val data = PieData(dataSet)
            data.setDrawValues(true)
            data.setValueFormatter(PercentFormatter(pieChart))
            data.setValueTextSize(18f)
            data.setValueTextColor(Color.BLACK)

            pieChart!!.data = data
            pieChart!!.invalidate()

            pieChart!!.animateY(1400, Easing.EaseInOutQuad)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}