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
import androidx.recyclerview.widget.RecyclerView
import com.example.appdev_project.R
import com.example.appdev_project.database.Category
import com.example.appdev_project.database.DatabaseCompanionObject
import com.example.appdev_project.database.QuestionsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CategoriesFragment : Fragment() {
    private lateinit var categoriesView: RecyclerView
    private lateinit var data : ArrayList<Category>
    private lateinit var adapter: CategoriesAdapter
    private lateinit var db: QuestionsDatabase
    private lateinit var toolbar: Toolbar

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


        try {
            db = DatabaseCompanionObject.buildDatabase(requireContext())

            lifecycleScope.launch {
                val fetchedData = withContext(Dispatchers.IO){
                    ArrayList(db.categoryDao().getAll())
                }
                data = fetchedData
                adapter = CategoriesAdapter(data)
                categoriesView.adapter = adapter
            }
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            db.close()
        }


        toolbar = view.findViewById(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
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
        toolbar.addView(button, params)

        // Set click listener for the button
        button.setOnClickListener {
            view?.findNavController()?.navigate(R.id.createQuizFragment)
        }
    }
}