package com.example.internalchart

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
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
import java.util.ArrayList
import java.util.Calendar

class Age_distribution : AppCompatActivity() {
    private var pieChart: PieChart? = null
    var BelowTen: String = ""
    var Ten: String = ""
    var Twenty: String = ""
    var Thirty: String = ""
    var Forty: String = ""
    var Fifty: String = ""
    var Sixty: String = ""
    var Seventy: String = ""

    lateinit var txt1: TextView
    lateinit var txt2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.age_distribution)

        txt1 = findViewById<TextView>(R.id.date_start)
        txt2 = findViewById<TextView>(R.id.date_end)
        pieChart = findViewById<PieChart>(R.id.piechart)

        //標題
        tablename()

        //7天前 和 當天 前取
        settime()

        //日期選擇器  後取
        startandend()

        //回首頁
        toMain()
    }

    //顯示當前日期
    private fun settime() {

        //轉Start 與 時間 上週
        val c = Calendar.getInstance()
        c.firstDayOfWeek = Calendar.MONDAY
        c.add(Calendar.DATE, -7)
        //上周開始
        //c[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        val sTime = c.time
        c[Calendar.DAY_OF_WEEK] = Calendar.SUNDAY
        val sdf = SimpleDateFormat("yyyy-M-d")
        val s = sdf.format(sTime).toString()

        //系統時間類
        val calendar = Calendar.getInstance()
        //年
        val year: Int = calendar.get(Calendar.YEAR)
        //月
        val month: Int = calendar.get(Calendar.MONTH) + 1
        //日
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

        //設定當前時間
        txt1.setText(String.format(s))
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

        //val Getparameter = String.format("https://app.hugsys.com/api/group/getappchart/000001?key=NTkxOTE0ODlCRjZBNDlGRURCMTIzMDQ0QTRBQUFDMEE=&number=1&start=%s&end=%2s",
                //d1, d2)
        val Getparameter = String.format("http://192.168.1.203:5000/api/V1/Analysis/quantity/NTkxOTE0ODlCRjZBNDlGRURCMTIzMDQ0QTRBQUFDMEE=/000000/1/"+d1+"/"+d2)

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
                    BelowTen = "" + jsonObject.getInt("belowTen")
                    Ten = "" + jsonObject.getInt("ten")
                    Twenty = "" + jsonObject.getInt("twenty")
                    Thirty = "" + jsonObject.getInt("thirty")
                    Forty = "" + jsonObject.getInt("forty")
                    Fifty = "" + jsonObject.getInt("fifty")
                    Sixty = "" + jsonObject.getInt("sixty")
                    Seventy = "" + jsonObject.getInt("seventy")

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
        pieChart!!.setUsePercentValues(true) //顯示百分比
        pieChart!!.setEntryLabelTextSize(16f)
        pieChart!!.setEntryLabelColor(Color.BLACK)
        //大標題
        pieChart!!.centerText = "年齡比例"
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
            val f1 = BelowTen.toFloat()
            val f2 = Ten.toFloat()
            val f3 = Twenty.toFloat()
            val f4 = Thirty.toFloat()
            val f5 = Forty.toFloat()
            val f6 = Fifty.toFloat()
            val f7 = Sixty.toFloat()
            val f8 = Seventy.toFloat()

            val entries: ArrayList<PieEntry> = ArrayList()
            /*
            entries.add(PieEntry(f1, "10歲以下"))
            entries.add(PieEntry(f2, "10-19歲"))
            entries.add(PieEntry(f3, "20-29歲"))
            entries.add(PieEntry(f4, "30-39歲"))
            entries.add(PieEntry(f5, "40-49歲"))
            entries.add(PieEntry(f6, "50-59歲"))
            entries.add(PieEntry(f7, "60-69歲"))
            entries.add(PieEntry(f8, "70-79歲"))
             */

            if (f1 != 0.0f){
                entries.add(PieEntry(f1, "10歲以下"))
            }else{
              null
            }
            if (f2 != 0.0f){
                entries.add(PieEntry(f1, "10-19歲"))
            }else{
                null
            }
            if (f3 != 0.0f){
                entries.add(PieEntry(f1, "20-29歲"))
            }else{
                null
            }
            if (f4 != 0.0f){
                entries.add(PieEntry(f1, "30-39歲"))
            }else{
                null
            }
            if (f5 != 0.0f){
                entries.add(PieEntry(f1, "40-49歲"))
            }else{
                null
            }
            if (f6 != 0.0f){
                entries.add(PieEntry(f1, "50-59歲"))
            }else{
                null
            }
            if (f7 != 0.0f){
                entries.add(PieEntry(f1, "60-69歲"))
            }else{
                null
            }
            if (f8 != 0.0f){
                entries.add(PieEntry(f1, "70-79歲"))
            }else{
                null
            }

            val colors: ArrayList<Int> = ArrayList()
            for (color in ColorTemplate.MATERIAL_COLORS) {
                colors.add(color)
            }

            for (color in ColorTemplate.VORDIPLOM_COLORS) {
                colors.add(color)
            }
            //小標題
            val dataSet = PieDataSet(entries, "年齡比例")
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
    //標題
    fun tablename(){
        val actionbar = supportActionBar
        actionbar!!.title = "年齡比例分布"
    }

    //按鍵
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == android.R.id.home) {
            //Toast.makeText(this, "按下左上角返回鍵", Toast.LENGTH_SHORT).show();
            startActivity(Intent(this, MainActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
    //返回鍵
    fun toMain(){
        val actionBar: ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}