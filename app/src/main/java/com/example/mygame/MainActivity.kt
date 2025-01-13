package com.example.mygame

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.random.Random

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var gameArea: RelativeLayout
    private lateinit var laneLeftOuter: View
    private lateinit var laneLeft: View
    private lateinit var laneCenter: View
    private lateinit var laneRight: View
    private lateinit var laneRightOuter: View
    private lateinit var car: ImageView
    private lateinit var buttonLeft: FloatingActionButton
    private lateinit var buttonRight: FloatingActionButton

    private lateinit var heart1: ImageView
    private lateinit var heart2: ImageView
    private lateinit var heart3: ImageView

    private lateinit var scoreTextView: TextView

    private var currentLane: Int = 2
    private val handler = Handler(Looper.getMainLooper())
    private val random = Random.Default
    private var lives = 3
    private var score = 0
    private var coinScore = 0
    private var firstObstaclePassed = false

    private val rows = 8
    private val cols = 5
    private val obstaclesMatrix = Array(rows) { arrayOfNulls<ImageView?>(cols) }
    private val coinsMatrix = Array(rows) { arrayOfNulls<ImageView?>(cols) }
    private val obstacleHistory = mutableListOf<Int>()

    private lateinit var vibrator: Vibrator
    private lateinit var crashSound: MediaPlayer
    private lateinit var coinCollectSound: MediaPlayer

    private var movingObstacles = false
    private var isCollision = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // אתחול סאונד ההתנגשות
        crashSound = MediaPlayer.create(this, R.raw.car_crash_sound)

        // אתחול סאונד איסוף המטבע
        coinCollectSound = MediaPlayer.create(this, R.raw.coin_sound)

        findViews()
        initMatrix()
        initCoins()

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

    override fun onDestroy() {
        super.onDestroy()
        crashSound.release()
        coinCollectSound.release()
    }

    private fun findViews() {
        gameArea = findViewById(R.id.game_area)
        laneLeftOuter = findViewById(R.id.lane_left_outer)
        laneLeft = findViewById(R.id.lane_left)
        laneCenter = findViewById(R.id.lane_center)
        laneRight = findViewById(R.id.lane_right)
        laneRightOuter = findViewById(R.id.lane_right_outer)
        car = findViewById(R.id.car_id)
        buttonLeft = findViewById(R.id.left_button_id)
        buttonRight = findViewById(R.id.right_button_id)

        heart1 = findViewById(R.id.heart1)
        heart2 = findViewById(R.id.heart2)
        heart3 = findViewById(R.id.heart3)
        scoreTextView = findViewById(R.id.score_text_view)
    }

    private fun createGameElement(size: Int, drawableId: Int): ImageView {
        return ImageView(this).apply {
            setImageResource(drawableId)
            layoutParams = RelativeLayout.LayoutParams(size, size)
            visibility = View.INVISIBLE
        }
    }

    private fun initMatrix() {
        gameArea.post {
            val laneWidth = gameArea.width / cols
            val cellHeight = (gameArea.height - buttonLeft.height) / rows

            val carWidth = car.width
            val carHeight = car.height

            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    val obstacle = createGameElement(carWidth, R.drawable.obstacle)
                    gameArea.addView(obstacle)
                    obstaclesMatrix[i][j] = obstacle

                    obstacle.x = (j * laneWidth) + (laneWidth - carWidth) / 2f
                    obstacle.y = i * cellHeight.toFloat() + (cellHeight - carHeight) / 2f
                }
            }
        }
    }

    private fun initCoins() {
        gameArea.post {
            val laneWidth = gameArea.width / cols
            val cellHeight = (gameArea.height - buttonLeft.height) / rows
            val coinSize = (car.width * 0.6).toInt()

            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    val coin = createGameElement(coinSize, R.drawable.coin)
                    gameArea.addView(coin)
                    coinsMatrix[i][j] = coin

                    coin.x = (j * laneWidth) + (laneWidth - coinSize) / 2f
                    coin.y = i * cellHeight.toFloat() + (cellHeight - coinSize) / 2f
                }
            }
        }
    }

    private fun generateNewElements() {
        // ניקוי השורה העליונה
        for (j in 0 until cols) {
            obstaclesMatrix[0][j]?.visibility = View.INVISIBLE
            coinsMatrix[0][j]?.visibility = View.INVISIBLE
        }

        // יצירת מכשול
        var obstacleCol: Int
        do {
            obstacleCol = random.nextInt(cols)
        } while (obstacleHistory.count { it == obstacleCol } >= 2)

        obstaclesMatrix[0][obstacleCol]?.visibility = View.VISIBLE
        obstacleHistory.add(obstacleCol)
        if (obstacleHistory.size > 2) {
            obstacleHistory.removeAt(0)
        }

        // יצירת מטבע (אם אפשר)
        if (random.nextInt(2) == 0) {
            val availableColumns = (0 until cols).filter { it != obstacleCol }
            if (availableColumns.isNotEmpty()) {
                val coinCol = availableColumns[random.nextInt(availableColumns.size)]
                coinsMatrix[0][coinCol]?.visibility = View.VISIBLE
            }
        }
    }

    private fun moveObstaclesDown() {
        if (movingObstacles) return
        movingObstacles = true
        isCollision = false

        var scoreUpdated = false
        var hasObstacleInLastRow = false

        // בדיקה אם יש מכשול בשורה האחרונה
        for (j in 0 until cols) {
            if (obstaclesMatrix[rows - 1][j]?.visibility == View.VISIBLE) {
                hasObstacleInLastRow = true
                break
            }
        }

        // הזזת אלמנטים למטה
        for (i in rows - 1 downTo 1) {
            for (j in 0 until cols) {
                // הזזת מכשולים
                obstaclesMatrix[i][j]?.visibility = obstaclesMatrix[i - 1][j]?.visibility ?: View.INVISIBLE

                // הזזת מטבעות רק אם אין מכשול
                if (obstaclesMatrix[i][j]?.visibility != View.VISIBLE) {
                    coinsMatrix[i][j]?.visibility = coinsMatrix[i - 1][j]?.visibility ?: View.INVISIBLE
                } else {
                    coinsMatrix[i][j]?.visibility = View.INVISIBLE
                }

                // בדיקת התנגשות בשורה של הרכב
                if (i == rows - 2 && j == currentLane && obstaclesMatrix[i][j]?.visibility == View.VISIBLE) {
                    isCollision = true
                }
            }
        }

        // עדכון firstObstaclePassed רק אם המכשול הראשון עבר בלי התנגשות
        if (hasObstacleInLastRow && !isCollision) {
            firstObstaclePassed = true
            // מוסיפים ניקוד על מעבר מכשול בהצלחה
            if (!scoreUpdated) {
                score++
                scoreUpdated = true
            }
        }

        generateNewElements()
        checkCollisions()
        checkCoinCollection()

        handler.postDelayed({
            movingObstacles = false
        }, 500)
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
        if (currentLane < cols - 1) {
            currentLane++
            updateCarPosition()
        } else {
            Toast.makeText(this, "Already in the rightmost lane", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCarPosition() {
        val lane = when (currentLane) {
            0 -> laneLeftOuter
            1 -> laneLeft
            2 -> laneCenter
            3 -> laneRight
            else -> laneRightOuter
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

    private fun checkCoinCollection() {
        val coin = coinsMatrix[rows - 1][currentLane]
        if (coin?.visibility == View.VISIBLE) {
            coin.visibility = View.INVISIBLE
            coinScore++

            // הפעלת סאונד איסוף המטבע
            coinCollectSound.seekTo(0)  // חזרה לתחילת הסאונד
            coinCollectSound.start()
        }
        updateScore()  // מעדכנים את התצוגה בנפרד
    }

    private fun handleCollision() {
        lives--
        isCollision = true

        // הפעלת סאונד ההתנגשות
        crashSound.seekTo(0)  // חזרה לתחילת הסאונד
        crashSound.start()

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

    private fun updateScore() {
        val totalScore = score + coinScore
        scoreTextView.text = "$totalScore"
    }
}