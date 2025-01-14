package com.example.mygame.utilities

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator

class VibrationManager(context: Context) {
    private val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun vibrateCrash() {
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun vibrateCollect() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}