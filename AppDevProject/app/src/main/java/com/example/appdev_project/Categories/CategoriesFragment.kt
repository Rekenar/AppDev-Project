package com.example.appdev_project.Categories


import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appdev_project.R
import com.example.appdev_project.database.DatabaseCompanionObject
import com.example.appdev_project.database.QuestionsDatabase
import com.example.appdev_project.databinding.FragmentCategoriesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CategoriesFragment : Fragment() {
    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var adapter: CategoriesAdapter
    private lateinit var db: QuestionsDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentCategoriesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.categoriesView.layoutManager = LinearLayoutManager(this.context)

        lifecycleScope.launch {
            try {
                db = DatabaseCompanionObject.buildDatabase(requireContext())

                val fetchedData = withContext(Dispatchers.IO){
                    ArrayList(db.categoryDao().getAll())
                }
                adapter = CategoriesAdapter(fetchedData)
                binding.categoriesView.adapter = adapter
            } catch (e : Exception){
            e.printStackTrace()
            }finally {
                if (::db.isInitialized && db.isOpen) {
                    db.close()
                }
            }
        }

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        addButtonToToolbar()
    }
    private fun addButtonToToolbar() {
        val button = ImageButton(requireContext())

        button.setImageResource(R.drawable.ic_button1)

        val params = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.END
        binding.toolbar.addView(button, params)

        // Set click listener for the button
        button.setOnClickListener {
            view?.findNavController()?.navigate(R.id.createQuizFragment)
        }
    }
}