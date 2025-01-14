package com.example.mygame.utilities

import android.content.Context
import android.media.MediaPlayer
import com.example.mygame.R

class SoundManager(context: Context) {
    private val crashSound: MediaPlayer = MediaPlayer.create(context, R.raw.car_crash_sound)
    private val coinCollectSound: MediaPlayer = MediaPlayer.create(context, R.raw.coin_sound)

    fun playCrashSound() {
        crashSound.seekTo(0)
        crashSound.start()
    }

    fun playCoinSound() {
        coinCollectSound.seekTo(0)
        coinCollectSound.start()
    }

    fun release() {
        crashSound.release()
        coinCollectSound.release()
    }
}