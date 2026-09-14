package com.omniai.studio

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Handler
import android.os.Looper
import kotlin.random.Random

class ImageGenerator(private val context: Context) {

    interface GenerationCallback {
        fun onProgress(progress: Int)
        fun onSuccess(bitmap: Bitmap)
        fun onError(error: String)
    }

    fun generateImage(prompt: String, engine: String, callback: GenerationCallback) {
        val handler = Handler(Looper.getMainLooper())
        var progress = 0

        val runnable = object : Runnable {
            override fun run() {
                progress += 10
                callback.onProgress(progress)

                if (progress < 100) {
                    handler.postDelayed(this, 150)
                } else {
                    val bitmap = renderAbstractAiArt(prompt, engine)
                    callback.onSuccess(bitmap)
                }
            }
        }
        handler.post(runnable)
    }

    private fun renderAbstractAiArt(prompt: String, engine: String): Bitmap {
        val width = 1080
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val rand = Random(prompt.hashCode())

        // Background Gradient
        val bgPaint = Paint().apply {
            shader = RadialGradient(
                width / 2f, height / 2f, width.toFloat(),
                intArrayOf(
                    Color.rgb(rand.nextInt(100, 255), rand.nextInt(50, 150), rand.nextInt(150, 255)),
                    Color.rgb(15, 23, 42)
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Geometric AI Art Elements
        val shapePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 6f
            color = Color.argb(180, 255, 255, 255)
        }

        for (i in 0..15) {
            val cx = rand.nextFloat() * width
            val cy = rand.nextFloat() * height
            val radius = rand.nextFloat() * 300 + 50
            canvas.drawCircle(cx, cy, radius, shapePaint)
        }

        // Overlay Text Label
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 36f
            isAntiAlias = true
            setShadowLayer(8f, 0f, 0f, Color.BLACK)
        }
        canvas.drawText("OmniAI Studio: $engine", 40f, height - 60f, textPaint)

        return bitmap
    }
}
