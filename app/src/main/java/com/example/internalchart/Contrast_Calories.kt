package com.example.internalchart

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar


lateinit var barChart: BarChart


lateinit var txt1: TextView
lateinit var txt2: TextView
lateinit var spinner: Spinner

//主標題 項目 --這邊才要抓全部
var entries: ArrayList<BarEntry> = ArrayList()
var entries1: ArrayList<BarEntry> = ArrayList()

//副標題 項目
var theDates: ArrayList<String> = ArrayList()
var spinnerDates: ArrayList<String> = ArrayList()
var groupidDates: ArrayList<String> = ArrayList()


var group : String = ""

var d1 : String = ""
var d2 : String = ""


class Contrast_Calories : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contrast__calories)

        txt1 = findViewById<TextView>(R.id.date_start)
        txt2 = findViewById<TextView>(R.id.date_end)
        barChart = findViewById(R.id.barchart)
        spinner = findViewById(R.id.Spinner1)

        toMain()

        btnclear()
        Groups()

        //7天前 和 當天 前取
        settime()
        //日期選擇器  後取
        startandend()


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

         d1 = txt1.text.toString()
         d2 = txt2.text.toString()

        Parameter(d1, d2)

    }

    //開始&結束 日期選擇器
    private fun startandend() {

        //建立日期 & 建立時間戳 &
        var calendar = Calendar.getInstance()
        var df = SimpleDateFormat("yyyy-M-dd")

        try {
            //2022/06/01
            txt1.setOnClickListener(View.OnClickListener {
                val datePicker = DatePickerDialog(
                    this,
                    { view, year, month, dayOfMonth ->
                        txt1.setText(String.format("%d-%d-%d", year, month + 1, dayOfMonth))
                        d1 = txt1.text.toString()
                        d2 = txt2.text.toString()
                        Parameter(d1, d2)
                    },
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]
                )
                //抓取結束時間 setMaxDate後面不能選
                datePicker.getDatePicker().setMaxDate(df.parse(txt2.text.toString()).time)
                datePicker.show()

                entries!!.clear()
                entries1!!.clear()
                theDates!!.clear()
                barChart!!.clear()
            })
            // 2022-06-01
            txt2.setOnClickListener(View.OnClickListener {
                val datePicker = DatePickerDialog(
                    this,
                    { view, year, month, dayOfMonth ->
                        txt2.setText(String.format("%d-%d-%d", year, month + 1, dayOfMonth))
                        d1 = txt1.text.toString()
                        d2 = txt2.text.toString()
                        Parameter(d1, d2)
                    },
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]
                )
                //抓取開始時間 setMinDate前面不能選
                datePicker.getDatePicker().setMinDate(df.parse(txt1.text.toString()).time)
                datePicker.show()

                entries!!.clear()
                entries1!!.clear()
                theDates!!.clear()
                barChart!!.clear()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //API
    private fun Parameter(d1: String, d2: String) {

        val Getparameter = String.format("http://192.168.1.203:5000/api/V1/Analysis/quantity/NTkxOTE0ODlCRjZBNDlGRURCMTIzMDQ0QTRBQUFDMEE=/"+group+"/4/" + d1 + "/" + d2)

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
                    var deviceID: String = ""
                    var same: Int = 0
                    var different: Int = 0

                    val jsonArray = JSONArray(response.body!!.string())
                    for (i in 0..jsonArray.length()) {
                        //列
                        val jsonObject = jsonArray.getJSONObject(i)

                        deviceID = jsonObject.getString("deviceid")
                        same = jsonObject.getInt("same")
                        different = jsonObject.getInt("different")

                        entries.add(BarEntry(i.toFloat(), same.toFloat()))
                        entries1.add(BarEntry(i.toFloat(), different.toFloat()))
                        //X軸名稱
                        theDates.add(deviceID)

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                runOnUiThread {
                    BarChartData()
                }
            }
        })

    }
    /**抓取編組**/
    private fun Groups(){

        val Getgroups = String.format("http://192.168.1.203:5000/api/V1/Groups/NTkxOTE0ODlCRjZBNDlGRURCMTIzMDQ0QTRBQUFDMEE=")

        val gclient = OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build()

        /**抓取Groups**/
        val Grequest: Request = Request.Builder().url(Getgroups).build()
        val Groups = gclient.newCall(Grequest)
        Groups.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.message
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    var groupid : String = ""
                    var groupname: String = ""

                    val jsonArray = JSONArray(response.body!!.string())
                    for (i in 0..jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        groupname = jsonObject.getString("groupname")
                        groupid = jsonObject.getString("groupid")

                        spinnerDates.add(groupname)
                        groupidDates.add(groupid)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                runOnUiThread {
                    entries!!.clear()
                    entries1!!.clear()
                    theDates!!.clear()
                    barChart!!.clear()

                    DropdownGp()
                }
            }

        })
    }

    fun DropdownGp(){

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerDates)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = (object : OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                group = groupidDates.get(p2)
                Parameter(d1, d2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        })
    }

    //直條圖比較
    fun BarChartData() {
        val barDataSet1: BarDataSet
        val barDataSet2: BarDataSet
        barDataSet1 = BarDataSet(entries, "")
        barDataSet1.setColor(resources.getColor(R.color.blue))
        barDataSet2 = BarDataSet(entries1, "")
        barDataSet2.setColor(resources.getColor(R.color.red))

        // on below line we are adding bar data set to bar data
        var data = BarData(barDataSet1, barDataSet2)

        // on below line we are setting data to our chart
        barChart.data = data

        // 在下面一行，我们正在设置描述启用。 浮水印
        barChart.description.isEnabled = false

        // 在下面的线设置x轴
        var xAxis = barChart.xAxis
        val yAxisLeft = barChart!!.axisLeft
        val yAxisRight = barChart!!.axisRight


        //下面一行是将值格式化程序设置为我们的x轴和
        //我们将我们的日子添加到我们的x轴。
        xAxis.valueFormatter = IndexAxisValueFormatter(theDates)

        //下面的线是设置中心轴
        //标签到我们的条形图。
        xAxis.setCenterAxisLabels(true)

        //下面一行是设置位置
        //到我们的x轴到底部。
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        //下面一行是设置粒度
        //到我们的x轴标签。
        xAxis.setGranularity(1f)

        //下面一行是启用
        //x轴的粒度。
        xAxis.setGranularityEnabled(true)

        // 下面一行是使我们的
        //条形图为可拖动。
        barChart.setDragEnabled(true)
        //右邊 不繪制格網線
        yAxisRight.setDrawGridLines(false);
        yAxisLeft.setGranularity(1f)
        //下面的行是为了使可见
        //我们的条形图的范围。
        barChart.setVisibleXRangeMaximum(4f)

        //下面一行是添加栏
        //空间到我们的图表。
        val barSpace = 0.1f

        //下面一行是用来添加组的
        //我们的条形图的间距
        val groupSpace = 0.45f

        //我们正在设置宽度
        //下面一行的酒吧。
        data.barWidth = 0.15f

        //下面的行是设置最小值
        //轴到我们的图表。
        barChart.xAxis.axisMinimum = 0f
        yAxisLeft.axisMinimum = 0f

        // below line is to
        // animate our chart.
        barChart.animate()
        //barChart!!.animateY(500)

        // below line is to group bars
        // and add spacing to it.
        barChart.groupBars(0f, groupSpace, barSpace)

        // below line is to invalidate
        // our bar chart.
        barChart.invalidate()
    }

    private fun btnclear() {
        val clean = findViewById<Button>(R.id.clean)
        clean.setOnClickListener(object : OnClickListener{
            override fun onClick(p0: View?) {
                entries!!.clear()
                entries1!!.clear()
                theDates!!.clear()
                barChart!!.clear()
            }

        })
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
    fun toMain() {
        val actionBar: ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}