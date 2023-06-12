package com.example.appdev_project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView


class GameFragment : Fragment() {
lateinit var questionText: TextView
lateinit var buttons :Array<Button>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        questionText = view.findViewById(R.id.txt_Question)
        buttons = arrayOf(view.findViewById(R.id.btn_Ans2),
                          view.findViewById(R.id.btn_Ans2),
                          view.findViewById(R.id.btn_Ans2),
                          view.findViewById(R.id.btn_Ans2))


        return view
    }

}