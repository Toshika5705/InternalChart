package com.example.internalchart.Logs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.internalchart.JsonPage.MembersLog
import com.example.internalchart.JsonPage.Users
import com.example.internalchart.ListAdapter.MyAdapter
import com.example.internalchart.MainActivity
import com.example.internalchart.R
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MembersList : AppCompatActivity() {

    lateinit var spinner1: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.memberslist)

        spinner1 = findViewById<Spinner>(R.id.spinner)

        urls()

        //回首頁
        toMain()

    }
    //網路JSON接收
    fun urls() {

        val memberidurl =
            "http://192.168.1.203:5000/api/v1/ApplicationUser/User/d6d8cbd2-9f5f-4b54-bd17-ebb82bbd701a"

        Thread(label@ Runnable {
            try {
                val url = URL(memberidurl)
                val connection = url.openConnection() as HttpURLConnection

                //api 200 通過
                if (connection.responseCode == 200) {
                    val Tois = connection.inputStream
                    val bufferedReader =
                        BufferedReader(InputStreamReader(Tois))
                    var output: String?
                    val sb = StringBuilder()
                    while (bufferedReader.readLine().also { output = it } != null) {
                        sb.append(output)
                    }
                    val users: Users = Gson().fromJson(sb.toString(), Users::class.java)
                    if (users.size == 0) {
                        Log.d("EEOR", "400")
                        return@Runnable
                    }
                    usersUI(users)
                    bufferedReader.close()
                    Tois.close()
                } else {
                    Log.d("EEOR", "500")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
        // 當子線程執行完畢後，釋放子線程
        Thread.interrupted()
    }
    //下拉
    private fun usersUI(users: Users) {
        runOnUiThread {

            //設定陣列[]
            val usersList: ArrayList<String> = ArrayList<String>()
            val size: Int = users.size
            // 訪問 JSON 陣列中所有 JSON 物件。
            for (i in 0 until size) {
                usersList.add(users.get(i).memberid)
            }
            val dapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
                applicationContext, android.R.layout.simple_spinner_item, usersList as List<Any?>
            )
            spinner1.setAdapter(dapter)

            spinner1.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val memberID = users.get(p2).memberid.trim()
                    make(memberID)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
        }

    }

    //刷新動作+圖表
    fun make(memberID: String) {

        val recyclerView : RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        Thread {
            try {
                val memberidurl = URL("http://192.168.1.203:5000/api/v1/MembersLog/Log/NTkxOTE0ODlCRjZBNDlGRURCMTIzMDQ0QTRBQUFDMEE=/" + memberID).readText()
                //Log.d("url",memberidurl)

                val membersLog: MembersLog = Gson().fromJson(memberidurl, MembersLog::class.java)

                runOnUiThread {
                    // 創建 Adapter 對象
                    recyclerView.apply {
                        adapter = MyAdapter(membersLog)
                    }
                    //下拉刷新
                    val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
                    swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.blue_RURI))
                    swipeRefreshLayout.setOnRefreshListener {
                        membersLog.clear()
                        recyclerView.adapter!!.notifyDataSetChanged()
                        val intent = Intent(this, MembersList::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        swipeRefreshLayout.isRefreshing = false
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
        // 當子線程執行完畢後，釋放子線程
        Thread.interrupted()
    }

    //按鍵
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == android.R.id.home) {
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

