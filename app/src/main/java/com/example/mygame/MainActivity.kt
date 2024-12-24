package com.example.mygame

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.random.Random

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var gameArea: RelativeLayout
    private lateinit var laneLeft: View
    private lateinit var laneCenter: View
    private lateinit var laneRight: View
    private lateinit var car: ImageView
    private lateinit var buttonLeft: FloatingActionButton
    private lateinit var buttonRight: FloatingActionButton

    private lateinit var heart1: ImageView
    private lateinit var heart2: ImageView
    private lateinit var heart3: ImageView

    private var currentLane: Int = 1
    private val handler = Handler(Looper.getMainLooper())
    private val random = Random.Default
    private var lives = 3

    private val rows = 5
    private val cols = 3
    private val obstaclesMatrix = Array(rows) { arrayOfNulls<ImageView?>(cols) }
    private val obstacleHistory = mutableListOf<Int>()

    private lateinit var vibrator: Vibrator

    private var movingObstacles = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()
        initMatrix()

        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        buttonLeft.setOnClickListener { moveCarLeft() }
        buttonRight.setOnClickListener { moveCarRight() }

        startObstacleMovement()
    }

    override fun onResume() {
        super.onResume()
        startObstacleMovement()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    private fun findViews() {
        gameArea = findViewById(R.id.game_area)
        laneLeft = findViewById(R.id.lane_left)
        laneCenter = findViewById(R.id.lane_center)
        laneRight = findViewById(R.id.lane_right)
        car = findViewById(R.id.car_id)
        buttonLeft = findViewById(R.id.left_button_id)
        buttonRight = findViewById(R.id.right_button_id)

        heart1 = findViewById(R.id.heart1)
        heart2 = findViewById(R.id.heart2)
        heart3 = findViewById(R.id.heart3)
    }

    private fun initMatrix() {
        gameArea.post {
            val laneWidth = gameArea.width / 3
            val cellHeight = (gameArea.height - buttonLeft.height) / rows

            val carWidth = car.width
            val carHeight = car.height

            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    val obstacle = ImageView(this).apply {
                        setImageResource(R.drawable.obstacle)
                        layoutParams = RelativeLayout.LayoutParams(
                            carWidth,
                            carHeight
                        )
                        visibility = View.INVISIBLE
                    }
                    gameArea.addView(obstacle)
                    obstaclesMatrix[i][j] = obstacle

                    obstacle.x = (j * laneWidth) + (laneWidth * 0.25).toFloat()
                    obstacle.y = i * cellHeight.toFloat() + (cellHeight * 0.25).toFloat()
                }
            }
        }
    }

    private fun generateRandomObstaclesInTopRow() {
        for (j in 0 until cols) {
            obstaclesMatrix[0][j]?.visibility = View.INVISIBLE
        }

        var randomCol: Int
        do {
            randomCol = random.nextInt(cols)
        } while (obstacleHistory.count { it == randomCol } >= 2)

        obstaclesMatrix[0][randomCol]?.visibility = View.VISIBLE
        obstacleHistory.add(randomCol)

        if (obstacleHistory.size > 2) {
            obstacleHistory.removeAt(0)
        }
    }

    private fun moveObstaclesDown() {
        if (movingObstacles) return

        movingObstacles = true

        for (i in rows - 1 downTo 1) {
            for (j in 0 until cols) {
                obstaclesMatrix[i][j]?.visibility = obstaclesMatrix[i - 1][j]?.visibility ?: View.INVISIBLE
            }
        }

        generateRandomObstaclesInTopRow()

        checkCollisions()

        handler.postDelayed({
            movingObstacles = false
        }, 1000)
    }

    private fun startObstacleMovement() {
        handler.post(object : Runnable {
            override fun run() {
                moveObstaclesDown()
                handler.postDelayed(this, 750)
            }
        })
    }

    private fun moveCarLeft() {
        if (currentLane > 0) {
            currentLane--
            updateCarPosition()
        } else {
            Toast.makeText(this, "Already in the leftmost lane", Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveCarRight() {
        if (currentLane < 2) {
            currentLane++
            updateCarPosition()
        } else {
            Toast.makeText(this, "Already in the rightmost lane", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCarPosition() {
        val lane = when (currentLane) {
            0 -> laneLeft
            1 -> laneCenter
            else -> laneRight
        }
        car.x = lane.x + (lane.width - car.width) / 2
    }

    private fun checkCollisions() {
        val carLane = currentLane

        val obstacle = obstaclesMatrix[rows - 1][carLane]
        if (obstacle?.visibility == View.VISIBLE) {
            handleCollision()
        }
    }

    private fun handleCollision() {
        lives--

        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))

        Toast.makeText(this, "Crash happened!", Toast.LENGTH_SHORT).show()

        updateHearts()

        if (lives == 0) {
            Toast.makeText(this, "Game Over!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun updateHearts() {
        when (lives) {
            0 -> heart1.visibility = View.INVISIBLE
            1 -> heart2.visibility = View.INVISIBLE
            2 -> heart3.visibility = View.INVISIBLE
        }
    }
}
