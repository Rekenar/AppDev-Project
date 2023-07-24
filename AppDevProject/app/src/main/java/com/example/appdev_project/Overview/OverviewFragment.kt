package com.example.appdev_project.Overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdev_project.Categories.CategoriesAdapter
import com.example.appdev_project.Categories.CategoriesFragmentDirections
import com.example.appdev_project.ItemsViewModel

import com.example.appdev_project.R
import com.example.appdev_project.database.DatabaseCompanionObject
import com.example.appdev_project.database.Question
import com.example.appdev_project.database.QuestionsDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class OverviewFragment : Fragment() {
    private lateinit var questionsView: RecyclerView
    private lateinit var data : ArrayList<Question>
    private lateinit var adapter: OverviewAdapter
    private lateinit var play: FloatingActionButton
    private val args: OverviewFragmentArgs by navArgs()
    private lateinit var db: QuestionsDatabase
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
        val identifier = args.identifier
        println("id is: " + identifier)

        var header = view.findViewById<TextView>(R.id.overViewHeader)
        questionsView = view.findViewById(R.id.questionsView)
        questionsView.layoutManager = LinearLayoutManager(this.context)
        play = view.findViewById(R.id.fab_play)
        data = ArrayList()



        try {
            db = DatabaseCompanionObject.buildDatabase(requireContext())

            lifecycleScope.launch {
                val fetchedData = withContext(Dispatchers.IO){
                    ArrayList(db.questionsDao().getQuestions(identifier))
                }
                header.text = fetchedData[0].category
                data = fetchedData
                adapter = OverviewAdapter(data)
                questionsView.adapter = adapter
            }
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            db.close()
        }


        play.setOnClickListener {
            val action = OverviewFragmentDirections.actionOverviewFragmentToGameFragment(identifier)
            this.findNavController().navigate(action)
        }

    }
}