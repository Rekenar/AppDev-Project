package com.example.appdev_project.Categories

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdev_project.database.DatabaseViewModel
import com.example.appdev_project.R
import com.example.appdev_project.database.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CategoriesFragment : Fragment() {
    private lateinit var categoriesView: RecyclerView
    private lateinit var data : ArrayList<Category>
    private lateinit var adapter: CategoriesAdapter
    private lateinit var db: DatabaseViewModel

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
        db = ViewModelProvider(requireActivity()).get(DatabaseViewModel::class.java)
        data = ArrayList()

        lifecycleScope.launch {
            val fetchedData = withContext(Dispatchers.IO){
                ArrayList(db.getDB().categoryDao().getAll())
            }
            data = fetchedData

        }

        adapter = CategoriesAdapter(data)
        categoriesView.adapter = adapter
        /**
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                println(QuestionsDatabase.getDB(requireContext()).questionsDao().getQuestions(1))
            }

        }
        */

    }
}