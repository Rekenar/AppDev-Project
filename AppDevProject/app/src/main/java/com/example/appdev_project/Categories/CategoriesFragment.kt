package com.example.appdev_project.Categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdev_project.ItemsViewModel
import com.example.appdev_project.R


class CategoriesFragment : Fragment() {
    private lateinit var categoriesView: RecyclerView
    private lateinit var data : ArrayList<ItemsViewModel>
    private lateinit var adapter: CategoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoriesView = view.findViewById(R.id.categoriesView)
        categoriesView.layoutManager = LinearLayoutManager(this.context)
        data = ArrayList()
        for (i in 1..20) { //This need to be linked to the DB to get the right amount of elements/achievements
            data.add(ItemsViewModel( "Item " + i))  //Here goes the achievement name
        }

        adapter = CategoriesAdapter(data)
        categoriesView.adapter = adapter
    }
}