package com.example.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ToDoAdapter(val list: List<ToDoModel>): RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title:TextView = itemView.findViewById(R.id.txtShowTitle)
        val task:TextView = itemView.findViewById(R.id.txtShowTask)
        val category:TextView = itemView.findViewById(R.id.txtShowCategory)
        val date:TextView = itemView.findViewById(R.id.txtShowDate)
        val time:TextView = itemView.findViewById(R.id.txtShowTime)
        val colorTag:View = itemView.findViewById(R.id.viewColorTag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view =LayoutInflater.from(parent.context).inflate(R.layout.item_todo,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]

        with(currentItem){
            holder.title.text = title
            holder.task.text = description
            holder.category.text = category
            setDate(date,holder.date)
            setTime(time,holder.time)
            //setting up random color
            var randColor = color[Random().nextInt(color.size)]
            holder.colorTag.setBackgroundColor(randColor)

        }
    }
    override fun getItemCount(): Int {
        return list.size
    }

    private fun setTime(time: Long, tv: TextView) {
        //setting up the text
        val timeFormat = "h:mm a"
        val simpleDateFormat= SimpleDateFormat(timeFormat)
        tv.text = simpleDateFormat.format(Date(time))


    }

    private fun setDate(date: Long, tv: TextView) {
        //setting up the text
        val dateFormat = "EEE, MMM d, ''yy"
        val simpleDateFormat= SimpleDateFormat(dateFormat)
        tv.text = simpleDateFormat.format(Date(date))
    }


}