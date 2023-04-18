package com.example.internalchart.ListAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.internalchart.JsonPage.ProgramsItem
import com.example.internalchart.Logs.User_works
import com.example.internalchart.R

class VcAdpter(private val items : MutableList<ProgramsItem>) :
    RecyclerView.Adapter<VcAdpter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txt_title.text = item.title
        holder.txt_subtitle.text = item.subtitle
        holder.card.setOnClickListener(object : OnClickListener{
            override fun onClick(p0: View?) {
                val context = holder.itemView.context
                //Log.d("Json圖條",item.program)
                val intent = Intent(context, User_works::class.java).apply {
                    putExtra("Program", item.program)
                    putExtra("title",item.title)
                    putExtra("subtitle",item.subtitle)
                    putExtra("createdtime",item.createdtime)
                    putExtra("updatedtime",item.updatedtime)
                    putExtra("mets",item.mets)
                }
                context.startActivity(intent)
            }
        })
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt_title: TextView = itemView.findViewById(R.id.title)
        val txt_subtitle: TextView = itemView.findViewById(R.id.subtitle)
        val img_star: ImageView = itemView.findViewById(R.id.star)
        val card : CardView = itemView.findViewById(R.id.CardPs)
    }
}


