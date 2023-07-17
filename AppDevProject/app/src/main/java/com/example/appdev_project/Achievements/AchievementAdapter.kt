package com.example.appdev_project.Achievements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.appdev_project.ItemsViewModel
import com.example.appdev_project.R
import com.example.appdev_project.database.Achievements
import com.example.appdev_project.database.Category

class AchievementAdapter(private val mList: List<Achievements>): RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_design,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val achievement = mList[position]

        holder.textView.text = achievement.name


        val colorResId = if(achievement.finished) R.color.item_color_1 else R.color.item_color_2

        val color = ContextCompat.getColor(holder.itemView.context, colorResId)
        holder.itemView.setBackgroundColor(color)
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
    }
}