package com.example.appdev_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.appdev_project.database.QuestionsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class StartFragment : Fragment() {
    lateinit var btn_start : Button
    lateinit var btn_create : Button
    lateinit var btn_delete : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        btn_start = view.findViewById(R.id.btn_start)
        btn_create = view.findViewById(R.id.createQuiz)
        btn_delete = view.findViewById(R.id.deleteDB)
        btn_start.setOnClickListener{
                view.findNavController().navigate(R.id.gameFragment)
        }
        btn_create.setOnClickListener{
            view.findNavController().navigate(R.id.createQuizFragment)
        }
        btn_delete.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    QuestionsDatabase.getDB(context!!.applicationContext).questionsDao().deleteAllRows()
                }
            }
        }


        return view
    }
}