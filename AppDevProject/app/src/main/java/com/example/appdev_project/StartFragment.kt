package com.example.appdev_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.findNavController

class StartFragment : Fragment() {
     lateinit var btn_start : Button
     lateinit var btn_achievements : ImageButton
    lateinit var createButton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_start = view.findViewById(R.id.btn_start)
        btn_start.setOnClickListener{ view.findNavController().navigate(R.id.categoriesFragment) }

        createButton = view.findViewById(R.id.createButton)
        createButton.setOnClickListener {
            view.findNavController().navigate(R.id.createQuizFragment)
        }


        btn_achievements = view.findViewById(R.id.btn_Achievements)
        btn_achievements.setOnClickListener { view.findNavController().navigate(R.id.achievementFragment) }
    }
}