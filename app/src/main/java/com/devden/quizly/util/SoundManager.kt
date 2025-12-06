package com.devden.quizly.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var soundPool: SoundPool? = null
    private var correctSoundId: Int = 0
    private var incorrectSoundId: Int = 0
    private var clickSoundId: Int = 0
    private var successSoundId: Int = 0

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    var soundEnabled = true
    var vibrationEnabled = true

    init {
        initializeSoundPool()
    }

    private fun initializeSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load sounds from raw resources (we'll create simple tone generators)
        // For now, using system sounds as placeholders
        // TODO: Add actual sound files to res/raw/
    }

    fun playCorrectSound() {
        if (soundEnabled) {
            // Play a pleasant "ding" sound for correct answers
            playTone(800, 150) // High frequency, short duration
        }
        if (vibrationEnabled) {
            vibrateSuccess()
        }
    }

    fun playIncorrectSound() {
        if (soundEnabled) {
            // Play a gentle "buzz" sound for incorrect answers
            playTone(200, 200) // Low frequency, slightly longer
        }
        if (vibrationEnabled) {
            vibrateError()
        }
    }

    fun playClickSound() {
        if (soundEnabled) {
            playTone(600, 50) // Medium frequency, very short
        }
        if (vibrationEnabled) {
            vibrateLight()
        }
    }

    fun playSuccessSound() {
        if (soundEnabled) {
            // Play an ascending tone sequence for quiz completion
            playTone(600, 100)
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                playTone(800, 100)
            }, 100)
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                playTone(1000, 150)
            }, 200)
        }
        if (vibrationEnabled) {
            vibrateSuccess()
        }
    }

    private fun playTone(frequency: Int, duration: Long) {
        // Use Android's ToneGenerator for simple sounds
        try {
            val toneGen = android.media.ToneGenerator(
                android.media.AudioManager.STREAM_MUSIC,
                80 // Volume (0-100)
            )

            // Map frequency to tone type
            val toneType = when {
                frequency > 900 -> android.media.ToneGenerator.TONE_PROP_BEEP
                frequency > 500 -> android.media.ToneGenerator.TONE_PROP_ACK
                else -> android.media.ToneGenerator.TONE_PROP_NACK
            }

            toneGen.startTone(toneType, duration.toInt())

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                toneGen.release()
            }, duration + 50)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Vibration patterns
    fun vibrateLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    50, // Duration in milliseconds
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }

    private fun vibrateSuccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 50, 50, 50) // Wait, vibrate, wait, vibrate
            val amplitudes = intArrayOf(0, 255, 0, 255) // Off, max, off, max
            vibrator.vibrate(
                VibrationEffect.createWaveform(timings, amplitudes, -1)
            )
        } else {
            @Suppress("DEPRECATION")
            val pattern = longArrayOf(0, 50, 50, 50)
            vibrator.vibrate(pattern, -1)
        }
    }

    private fun vibrateError() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 100, 50, 100) // Longer pattern for error
            val amplitudes = intArrayOf(0, 200, 0, 200) // Slightly less intense
            vibrator.vibrate(
                VibrationEffect.createWaveform(timings, amplitudes, -1)
            )
        } else {
            @Suppress("DEPRECATION")
            val pattern = longArrayOf(0, 100, 50, 100)
            vibrator.vibrate(pattern, -1)
        }
    }

    fun vibrateMedium() {
        if (!vibrationEnabled) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    100,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }

    fun vibrateHeavy() {
        if (!vibrationEnabled) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    200,
                    255 // Max amplitude
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}

