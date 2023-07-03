package com.example.appdev_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.appdev_project.database.DatabaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateQuizFragment:Fragment(){
    private lateinit var spinnerAmount: Spinner
    lateinit var spinnerCategory: Spinner
    lateinit var spinnerDifficulty: Spinner
    lateinit var createButton: Button
    lateinit var editText: EditText
    private val db by viewModels<DatabaseViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_createquiz, container, false)

        spinnerAmount = view.findViewById(R.id.spinnerAmount)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty)
        createButton = view.findViewById(R.id.createQuizButton)
        editText = view.findViewById(R.id.categoryNameEditText)
        val spinnerAmountOptions = mutableListOf<Int>()


        for(i in 10..50){
            spinnerAmountOptions.add(i)
        }


        val adapterAmount = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerAmountOptions)
        adapterAmount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAmount.adapter = adapterAmount


        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.quiz_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.quiz_difficulties,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDifficulty.adapter = adapter
        }

        createButton.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    db.addQuiz(db.generateApiUrl(
                        spinnerAmount.selectedItem.toString(),
                        spinnerCategory.selectedItemPosition+9,
                        spinnerDifficulty.selectedItem.toString()),editText.text.toString())
                }
            }
        }

        return view
    }

}