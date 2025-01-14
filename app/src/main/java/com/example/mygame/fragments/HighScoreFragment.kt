// HighScoreFragment.kt
package com.example.mygame.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mygame.MenuActivity
import com.example.mygame.R
import com.example.mygame.ScoreManager
import com.example.mygame.ScoresAdapter
import com.example.mygame.interfaces.Callback_HighScoreItemClicked

class HighScoreFragment : Fragment() {

    private lateinit var scores_RV_table: RecyclerView
    private lateinit var scores_BTN_back: ImageButton
    private lateinit var scoresAdapter: ScoresAdapter
    private lateinit var scoresManager: ScoreManager
    var highScoreItemClicked: Callback_HighScoreItemClicked? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_high_score, container, false)

        scoresManager = ScoreManager(requireContext())

        findViews(view)
        initViews()
        loadScores()

        return view
    }

    private fun findViews(view: View) {
        scores_RV_table = view.findViewById(R.id.scores_RV_table)
        scores_BTN_back = view.findViewById(R.id.scores_BTN_back)
    }

    private fun initViews() {

        scores_BTN_back.setOnClickListener {

            val intent = Intent(requireContext(), MenuActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

            activity?.finish()
        }

        scoresAdapter = ScoresAdapter { score ->
            highScoreItemClicked?.highScoreItemClicked(score.latitude, score.longitude)
        }

        scores_RV_table.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = scoresAdapter
        }
    }

    private fun loadScores() {
        val scores = scoresManager.getScores()
        scoresAdapter.updateScores(scores)
    }
}