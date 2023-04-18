package com.example.internalchart

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter


class MPChart : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mpchart)

        Line()
        toMain()
    }
    //折線圖
    fun Line(){
        val linechart = findViewById<LineChart>(R.id.lineChart)
        // X Y軸
        val entries : ArrayList<Entry>
        //副標題 項目
        val theDates : ArrayList<String>
        //X資料
        val dataset : LineDataSet

                //設定資料
        entries = ArrayList()
        entries.add(Entry(0F,4F))
        entries.add(Entry(1f,1f))
        entries.add(Entry(2f,2f))
        entries.add(Entry(3f,4f))
        entries.add(Entry(4f,10f))
        entries.add(Entry(5f,12f))
        //設定圖表格式
        dataset = LineDataSet(entries,"測試中")
        dataset.color = ContextCompat.getColor(this,R.color.red)
        dataset.lineWidth = 13f
        //點 寬比
        dataset.circleRadius = 10f
        //線 字大小
        dataset.valueTextSize = 18f
        dataset.setDrawFilled(true)
        //Line變隨性 setMode
        dataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataset.valueTextColor = ContextCompat.getColor(this,R.color.white)

        theDates = ArrayList()
        //X軸名稱
        theDates.add("十二月")
        theDates.add("一月")
        theDates.add("二月")
        theDates.add("三月")
        theDates.add("四月")
        theDates.add("五月")

        val xAxis = linechart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(theDates)
        xAxis.position = XAxis.XAxisPosition.BOTTOM


        val yAxisRight = linechart.axisRight
        yAxisRight.setEnabled(false)

        val yAxisLeft = linechart.axisLeft
        yAxisLeft.setGranularity(1f)

        // Setting Data
        val data = LineData(dataset)
        linechart.setData(data)
        linechart.animateX(2500)
        //refresh
        linechart.invalidate()
    }
    //長條圖
    /*private fun bar(){
        //主標題 項目
        val barList : ArrayList<BarEntry>
         //副標題 項目
        val theDates : ArrayList<String>
        //val theDates = java.util.ArrayList<String>()
         val barDataSet: BarDataSet
         val barData: BarData
         val barchart = findViewById<BarChart>(R.id.barChart)

        barList = ArrayList()
        barList.add(BarEntry(1f, 500f))
        barList.add(BarEntry(2f, 100f))
        barList.add(BarEntry(3f, 300f))
        barList.add(BarEntry(4f, 800f))
        barList.add(BarEntry(5f, 400f))
        barList.add(BarEntry(6f, 1000f))
        barList.add(BarEntry(7f, 800f))

        barDataSet = BarDataSet(barList, "")

        theDates = ArrayList()
        //第一個自動消失
        theDates.add("")
        theDates.add("標題一")
        theDates.add("標題二")
        theDates.add("標題三")
        theDates.add("標題四")
        theDates.add("標題五")
        theDates.add("標題六")
        theDates.add("標題七")

        //副標題 字串寫法 MP版本3.0.0後
        barchart.xAxis.valueFormatter = IndexAxisValueFormatter(theDates)
        barchart.animateY(500);

        barchart.getXAxis().setGranularityEnabled(true);
        barchart.getXAxis().setGranularity(0.0f);
        barchart.getXAxis().setLabelCount(barDataSet.getEntryCount());

        //輸出
        barData = BarData(barDataSet)
        barchart.data = barData
        barchart.invalidate()

        //上下方標題名
        barchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

       barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 250)

        barDataSet.valueTextColor=Color.BLACK
        barDataSet.valueTextSize=15f
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


