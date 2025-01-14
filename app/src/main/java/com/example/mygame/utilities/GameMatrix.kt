package com.example.mygame.utilities

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.example.mygame.R
import com.example.mygame.models.GameConstants
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GameMatrix(
    private val context: Context,
    private val gameArea: RelativeLayout,
    private val car: ImageView,
    private val buttonLeft: FloatingActionButton
) {
    private val obstaclesMatrix = Array(GameConstants.ROWS) { arrayOfNulls<ImageView?>(GameConstants.COLS) }
    private val coinsMatrix = Array(GameConstants.ROWS) { arrayOfNulls<ImageView?>(GameConstants.COLS) }
    private val obstacleHistory = mutableListOf<Int>()

    private fun createGameElement(size: Int, drawableId: Int): ImageView {
        return ImageView(context).apply {
            setImageResource(drawableId)
            layoutParams = RelativeLayout.LayoutParams(size, size)
            visibility = View.INVISIBLE
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    fun initMatrix() {
        gameArea.post {
            val laneWidth = gameArea.width / GameConstants.COLS
            val bottomMargin = dpToPx(60)
            val cellHeight = (gameArea.height - bottomMargin) / GameConstants.ROWS

            val carWidth = car.width
            val carHeight = car.height

            for (i in 0 until GameConstants.ROWS) {
                for (j in 0 until GameConstants.COLS) {
                    val obstacle = createGameElement(carWidth, R.drawable.obstacle)
                    gameArea.addView(obstacle)
                    obstaclesMatrix[i][j] = obstacle
                    obstacle.x = (j * laneWidth) + (laneWidth - carWidth) / 2f
                    obstacle.y = i * cellHeight.toFloat() + (cellHeight - carHeight) / 2f
                }
            }
        }
    }

    fun initCoins() {
        gameArea.post {
            val laneWidth = gameArea.width / GameConstants.COLS
            val bottomMargin = dpToPx(60)
            val cellHeight = (gameArea.height - bottomMargin) / GameConstants.ROWS
            val coinSize = (car.width * 0.6).toInt()

            for (i in 0 until GameConstants.ROWS) {
                for (j in 0 until GameConstants.COLS) {
                    val coin = createGameElement(coinSize, R.drawable.coin)
                    gameArea.addView(coin)
                    coinsMatrix[i][j] = coin
                    coin.x = (j * laneWidth) + (laneWidth - coinSize) / 2f
                    coin.y = i * cellHeight.toFloat() + (cellHeight - coinSize) / 2f
                }
            }
        }
    }

    fun moveElementsDown(): Boolean {
        var hasObstacleNearCar = false
        var hasObstacleInLastRow = false

        for (j in 0 until GameConstants.COLS) {
            if (obstaclesMatrix[GameConstants.ROWS - 1][j]?.visibility == View.VISIBLE) {
                hasObstacleInLastRow = true
            }
            if (obstaclesMatrix[GameConstants.ROWS - 2][j]?.visibility == View.VISIBLE) {
                hasObstacleNearCar = true
            }
        }

        for (i in obstaclesMatrix.size - 1 downTo 1) {
            for (j in 0 until GameConstants.COLS) {
                obstaclesMatrix[i][j]?.visibility = obstaclesMatrix[i - 1][j]?.visibility ?: View.INVISIBLE
                if (obstaclesMatrix[i][j]?.visibility != View.VISIBLE) {
                    coinsMatrix[i][j]?.visibility = coinsMatrix[i - 1][j]?.visibility ?: View.INVISIBLE
                } else {
                    coinsMatrix[i][j]?.visibility = View.INVISIBLE
                }
            }
        }

        for (j in 0 until GameConstants.COLS) {
            obstaclesMatrix[0][j]?.visibility = View.INVISIBLE
            coinsMatrix[0][j]?.visibility = View.INVISIBLE
        }

        return hasObstacleNearCar
    }

    fun generateNewElements() {
        var obstacleCol: Int
        do {
            obstacleCol = (0 until GameConstants.COLS).random()
        } while (obstacleHistory.count { it == obstacleCol } >= 2)

        obstaclesMatrix[0][obstacleCol]?.visibility = View.VISIBLE
        obstacleHistory.add(obstacleCol)
        if (obstacleHistory.size > 2) {
            obstacleHistory.removeAt(0)
        }

        if (Math.random() < 0.5) {
            val availableColumns = (0 until GameConstants.COLS).filter { it != obstacleCol }
            if (availableColumns.isNotEmpty()) {
                val coinCol = availableColumns.random()
                coinsMatrix[0][coinCol]?.visibility = View.VISIBLE
            }
        }
    }

    fun isObstacleVisible(row: Int, col: Int): Boolean = obstaclesMatrix[row][col]?.visibility == View.VISIBLE

    fun isCoinVisible(row: Int, col: Int): Boolean = coinsMatrix[row][col]?.visibility == View.VISIBLE

    fun hideCoin(row: Int, col: Int) {
        coinsMatrix[row][col]?.visibility = View.INVISIBLE
    }
}