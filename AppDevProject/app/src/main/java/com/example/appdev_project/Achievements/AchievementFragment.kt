package com.example.appdev_project.Achievements

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdev_project.R
import com.example.appdev_project.database.Achievements
import com.example.appdev_project.database.DatabaseCompanionObject
import com.example.appdev_project.database.QuestionsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AchievementFragment : Fragment() {
    private lateinit var achievementsView: RecyclerView
    private lateinit var data : ArrayList<Achievements>
    private lateinit var adapter: AchievementAdapter
    private lateinit var db: QuestionsDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_achievement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        achievementsView = view.findViewById(R.id.achievementsView)
        achievementsView.layoutManager = LinearLayoutManager(this.context)

        data = ArrayList()

        try {
            db = DatabaseCompanionObject.buildDatabase(requireContext())
            lifecycleScope.launch {
                val fetchedData = withContext(Dispatchers.IO){
                    ArrayList(db.achievementsDAO().getAllAchievements())
                }
                data = fetchedData
                adapter = AchievementAdapter(data)
                achievementsView.adapter = adapter
            }
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            db.close()
        }
    }


}