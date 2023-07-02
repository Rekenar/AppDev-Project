package com.example.appdev_project.Overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdev_project.Categories.CategoriesAdapter
import com.example.appdev_project.ItemsViewModel

import com.example.appdev_project.R


class OverviewFragment : Fragment() {
    private lateinit var questionsView: RecyclerView
    private lateinit var data : ArrayList<ItemsViewModel>
    private lateinit var adapter: OverviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionsView = view.findViewById(R.id.questionsView)
        questionsView.layoutManager = LinearLayoutManager(this.context)
        data = ArrayList()
        for (i in 1..20) { //This need to be linked to the DB to get the right amount of elements/achievements
            data.add(ItemsViewModel( "Item " + i))  //Here goes the achievement name
        }

        adapter = OverviewAdapter(data)
        questionsView.adapter = adapter

        /**
        lifecycleScope.launch {
        withContext(Dispatchers.IO) {
        println(QuestionsDatabase.getDB(requireContext()).questionsDao().getQuestions(1))
        }

        }
         */

    }
}