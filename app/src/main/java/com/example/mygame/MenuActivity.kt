package com.example.mygame

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        val switchGameMode: SwitchCompat = findViewById(R.id.switchGameMode)
        val difficultyLayout: LinearLayout = findViewById(R.id.difficultyLayout)
        val radioEasy: RadioButton = findViewById(R.id.radioEasy)
        val radioHard: RadioButton = findViewById(R.id.radioHard)
        val btnStartGame: Button = findViewById(R.id.btnStartGame)
        val btnHighScores: Button = findViewById(R.id.btnHighScores)
        val playerNameInput: EditText = findViewById(R.id.playerNameInput)

        listOf(radioEasy, radioHard).forEach { radioButton ->
            radioButton.setTextColor(Color.WHITE)
            radioButton.setTypeface(null, Typeface.BOLD)
        }


        fun updateDifficultyLayoutVisibility() {
            if (switchGameMode.isChecked) {

                difficultyLayout.animate().alpha(0f).setDuration(300).withEndAction {
                    difficultyLayout.visibility = View.INVISIBLE
                }
            } else {

                difficultyLayout.visibility = View.VISIBLE
                difficultyLayout.animate().alpha(1f).setDuration(300)
            }
        }


        updateDifficultyLayoutVisibility()


        switchGameMode.setOnCheckedChangeListener { _, _ ->
            updateDifficultyLayoutVisibility()
        }


        btnStartGame.setOnClickListener {
            val playerName = playerNameInput.text.toString().trim()


            if (playerName.isEmpty()) {
                playerNameInput.error = "Please enter your name"
                return@setOnClickListener
            }

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("playerName", playerName)
                putExtra("gameMode", switchGameMode.isChecked)


                if (!switchGameMode.isChecked) {
                    putExtra("difficulty", if (radioEasy.isChecked) "EASY" else "HARD")
                }
            }

            startActivity(intent)
        }


        btnHighScores.setOnClickListener {
            val intent = Intent(this, HighScoresActivity::class.java)
            startActivity(intent)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
