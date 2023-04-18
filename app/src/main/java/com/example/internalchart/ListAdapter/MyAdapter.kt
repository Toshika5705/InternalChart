package com.example.internalchart.ListAdapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.internalchart.JsonPage.MembersLogItem
import com.example.internalchart.JsonPage.Strava
import com.example.internalchart.R
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MyAdapter(private val items: MutableList<MembersLogItem>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {


    var client_id: String = "97512"
    var redirect_uri: String = "http://www.google.com.tw"
    var code: String? = null
    var client_secret: String = "62bde77534420863a38dc9ece147e3b039293cc3"
    var access_token: String = ""
    var refresh_token: String = "5dc8e46e23f1384b2d8214d46346cf552796783c"

    var StravaToken =
        "https://www.strava.com/oauth/token?code=" + code + "&client_id=" + client_id + "&client_secret=" + client_secret + "&grant_type=authorization_code"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txt_time.text = "時間:" + item.logTime
        holder.txt_sport.text = "運動項目:" + item.machineName
        holder.distance.text = "距離:" + item.distance.toString()
        holder.calories.text = "卡路里:" + item.calories.toString()
        holder.mets.text = "Met:" + item.mets.toString()
        holder.targetSeconds.text = "目標時間:" + item.targetSeconds.toString()
        holder.hrAverage.text = "平均心率:" + item.hrAverage.toString()
        holder.statusSeconds.text = "實際時間:" + item.statusSeconds.toString()
        holder.hrMax.text = "最大心率:" + item.hrMax.toString()

        holder.share.setOnClickListener(object : OnClickListener {
            override fun onClick(p0: View?) {
                val context = holder.itemView.context

                //Usercode(context)
                Retoken()
                UpStrava(context,item.logTime,item.statusSeconds,item.machineName,item.distance)

            }
        })


    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt_time: TextView = itemView.findViewById(R.id.txt_time)
        val txt_sport: TextView = itemView.findViewById(R.id.txt_sport)
        val distance: TextView = itemView.findViewById(R.id.distance)
        val calories: TextView = itemView.findViewById(R.id.calories)
        val mets: TextView = itemView.findViewById(R.id.mets)
        val targetSeconds: TextView = itemView.findViewById(R.id.targetSeconds)
        val hrAverage: TextView = itemView.findViewById(R.id.hrAverage)
        val statusSeconds: TextView = itemView.findViewById(R.id.statusSeconds)
        val hrMax: TextView = itemView.findViewById(R.id.hrMax)
        val share: Button = itemView.findViewById(R.id.btn_2)
    }

    //登入使用者取code()失敗
    fun Usercode(context: Context) {
        Thread {
            try {
                val Stravalogin =
                    URL("https://www.strava.com/oauth/authorize?client_id=" + client_id + "&response_type=code&redirect_uri=" + redirect_uri + "&approval_prompt=force&scope=activity:write,activity:read_all")
                //GET 創建 Intent 對象，並設置 Action 和 Uri
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Stravalogin.toString()))
                // 啟動 Activity
                context.startActivity(intent)

                val connection = Stravalogin.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                Log.d("responseCode", responseCode.toString())
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val url = connection.url.toString()
                    val uri = Uri.parse(url)
                    code = uri.getQueryParameter("code")
                    Log.d("code1", code.toString())
                    if (code != null) {
                        Log.d("code2", code.toString())
                        (context as Activity).runOnUiThread {
                            Toast.makeText(context, "Strava code: $code", Toast.LENGTH_SHORT).show()
                            context.finish()
                        }
                    }

                } else {
                    (context as Activity).runOnUiThread {
                        Toast.makeText(context, "Unable to connect to Strava", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
        Thread.interrupted()

    }

    //抓access_token
    fun Retoken() {
        //refresh
        Thread {
            val refreshurl = URL("https://www.strava.com/api/v3/oauth/token")
            val connection = refreshurl.openConnection() as HttpURLConnection
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.requestMethod = "POST"
            connection.doOutput = true

            // 設定 POST 參數
            val postData =
                "client_id=${client_id}&client_secret=${client_secret}&grant_type=${"refresh_token"}&refresh_token=${refresh_token}"
            val output = OutputStreamWriter(connection.outputStream)
            output.write(postData)
            output.flush()
            output.close()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use(BufferedReader::readText)
                val token = Gson().fromJson(response, Strava::class.java)
                access_token = token.access_token
                Log.d("token", "Response: $responseCode")

            } else {
                Log.e("token", "POST refresh code: $responseCode")
            }
        }.start()

        Thread.interrupted()

    }

    //上傳Strava
    fun UpStrava(
        context: Context,
        logTime: String,
        statusSeconds: Int,
        machineName: String,
        distance: Int
    ) {
        Thread {
            try {


                val StravaUp = URL("https://www.strava.com/api/v3/activities")

                val connection = StravaUp.openConnection() as HttpURLConnection
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                connection.requestMethod = "POST"
                connection.doOutput = true

                // 設定 POST 參數
                val postData =
                    "name=${"T_SoX"}&type=${"Ride"}&sport_type=${"Ride"}&start_date_local=${logTime}&elapsed_time=${statusSeconds}&description=${machineName}&distance=${distance}&trainer=${false}&commute=${false}&access_token=${access_token}"
                val output = OutputStreamWriter(connection.outputStream)
                output.write(postData)
                output.flush()
                output.close()

                val responseCode = connection.responseCode
                //200 201
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response =
                        connection.inputStream.bufferedReader().use(BufferedReader::readText)
                    Log.d("TAG", "Response: $response")
                } else {
                    Log.e("TAG", "POST request failed with error code: $responseCode")
                    if (responseCode == 201) {
                        (context as Activity).runOnUiThread {
                            Toast.makeText(context, "成功上傳", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                connection.disconnect()


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()

        Thread.interrupted()
    }


}

