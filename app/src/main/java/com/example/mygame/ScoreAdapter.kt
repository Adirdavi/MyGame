package com.example.mygame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mygame.models.PlayerScore

class ScoresAdapter(private val onScoreClicked: (PlayerScore) -> Unit) :
    RecyclerView.Adapter<ScoresAdapter.ScoreViewHolder>() {

    private val scores = mutableListOf<PlayerScore>()

    class ScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val score_LBL_player: TextView = view.findViewById(R.id.score_LBL_player)
        private val score_LBL_score: TextView = view.findViewById(R.id.score_LBL_score)

        fun bind(score: PlayerScore, onScoreClicked: (PlayerScore) -> Unit) {
            score_LBL_player.text = score.playerName
            score_LBL_score.text = "${score.score} pts"


            itemView.setOnClickListener {
                onScoreClicked(score)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.score_item, parent, false)
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.bind(scores[position], onScoreClicked)
    }

    override fun getItemCount() = scores.size

    fun updateScores(newScores: List<PlayerScore>) {
        scores.clear()
        scores.addAll(newScores)
        notifyDataSetChanged()
    }
}