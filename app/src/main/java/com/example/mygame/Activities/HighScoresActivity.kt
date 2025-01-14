// HighScoresActivity.kt
package com.example.mygame.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mygame.R
import com.example.mygame.fragments.HighScoreFragment
import com.example.mygame.fragments.MapFragment
import com.example.mygame.interfaces.Callback_HighScoreItemClicked

class HighScoresActivity : AppCompatActivity() {
    private lateinit var mapFragment: MapFragment
    private lateinit var highScoreFragment: HighScoreFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)


        mapFragment = MapFragment()
        highScoreFragment = HighScoreFragment()


        highScoreFragment.highScoreItemClicked = object : Callback_HighScoreItemClicked {
            override fun highScoreItemClicked(lat: Double, lon: Double) {
                mapFragment.zoom(lat, lon)
            }
        }


        supportFragmentManager.beginTransaction()
            .add(R.id.high_scores_frame_list, highScoreFragment)
            .add(R.id.scores_frame_map, mapFragment)
            .commit()
    }
}