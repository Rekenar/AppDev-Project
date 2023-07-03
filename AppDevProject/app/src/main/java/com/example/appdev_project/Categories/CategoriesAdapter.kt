package com.example.appdev_project.Categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.appdev_project.R
import com.example.appdev_project.database.Category


class CategoriesAdapter(private val mList: List<Category>): RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_design,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val databaseViewModel = mList[position]

        holder.textView.text = databaseViewModel.name
        holder.itemView.setOnClickListener{
            //TODO call category with safeargs
            Navigation.createNavigateOnClickListener(R.id.overviewFragment).onClick(holder.itemView)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }


    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
    }
}
