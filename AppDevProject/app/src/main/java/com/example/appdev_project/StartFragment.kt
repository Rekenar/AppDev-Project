package com.example.appdev_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController


class StartFragment : Fragment() {
     lateinit var btn_start : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        btn_start = view.findViewById(R.id.btn_start)
        btn_start.setOnClickListener{
                view.findNavController().navigate(R.id.gameFragment)
        }

        return view
    }
}