package com.example.appdev_project.Achievements

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appdev_project.database.DatabaseCompanionObject
import com.example.appdev_project.database.QuestionsDatabase
import com.example.appdev_project.databinding.FragmentAchievementBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AchievementFragment : Fragment() {
    private lateinit var binding: FragmentAchievementBinding
    private lateinit var adapter: AchievementAdapter
    private lateinit var db: QuestionsDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentAchievementBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.achievementsView.layoutManager = LinearLayoutManager(this.context)

        lifecycleScope.launch {
            try {
                db = DatabaseCompanionObject.buildDatabase(requireContext())

                val fetchedData = withContext(Dispatchers.IO) {
                    ArrayList(db.achievementsDAO().getAllAchievements())
                }
                adapter = AchievementAdapter(fetchedData)
                binding.achievementsView.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (::db.isInitialized && db.isOpen) {
                    db.close()
                }
            }
        }
    }


}