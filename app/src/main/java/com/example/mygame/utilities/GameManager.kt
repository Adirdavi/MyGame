package com.example.mygame.utilities

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.mygame.interfaces.GameOverListener
import com.example.mygame.models.GameConstants

class GameManager(
    private val context: Context,
    private val uiManager: UIManager,
    private val soundManager: SoundManager,
    private val vibrationManager: VibrationManager,
    private val gameOverListener: GameOverListener? = null
) {

    private var currentLane: Int = 2
    private var lives = 3
    private var score = 0
    private var coinScore = 0
    private var isGameRunning = false

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var gameMatrix: GameMatrix

    fun setupGame(playerName: String, isSensorMode: Boolean, difficulty: String) {
        gameMatrix = GameMatrix(
            context = context,
            gameArea = uiManager.getGameArea(),
            car = uiManager.getCar(),
            buttonLeft = uiManager.getButtonLeft()
        )

        gameMatrix.initMatrix()
        gameMatrix.initCoins()

        if (!isSensorMode) {
            uiManager.setMoveLeftListener { moveCarLeft() }
            uiManager.setMoveRightListener { moveCarRight() }
        } else {
            currentLane = GameConstants.COLS / 2
            uiManager.updateCarPosition(currentLane)
        }

        uiManager.setSensorMode(isSensorMode)

        val gameSpeed = when (difficulty) {
            "EASY" -> 1000L
            "HARD" -> 500L
            else -> 650L
        }

        startGame(gameSpeed)
    }

    private fun startGame(gameSpeed: Long) {
        isGameRunning = true
        handler.post(object : Runnable {
            override fun run() {
                if (isGameRunning) {
                    moveObstaclesDown()
                    handler.postDelayed(this, gameSpeed)
                }
            }
        })
    }

    fun pauseGame() {
        isGameRunning = false
        handler.removeCallbacksAndMessages(null)
    }

    fun resumeGame() {
        if (!isGameRunning) {
            isGameRunning = true
            startGame(750L)
        }
    }

    fun moveCarLeft() {
        if (currentLane > 0) {
            currentLane--
            uiManager.updateCarPosition(currentLane)
        } else {
            Toast.makeText(context, "Already in leftmost lane", Toast.LENGTH_SHORT).show()
        }
    }

    fun moveCarRight() {
        if (currentLane < GameConstants.COLS - 1) {
            currentLane++
            uiManager.updateCarPosition(currentLane)
        } else {
            Toast.makeText(context, "Already in rightmost lane", Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveObstaclesDown() {
        val hasObstacleNearCar = gameMatrix.moveElementsDown()
        if (hasObstacleNearCar) {
            score++
            uiManager.updateScore(score, coinScore)
        }

        val checkRow = GameConstants.ROWS - 1

        if (gameMatrix.isObstacleVisible(checkRow, currentLane)) {
            handleCollision()
        }

        if (gameMatrix.isCoinVisible(checkRow, currentLane)) {
            collectCoin()
        }

        gameMatrix.generateNewElements()
    }

    private fun handleCollision() {
        lives--
        soundManager.playCrashSound()
        vibrationManager.vibrateCrash()
        uiManager.updateHearts(lives)

        Toast.makeText(context, "Crash! Be careful!", Toast.LENGTH_SHORT).show()

        if (lives <= 0) {
            gameOver()
        }
    }

    private fun collectCoin() {
        coinScore++
        soundManager.playCoinSound()
        vibrationManager.vibrateCollect()

        val coinRow = GameConstants.ROWS - 1

        gameMatrix.hideCoin(coinRow, currentLane)
        uiManager.updateScore(score, coinScore)
    }

    private fun gameOver() {
        isGameRunning = false
        handler.removeCallbacksAndMessages(null)
        val totalScore = score + coinScore

        Toast.makeText(context, "Game Over! Score: $totalScore", Toast.LENGTH_LONG).show()

        gameOverListener?.onGameOver(totalScore)
    }

    fun handleTilt(direction: Int) {
        if (direction > 0) {
            moveCarRight()
        } else {
            moveCarLeft()
        }
    }
}