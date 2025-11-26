package com.standby.mode

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.standby.mode.databinding.ActivityClockStyleBinding
import com.standby.mode.databinding.ItemClockStyleBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClockStyleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClockStyleBinding
    private lateinit var clockStyleManager: ClockStyleManager
    private lateinit var adapter: ClockStyleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClockStyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clockStyleManager = ClockStyleManager.getInstance(this)

        setupToolbar()
        setupRecyclerView()
        updatePreview(clockStyleManager.getClockStyle())
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        val styles = ClockStyleManager.ClockStyle.values().toList()
        adapter = ClockStyleAdapter(styles, clockStyleManager.getClockStyle()) { style ->
            clockStyleManager.saveClockStyle(style)
            updatePreview(style)
            adapter.updateSelectedStyle(style)
        }

        binding.stylesRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.stylesRecyclerView.adapter = adapter
        
        // Setup Next button
        binding.nextButton.setOnClickListener {
            val intent = android.content.Intent(this, ClockLandscapePreviewActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updatePreview(style: ClockStyleManager.ClockStyle) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        binding.previewClock.text = currentTime

        applyClockStyle(binding.previewClock, style)
    }

    private fun applyClockStyle(textView: android.widget.TextView, style: ClockStyleManager.ClockStyle) {
        when (style) {
            ClockStyleManager.ClockStyle.DIGITAL -> {
                textView.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
                textView.textSize = 64f
                textView.setTextColor(ContextCompat.getColor(this, R.color.white))
                textView.setShadowLayer(0f, 0f, 0f, 0)
            }
            ClockStyleManager.ClockStyle.BOLD -> {
                textView.typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
                textView.textSize = 70f
                textView.setTextColor(ContextCompat.getColor(this, R.color.white))
                textView.setShadowLayer(0f, 0f, 0f, 0)
            }
            ClockStyleManager.ClockStyle.BOLD_SQUARE -> {
                textView.typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
                textView.textSize = 70f
                textView.letterSpacing = 0.12f
                textView.setTextColor(ContextCompat.getColor(this, R.color.white))
                textView.setShadowLayer(0f, 0f, 0f, 0)
            }
            ClockStyleManager.ClockStyle.BOLD_CONDENSED -> {
                textView.typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
                textView.textSize = 70f
                textView.letterSpacing = -0.03f
                textView.setTextColor(ContextCompat.getColor(this, R.color.white))
                textView.setShadowLayer(0f, 0f, 0f, 0)
            }
            ClockStyleManager.ClockStyle.NEON -> {
                textView.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                textView.textSize = 64f
                textView.setTextColor(ContextCompat.getColor(this, R.color.neon_cyan))
                textView.setShadowLayer(20f, 0f, 0f, ContextCompat.getColor(this, R.color.neon_cyan))
            }
            ClockStyleManager.ClockStyle.MINIMAL -> {
                textView.typeface = Typeface.create("sans-serif-thin", Typeface.NORMAL)
                textView.textSize = 60f
                textView.setTextColor(ContextCompat.getColor(this, R.color.minimal_gray))
                textView.setShadowLayer(0f, 0f, 0f, 0)
            }
            ClockStyleManager.ClockStyle.OUTLINED -> {
                textView.typeface = Typeface.create("sans-serif", Typeface.BOLD)
                textView.textSize = 64f
                textView.setTextColor(ContextCompat.getColor(this, R.color.white))
                textView.setShadowLayer(8f, 0f, 0f, ContextCompat.getColor(this, R.color.minimal_gray))
            }
            ClockStyleManager.ClockStyle.RETRO -> {
                textView.typeface = Typeface.MONOSPACE
                textView.textSize = 58f
                textView.setTextColor(ContextCompat.getColor(this, R.color.retro_amber))
                textView.setShadowLayer(0f, 0f, 0f, 0)
            }
        }
    }

    inner class ClockStyleAdapter(
        private val styles: List<ClockStyleManager.ClockStyle>,
        private var selectedStyle: ClockStyleManager.ClockStyle,
        private val onStyleSelected: (ClockStyleManager.ClockStyle) -> Unit
    ) : RecyclerView.Adapter<ClockStyleAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ItemClockStyleBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemClockStyleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val style = styles[position]
            val styleName = when (style) {
                ClockStyleManager.ClockStyle.DIGITAL -> getString(R.string.clock_style_digital)
                ClockStyleManager.ClockStyle.BOLD -> getString(R.string.clock_style_bold)
                ClockStyleManager.ClockStyle.BOLD_SQUARE -> getString(R.string.clock_style_bold_square)
                ClockStyleManager.ClockStyle.BOLD_CONDENSED -> getString(R.string.clock_style_bold_condensed)
                ClockStyleManager.ClockStyle.NEON -> getString(R.string.clock_style_neon)
                ClockStyleManager.ClockStyle.MINIMAL -> getString(R.string.clock_style_minimal)
                ClockStyleManager.ClockStyle.OUTLINED -> getString(R.string.clock_style_outlined)
                ClockStyleManager.ClockStyle.RETRO -> getString(R.string.clock_style_retro)
            }

            holder.binding.styleName.text = styleName
            holder.binding.styleClockPreview.text = "12:34"
            applyMiniClockStyle(holder.binding.styleClockPreview, style)

            // Highlight selected style
            val isSelected = style == selectedStyle
            if (isSelected) {
                holder.binding.root.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.accent_blue))
                holder.binding.root.alpha = 1.0f
            } else {
                holder.binding.root.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.card_background))
                holder.binding.root.alpha = 0.7f
            }

            holder.binding.root.setOnClickListener {
                onStyleSelected(style)
            }
        }

        override fun getItemCount(): Int = styles.size

        fun updateSelectedStyle(style: ClockStyleManager.ClockStyle) {
            val oldPosition = styles.indexOf(selectedStyle)
            val newPosition = styles.indexOf(style)
            selectedStyle = style
            notifyItemChanged(oldPosition)
            notifyItemChanged(newPosition)
        }

        private fun applyMiniClockStyle(textView: android.widget.TextView, style: ClockStyleManager.ClockStyle) {
            when (style) {
                ClockStyleManager.ClockStyle.DIGITAL -> {
                    textView.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
                    textView.textSize = 36f
                    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
                    textView.setShadowLayer(0f, 0f, 0f, 0)
                }
                ClockStyleManager.ClockStyle.BOLD -> {
                    textView.typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
                    textView.textSize = 40f
                    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
                    textView.setShadowLayer(0f, 0f, 0f, 0)
                }
                ClockStyleManager.ClockStyle.BOLD_SQUARE -> {
                    textView.typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
                    textView.textSize = 40f
                    textView.letterSpacing = 0.12f
                    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
                    textView.setShadowLayer(0f, 0f, 0f, 0)
                }
                ClockStyleManager.ClockStyle.BOLD_CONDENSED -> {
                    textView.typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
                    textView.textSize = 40f
                    textView.letterSpacing = -0.03f
                    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
                    textView.setShadowLayer(0f, 0f, 0f, 0)
                }
                ClockStyleManager.ClockStyle.NEON -> {
                    textView.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                    textView.textSize = 36f
                    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.neon_cyan))
                    textView.setShadowLayer(10f, 0f, 0f, ContextCompat.getColor(textView.context, R.color.neon_cyan))
                }
                ClockStyleManager.ClockStyle.MINIMAL -> {
                    textView.typeface = Typeface.create("sans-serif-thin", Typeface.NORMAL)
                    textView.textSize = 34f
                    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.minimal_gray))
                    textView.setShadowLayer(0f, 0f, 0f, 0)
                }
                ClockStyleManager.ClockStyle.OUTLINED -> {
                    textView.typeface = Typeface.create("sans-serif", Typeface.BOLD)
                    textView.textSize = 36f
                    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
                    textView.setShadowLayer(4f, 0f, 0f, ContextCompat.getColor(textView.context, R.color.minimal_gray))
                }
                ClockStyleManager.ClockStyle.RETRO -> {
                    textView.typeface = Typeface.MONOSPACE
                    textView.textSize = 32f
                    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.retro_amber))
                    textView.setShadowLayer(0f, 0f, 0f, 0)
                }
            }
        }
    }
}
