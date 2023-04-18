package com.example.internalchart.Logs

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.internalchart.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import org.json.JSONArray
import org.json.JSONObject

class User_works : AppCompatActivity() {

    private var barchart_w: BarChart? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_works)

        barchart_w = findViewById<BarChart>(R.id.barchart_work)

        //返回鍵
        toMain()

        //收取數據傳送
        dataView()
    }

    //收取數據傳送
    fun dataView() {
        val text1: TextView = findViewById(R.id.text1)
        val text2: TextView = findViewById(R.id.text2)
        val text3: TextView = findViewById(R.id.text3)
        val text4: TextView = findViewById(R.id.text4)

        val intent = this.intent
        //Log.d("title",intent.getStringExtra("title").toString())
        //取得傳遞過來的資料
        val title = intent.getStringExtra("title")
        val subtitle = intent.getStringExtra("subtitle")
        val createdtime = intent.getStringExtra("createdtime")
        val updatedtime = intent.getStringExtra("updatedtime")
        val Program = intent.getStringExtra("Program")

        text1.setText(title)
        text2.setText(subtitle)
        text3.setText("創建時間" + "\n" + createdtime)
        text4.setText("更新時間:" + updatedtime)

        try {
            runOnUiThread {
                var dataArray : JSONArray?

                val jsonObj = JSONObject(Program)
                val target_second = jsonObj.getInt("target_second")
                //抓取program欄位
                try {
                    dataArray = jsonObj.getJSONArray("program")
                }catch (e : Exception){
                    dataArray = jsonObj.getJSONArray("programs")
                    e.printStackTrace()
                }
                val entries = mutableListOf<BarEntry>()

                for (i in 0 until dataArray!!.length()) {
                    val item = dataArray.getJSONObject(i)
                    try {
                        val item2 : JSONObject? = dataArray.getJSONObject(i+1)
                        val level = item.getInt("resistance_level")
                        val second = item.getInt("second")
                        var second2 : Int?  = item2?.getInt("second")

                        if(second2 == null){
                            second2 = target_second
                        }

                        for (i in second until second2!!) {
                            entries.add(BarEntry(i.toFloat(),level.toFloat(),second.toFloat()))
                            //entries.add(BarEntry(second.toFloat(), level.toFloat(), i))
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    try {
                        val item2 : JSONObject? = dataArray.getJSONObject(i+1)
                        val speed = item.getInt("speed")
                        val second = item.getInt("second")
                        var secondS : Int?  = item2?.getInt("second")

                        if(secondS == null){
                            secondS = target_second
                        }
                        for ( i in second until  secondS!!){
                            entries.add(BarEntry(i.toFloat(),speed.toFloat(),second.toFloat()))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val barDataSet = BarDataSet(entries, "")

                val colors = mutableListOf<Int>()
                for (entry in entries) {
                    if (entry.y <= 20) {
                        colors.add(Color.BLUE)  // 值小於 20 的設定為藍色
                    } else if (entry.y <= 40) {
                        colors.add(Color.GREEN)  // 值小於 40 的設定為綠色
                    } else if (entry.y <= 60) {
                        colors.add(ContextCompat.getColor(this, R.color.yellow))  // 小於 60 為黃色
                    } else if (entry.y <= 85) {
                        colors.add(ContextCompat.getColor(this, R.color.orange))  //小於 85 為橘色
                    }
                }
                barDataSet.colors = colors
                //barDataSet.color = Color.BLUE

                val barData = BarData(barDataSet)
                //關閉左下角圖示
                val legend = barchart_w!!.legend
                legend.isEnabled = false
                //關閉右下角文字
                barchart_w!!.description.isEnabled = false
                barchart_w!!.data = barData
                barchart_w!!.setFitBars(true)
                barchart_w!!.invalidate()
                //Log.d("barData",barData.toString())
            }
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    //按鍵
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == android.R.id.home) {
            startActivity(Intent(this, VCoach::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    //返回鍵
    private fun toMain() {
        val actionBar: ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}