package com.example.internalchart

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar


class UseRate : AppCompatActivity() {
    //URL = https://tral-lalala.tistory.com/57
    private var Linechart: LineChart? = null

    //主標題 項目 --這邊才要抓全部
    var entries: ArrayList<Entry> = ArrayList()

    //副標題 項目
    var theDates: ArrayList<String> = ArrayList()



    lateinit var txt1: TextView
    lateinit var txt2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userate)

        txt1 = findViewById<TextView>(R.id.date_start)
        txt2 = findViewById<TextView>(R.id.date_end)
        Linechart = findViewById<LineChart>(R.id.Linechart)

        //7天前 和 當天 前取
        settime()

        //日期選擇器  後取
        startandend()

        //返回鍵
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

        //系統時間類 當天
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
        val clear = findViewById<Button>(R.id.clean)

        //重製畫面
        clear.setOnClickListener(View.OnClickListener {
            entries!!.clear()
            theDates!!.clear()
            Linechart!!.clear()
        })
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

                entries!!.clear()
                theDates!!.clear()
                Linechart!!.clear()
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

                entries!!.clear()
                theDates!!.clear()
                Linechart!!.clear()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //API
    private fun Parameter(d1: String, d2: String) {

        val Getparameter = String.format("http://192.168.1.203:5000/api/V1/Analysis/quantity/NTkxOTE0ODlCRjZBNDlGRURCMTIzMDQ0QTRBQUFDMEE=/000101/3/"+d1+"/"+d2)

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
                    var machinename: String = ""
                    var number: Int = 0

                    val jsonArray = JSONArray(response.body!!.string())
                    for (i in 0..jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        machinename = jsonObject.getString("machinename")
                        number = jsonObject.getInt("number")
                        // group = jsonObject.getString("groupName")
                        //設定資料 --抓不到群
                        //entries = ArrayList()
                        //theDates = ArrayList()

                        entries.add(Entry(i.toFloat(), number.toFloat()))
                        //X軸名稱
                        theDates.add(machinename)
                        //寫入spinner裡

                        //      for (i in -1..i) {
                        //          Line()
                        //     }
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                runOnUiThread {
                    Line()
                }
            }
        })
    }

    private fun setupLineChart() {

        Linechart!!.description.isEnabled = false
        Linechart!!.setNoDataText("暫無資料"); //無資料顯示

        val l = Linechart!!.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.isEnabled = true

        //Line()
    }

    //折線圖
    fun Line() {


        //線條與圖框
        val dataset: LineDataSet

        //設定資料
        //entries = ArrayList()
        //entries.add(Entry(0F, number.toFloat()))

        //設定圖表格式
        dataset = LineDataSet(entries, "測試中")
        dataset.color = ContextCompat.getColor(this, R.color.red)
        dataset.lineWidth = 13f
        //點 寬比
        dataset.circleRadius = 10f
        //線 字大小
        dataset.valueTextSize = 18f
        dataset.setDrawFilled(true)
        //Line變隨性 setMode
        dataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataset.valueTextColor = ContextCompat.getColor(this, R.color.black)

        //theDates = ArrayList()
        //X軸名稱
        //theDates.add(deviceID)

        val xAxis = Linechart!!.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(theDates)
        xAxis.position = XAxis.XAxisPosition.BOTTOM


        val yAxisRight = Linechart!!.axisRight
        yAxisRight.setEnabled(false)

        val yAxisLeft = Linechart!!.axisLeft
        yAxisLeft.setGranularity(1f)

        //下拉式
        //var spinner1 = findViewById<Spinner>(R.id.Spinner1)
        //val lunch = arrayListOf("雞腿飯", "魯肉飯", "排骨飯", "水餃", "陽春麵")
        //val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, theDates)
        //spinner1.adapter = adapter

        //移除右下角標籤
        Linechart!!.description.isEnabled = false

        // Setting Data
        val data = LineData(dataset)

        Linechart!!.setData(data)

        //持續動畫
        Linechart!!.animateX(2500)
        //refresh 刷新
        Linechart!!.invalidate()



    }

    /*   private fun getEntries() {

           //主標題 項目
           val linevalues : ArrayList<Entry>
           //副標題 項目
           val chartname : ArrayList<String>
           val lineDataSet: LineDataSet
           val lineData: LineData


           //下面標題
           chartname = ArrayList()
           chartname.add("1月")
           chartname.add("2月")
           chartname.add("3月")
           chartname.add("4月")


           linevalues = ArrayList()
           linevalues.add(Entry(20f, 0.0F))
           linevalues.add(Entry(30f, 3.0F))
           linevalues.add(Entry(40f, 2.0F))
           linevalues.add(Entry(50f, 1.0F))

           lineDataSet = LineDataSet(linevalues, "first")
           //lineDataSet.circleRadius = 0f
           //lineDataSet.setDrawFilled(true)
           lineDataSet.valueTextSize = 30f
           lineDataSet.fillAlpha = 110
           lineDataSet.formLineWidth = 13f
          // lineDataSet.color = resources.getColor(R.color.green)

           //副標題 字串寫法 MP版本3.0.0後
           Linechart!!.xAxis.valueFormatter = IndexAxisValueFormatter(chartname)
           Linechart!!.animateY(500);

           Linechart!!.getXAxis().setGranularityEnabled(true);
           Linechart!!.getXAxis().setGranularity(0.0f);
           Linechart!!.getXAxis().setLabelCount(lineDataSet.getEntryCount());

           //輸出
           lineData = LineData(lineDataSet)
           Linechart!!.data = lineData
           Linechart!!.invalidate()

           //上下方標題名
           Linechart!!.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

           lineDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 250)

           lineDataSet.valueTextColor=Color.BLACK
           lineDataSet.valueTextSize=15f

       }
       */


    //按鍵
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == android.R.id.home) {
            //Toast.makeText(this, "按下左上角返回鍵", Toast.LENGTH_SHORT).show();
            startActivity(Intent(this, MainActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    //返回鍵
    fun toMain() {
        val actionBar: ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}