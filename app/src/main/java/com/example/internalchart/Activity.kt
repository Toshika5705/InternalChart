package com.example.internalchart

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar

class Activity : AppCompatActivity() {
    private var barchart: BarChart? = null

    lateinit var txt1: TextView
    lateinit var txt2: TextView

    var one : String = ""
    var onename : String = ""
    var two : String = ""
    var twoname : String = ""
    var three : String = ""
    var threename : String = ""
    var four : String = ""
    var fourname : String = ""
    var five : String = ""
    var fivename : String = ""
    var six : String = ""
    var sixname : String = ""
    var seven : String = ""
    var sevenname : String = ""
    var eight : String = ""
    var eightname : String = ""
    var nine : String = ""
    var ninename : String = ""
    var ten : String = ""
    var tenname : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        txt1 = findViewById<TextView>(R.id.date_start)
        txt2 = findViewById<TextView>(R.id.date_end)
        barchart = findViewById<BarChart>(R.id.barchart)

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

       // val Getparameter = String.format("https://app.hugsys.com/api/group/getappchart/000001?key=NTkxOTE0ODlCRjZBNDlGRURCMTIzMDQ0QTRBQUFDMEE=&number=2&start=%s&end=%2s",
         //       d1, d2)
        val Getparameter = String.format("http://192.168.1.203:5000/api/V1/Analysis/quantity/NTkxOTE0ODlCRjZBNDlGRURCMTIzMDQ0QTRBQUFDMEE=/000000/2/"+d1+"/"+d2)


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
                    val jsonObject1 = jsonArray.getJSONObject(1)
                    val jsonObject2 = jsonArray.getJSONObject(2)
                    val jsonObject3 = jsonArray.getJSONObject(3)
                    val jsonObject4 = jsonArray.getJSONObject(4)
                    val jsonObject5 = jsonArray.getJSONObject(5)
                    val jsonObject6 = jsonArray.getJSONObject(6)
                    val jsonObject7 = jsonArray.getJSONObject(7)
                    val jsonObject8 = jsonArray.getJSONObject(8)
                    val jsonObject9 = jsonArray.getJSONObject(9)

                    one = "" + jsonObject.getInt("number")
                    two = "" + jsonObject1.getInt("number")
                    three = "" + jsonObject2.getInt("number")
                    four = "" + jsonObject3.getInt("number")
                    five = "" + jsonObject4.getInt("number")
                    six = "" + jsonObject5.getInt("number")
                    seven = "" + jsonObject6.getInt("number")
                    eight = "" + jsonObject7.getInt("number")
                    nine = "" + jsonObject8.getInt("number")
                    ten = "" + jsonObject9.getInt("number")
                    onename = "" + jsonObject.getString("name")
                    twoname = "" + jsonObject1.getString("name")
                    threename = "" + jsonObject2.getString("name")
                    fourname = "" + jsonObject3.getString("name")
                    fivename = "" + jsonObject4.getString("name")
                    sixname = "" + jsonObject5.getString("name")
                    sevenname = "" + jsonObject6.getString("name")
                    eightname = "" + jsonObject7.getString("name")
                    ninename = "" + jsonObject8.getString("name")
                    tenname = "" + jsonObject9.getString("name")

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                runOnUiThread {
                    setupPieChart()
                }
            }
        })
    }

    private fun setupPieChart() {

        barchart!!.description.isEnabled = false
        barchart!!.setScaleEnabled(false) //關閉縮放

        val l = barchart!!.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.isEnabled = true

        bar()
    }

    private fun bar(){
        //主標題 項目
        val barList : ArrayList<BarEntry>
        //副標題 項目
        val theDates : ArrayList<String>
        //val theDates = java.util.ArrayList<String>()
        val barDataSet: BarDataSet
        val barData: BarData
        //val barchart = findViewById<BarChart>(R.id.barChart)

        barList = ArrayList()
        barList.add(BarEntry(1f, one.toFloat()))
        barList.add(BarEntry(2f, two.toFloat()))
        barList.add(BarEntry(3f, three.toFloat()))
        barList.add(BarEntry(4f, four.toFloat()))
        barList.add(BarEntry(5f, five.toFloat()))
        barList.add(BarEntry(6f, six.toFloat()))
        barList.add(BarEntry(7f, seven.toFloat()))
        barList.add(BarEntry(8f, eight.toFloat()))
        barList.add(BarEntry(9f, nine.toFloat()))
        barList.add(BarEntry(10f, ten.toFloat()))

        barDataSet = BarDataSet(barList, "")

        theDates = ArrayList()
        //第一個自動消失
        if (one.toFloat() != 0.0f){
            theDates.add("")
            theDates.add(onename)
        }else{
            null
        }
        if (two.toFloat() != 0.0f){
            theDates.add(twoname)
        }else{
            null
        }
        if (three.toFloat() != 0.0f){
            theDates.add(threename)
        }else{
            null
        }
        if (four.toFloat() != 0.0f){
            theDates.add(fourname)
        }else{
            null
        }
        if (five.toFloat() != 0.0f){
            theDates.add(fivename)
        }else{
            null
        }
        if (six.toFloat() != 0.0f){
            theDates.add(sixname)
        }else{
            null
        }
        if (seven.toFloat() != 0.0f){
            theDates.add(sevenname)
        }else{
            null
        }
        if (eight.toFloat() != 0.0f){
            theDates.add(eightname)
        }else{
            null
        }
        if (nine.toFloat() != 0.0f){
            theDates.add(ninename)
        }else{
            null
        }
        if (ten.toFloat() != 0.0f){
            theDates.add(tenname)
        }else{
            null
        }

        //副標題 字串寫法 MP版本3.0.0後
        barchart!!.xAxis.valueFormatter = IndexAxisValueFormatter(theDates)
        barchart!!.animateY(500);

        barchart!!.getXAxis().setGranularityEnabled(true);
        barchart!!.getXAxis().setGranularity(0.0f);
        barchart!!.getXAxis().setLabelCount(barDataSet.getEntryCount());

        //輸出
        barData = BarData(barDataSet)
        barchart!!.data = barData
        barchart!!.invalidate()

        //上下方標題名
        barchart!!.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 250)

        barDataSet.valueTextColor=Color.BLACK
        barDataSet.valueTextSize=15f
    }

    //標題
    fun tablename(){
        val actionbar = supportActionBar
        actionbar!!.title = "運動前10位活躍者"
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