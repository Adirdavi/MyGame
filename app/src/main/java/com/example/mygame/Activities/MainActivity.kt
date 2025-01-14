package com.example.mygame.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mygame.R
import com.example.mygame.utilities.ScoreManager
import com.example.mygame.interfaces.GameOverListener
import com.example.mygame.interfaces.TiltCallback
import com.example.mygame.models.PlayerScore
import com.example.mygame.utilities.GameManager
import com.example.mygame.utilities.SoundManager
import com.example.mygame.utilities.TiltDetector
import com.example.mygame.utilities.UIManager
import com.example.mygame.utilities.VibrationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), GameOverListener {
    private lateinit var gameManager: GameManager
    private lateinit var uiManager: UIManager
    private lateinit var soundManager: SoundManager
    private lateinit var vibrationManager: VibrationManager
    private lateinit var scoresManager: ScoreManager
    private lateinit var playerName: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var tiltDetector: TiltDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initializeManagers()
        setupGame()
        requestLocationPermissions()
    }

    private fun initializeManagers() {
        soundManager = SoundManager(this)
        uiManager = UIManager(this)
        vibrationManager = VibrationManager(this)
        scoresManager = ScoreManager(this)

        gameManager = GameManager(
            context = this,
            uiManager = uiManager,
            soundManager = soundManager,
            vibrationManager = vibrationManager,
            gameOverListener = this
        )
    }

    private fun setupGame() {
        playerName = intent.getStringExtra(PLAYER_NAME_KEY).orEmpty()
        val isSensorMode = intent.getBooleanExtra(GAME_MODE_KEY, false)
        val difficulty = intent.getStringExtra(DIFFICULTY_KEY) ?: DEFAULT_DIFFICULTY

        if (isSensorMode) {
            tiltDetector = TiltDetector(this, object : TiltCallback {
                override fun tiltX(direction: Int) {
                    runOnUiThread {
                        gameManager.handleTilt(direction)
                    }
                }
            })
        }

        gameManager.setupGame(
            playerName = playerName,
            isSensorMode = isSensorMode,
            difficulty = difficulty
        )
    }

    override fun onGameOver(score: Int) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    val playerScore = PlayerScore(
                        playerName = playerName,
                        score = score,
                        latitude = location?.latitude ?: DEFAULT_LATITUDE,
                        longitude = location?.longitude ?: DEFAULT_LONGITUDE
                    )

                    scoresManager.saveScore(playerScore)
                    navigateToHighScores()
                }
                .addOnFailureListener {
                    saveScoreWithDefaultLocation(score)
                }
        } else {
            saveScoreWithDefaultLocation(score)
        }
    }

    private fun saveScoreWithDefaultLocation(score: Int) {
        val playerScore = PlayerScore(
            playerName = playerName,
            score = score,
            latitude = DEFAULT_LATITUDE,
            longitude = DEFAULT_LONGITUDE
        )
        scoresManager.saveScore(playerScore)
        navigateToHighScores()
    }

    private fun navigateToHighScores() {
        val intent = Intent(this, HighScoresActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onResume() {
        super.onResume()
        gameManager.resumeGame()
        tiltDetector?.start()
    }

    override fun onPause() {
        super.onPause()
        gameManager.pauseGame()
        tiltDetector?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
        tiltDetector?.stop()
    }


    companion object {
        private const val PLAYER_NAME_KEY = "playerName"
        private const val GAME_MODE_KEY = "gameMode"
        private const val DIFFICULTY_KEY = "difficulty"
        private const val DEFAULT_DIFFICULTY = "EASY"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val DEFAULT_LATITUDE = 32.0853
        private const val DEFAULT_LONGITUDE = 34.7818
    }
}