package com.example.mygame.utilities

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.mygame.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UIManager(private val activity: Activity) {
    private val gameArea: RelativeLayout = activity.findViewById(R.id.game_area)
    private val car: ImageView = activity.findViewById(R.id.car_id)
    private val hearts = arrayOf(
        activity.findViewById<ImageView>(R.id.heart1),
        activity.findViewById<ImageView>(R.id.heart2),
        activity.findViewById<ImageView>(R.id.heart3)
    )
    private val scoreTextView: TextView = activity.findViewById(R.id.score_text_view)
    private val buttonLeft: FloatingActionButton = activity.findViewById(R.id.left_button_id)
    private val buttonRight: FloatingActionButton = activity.findViewById(R.id.right_button_id)

    private val lanes = arrayOf(
        activity.findViewById<View>(R.id.lane_left_outer),
        activity.findViewById<View>(R.id.lane_left),
        activity.findViewById<View>(R.id.lane_center),
        activity.findViewById<View>(R.id.lane_right),
        activity.findViewById<View>(R.id.lane_right_outer)
    )

    fun getGameArea(): RelativeLayout = gameArea

    fun setMoveLeftListener(listener: () -> Unit) {
        buttonLeft.setOnClickListener { listener() }
    }

    fun setMoveRightListener(listener: () -> Unit) {
        buttonRight.setOnClickListener { listener() }
    }

    fun updateCarPosition(currentLane: Int) {
        val lane = lanes[currentLane]
        car.x = lane.x + (lane.width - car.width) / 2
    }

    fun updateHearts(lives: Int) {
        hearts.forEachIndexed { index, heart ->
            heart.visibility = if (index < lives) View.VISIBLE else View.INVISIBLE
        }
    }

    fun updateScore(score: Int, coinScore: Int) {
        val totalScore = score + coinScore
        scoreTextView.text = totalScore.toString()
    }

    fun setSensorMode(isSensorMode: Boolean) {
        val buttonsVisibility = if (isSensorMode) View.GONE else View.VISIBLE
        buttonLeft.visibility = buttonsVisibility
        buttonRight.visibility = buttonsVisibility
    }
    fun getCar(): ImageView = car
    fun getButtonLeft(): FloatingActionButton = buttonLeft

    fun isButtonMode(): Boolean {
        return buttonLeft.visibility == View.VISIBLE
    }


}