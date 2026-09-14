package com.omniai.studio

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.omniai.studio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isImageMode = true

    private lateinit var imageGenerator: ImageGenerator
    private lateinit var videoGenerator: VideoGenerator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageGenerator = ImageGenerator(this)
        videoGenerator = VideoGenerator(this)

        setupSpinners()
        setupListeners()
    }

    private fun setupSpinners() {
        val engines = arrayOf(
            "Flux 1.1 Pro (ความละเอียดสูง)",
            "Stable Diffusion XL (เร็วพิเศษ)",
            "Midjourney V6 Style Engine",
            "Anime Diffusion Engine"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, engines)
        binding.spinnerEngine.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnTabImage.setOnClickListener {
            isImageMode = true
            binding.txtPanelTitle.text = getString(R.string.panel_title_image)
            binding.btnGenerate.text = getString(R.string.btn_generate_image)
            binding.btnTabImage.setBackgroundColor(resources.getColor(R.color.accent_purple, theme))
            binding.btnTabVideo.setBackgroundColor(resources.getColor(R.color.bg_card, theme))
            binding.imgResult.visibility = View.VISIBLE
            binding.videoResult.visibility = View.GONE
        }

        binding.btnTabVideo.setOnClickListener {
            isImageMode = false
            binding.txtPanelTitle.text = getString(R.string.panel_title_video)
            binding.btnGenerate.text = getString(R.string.btn_generate_video)
            binding.btnTabVideo.setBackgroundColor(resources.getColor(R.color.accent_purple, theme))
            binding.btnTabImage.setBackgroundColor(resources.getColor(R.color.bg_card, theme))
            binding.imgResult.visibility = View.GONE
            binding.videoResult.visibility = View.VISIBLE
        }

        binding.btnGenerate.setOnClickListener {
            val prompt = binding.etPrompt.text.toString().trim()
            if (prompt.isEmpty()) {
                Toast.makeText(this, "กรุณากรอก Prompt คำอธิบาย", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            binding.btnGenerate.isEnabled = false

            if (isImageMode) {
                val engine = binding.spinnerEngine.selectedItem.toString()
                imageGenerator.generateImage(prompt, engine, object : ImageGenerator.GenerationCallback {
                    override fun onProgress(progress: Int) {}

                    override fun onSuccess(bitmap: Bitmap) {
                        binding.progressBar.visibility = View.GONE
                        binding.btnGenerate.isEnabled = true
                        binding.imgResult.setImageBitmap(bitmap)
                        Toast.makeText(this@MainActivity, "สร้างภาพเรียบร้อยแล้ว!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(error: String) {
                        binding.progressBar.visibility = View.GONE
                        binding.btnGenerate.isEnabled = true
                        Toast.makeText(this@MainActivity, "เกิดข้อผิดพลาด: $error", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                videoGenerator.generateVideo(prompt, 5, object : VideoGenerator.VideoCallback {
                    override fun onProgress(progress: Int) {}

                    override fun onSuccess(videoPath: String) {
                        binding.progressBar.visibility = View.GONE
                        binding.btnGenerate.isEnabled = true
                        binding.videoResult.setVideoURI(Uri.parse(videoPath))
                        binding.videoResult.start()
                        Toast.makeText(this@MainActivity, "สร้างวิดีโอเรียบร้อยแล้ว!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(error: String) {
                        binding.progressBar.visibility = View.GONE
                        binding.btnGenerate.isEnabled = true
                        Toast.makeText(this@MainActivity, "เกิดข้อผิดพลาด: $error", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}
