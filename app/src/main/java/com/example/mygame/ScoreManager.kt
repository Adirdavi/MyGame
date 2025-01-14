package com.example.mygame

import android.content.Context
import com.example.mygame.models.PlayerScore

class ScoreManager(private val context: Context) {
    private val PREFS_NAME = "GameScores"
    private val SCORES_KEY = "scores"
    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveScore(playerScore: PlayerScore) {

        val scores = getScores().toMutableList()


        scores.add(playerScore)


        scores.sortByDescending { it.score }


        val topScores = scores.take(10)


        sharedPreferences.edit().apply {
            putString(SCORES_KEY, convertToString(topScores))
            apply()
        }
    }

    fun getScores(): List<PlayerScore> {

        val scoresStr = sharedPreferences.getString(SCORES_KEY, "") ?: ""


        return if (scoresStr.isEmpty()) listOf() else convertFromString(scoresStr)
    }


    private fun convertToString(scores: List<PlayerScore>): String {
        return scores.joinToString(";") { score ->
            "${score.playerName},${score.score},${score.latitude},${score.longitude}"
        }
    }


    private fun convertFromString(str: String): List<PlayerScore> {
        return str.split(";")
            .filter { it.isNotEmpty() }
            .mapNotNull { scoreStr ->

                try {
                    val parts = scoreStr.split(",")
                    PlayerScore(
                        playerName = parts[0],
                        score = parts[1].toInt(),
                        latitude = parts[2].toDouble(),
                        longitude = parts[3].toDouble()
                    )
                } catch (e: Exception) {
                    null
                }
            }
    }

    fun clearScores() {
        sharedPreferences.edit().apply {
            remove(SCORES_KEY)
            apply()
        }
    }


    fun getScoreCount(): Int {
        return getScores().size
    }
}