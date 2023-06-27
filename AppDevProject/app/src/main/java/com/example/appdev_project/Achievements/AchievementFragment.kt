package com.example.appdev_project.Achievements

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdev_project.ItemsViewModel
import com.example.appdev_project.R


class AchievementFragment : Fragment() {
private lateinit var achievementsView: RecyclerView
private lateinit var data : ArrayList<ItemsViewModel>
private lateinit var adapter: AchievementsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_achievement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        achievementsView = view.findViewById(R.id.achievementsView)
        achievementsView.layoutManager = LinearLayoutManager(this.context)
        data = ArrayList()
        for (i in 1..20) { //This need to be linked to the DB to get the right amount of elements/achievements
            data.add(ItemsViewModel( "Item " + i))  //Here goes the achievement name
        }

        adapter = AchievementsAdapter(data)
        achievementsView.adapter = adapter
    }


}