package com.example.appdev_project.Overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appdev_project.database.DatabaseCompanionObject
import com.example.appdev_project.database.Question
import com.example.appdev_project.database.QuestionsDatabase
import com.example.appdev_project.databinding.FragmentOverviewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class OverviewFragment : Fragment() {
    private lateinit var binding:FragmentOverviewBinding
    private lateinit var adapter: OverviewAdapter

    private val args: OverviewFragmentArgs by navArgs()


    private lateinit var db: QuestionsDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentOverviewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val identifier = args.identifier

        binding.questionsView.layoutManager = LinearLayoutManager(this.context)

        lifecycleScope.launch {
            try {
                db = DatabaseCompanionObject.buildDatabase(requireContext())

                val fetchedData = withContext(Dispatchers.IO){
                    ArrayList(db.questionsDao().getQuestions(identifier))
                }
                binding.overViewHeader.text = fetchedData[0].category
                adapter = OverviewAdapter(fetchedData)
                binding.questionsView.adapter = adapter
            } catch (e : Exception){
                e.printStackTrace()
            }finally {
                if (::db.isInitialized && db.isOpen) {
                    db.close()
                }
            }
        }


        binding.fabPlay.setOnClickListener {
            val action = OverviewFragmentDirections.actionOverviewFragmentToGameFragment(identifier)
            this.findNavController().navigate(action)
        }
    }
}