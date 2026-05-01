package com.health.insugo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.health.insugo.databinding.ActivityGlucosaBinding

class GlucosaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGlucosaBinding
    private var glucValue = ""
    private var dirty = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlucosaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }

        // Chips selección única
        val chips = listOf(binding.chipAyunas, binding.chipPostComida, binding.chipDormir, binding.chipOtro)
        chips.forEach { chip ->
            chip.setOnClickListener {
                chips.forEach { c ->
                    c.setBackgroundResource(R.drawable.bg_chip_off)
                    c.setTextColor(getColor(R.color.gris_600))
                }
                chip.setBackgroundResource(R.drawable.bg_chip_on)
                chip.setTextColor(getColor(R.color.verde_texto))
            }
        }

        // Teclado
        val keys = mapOf(
            binding.key1 to "1", binding.key2 to "2", binding.key3 to "3",
            binding.key4 to "4", binding.key5 to "5", binding.key6 to "6",
            binding.key7 to "7", binding.key8 to "8", binding.key9 to "9",
            binding.key0 to "0"
        )
        keys.forEach { (btn, digit) ->
            btn.setOnClickListener { glucKey(digit) }
        }
        binding.keyC.setOnClickListener { glucClear() }
        binding.keyDel.setOnClickListener { glucDel() }

        binding.btnGuardar.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

    private fun glucKey(d: String) {
        if (!dirty) { glucValue = ""; dirty = true }
        if (glucValue == "0") glucValue = ""
        if (glucValue.length < 3) glucValue += d
        updateDisplay()
    }

    private fun glucDel() {
        dirty = true
        glucValue = if (glucValue.isNotEmpty()) glucValue.dropLast(1) else ""
        updateDisplay()
    }

    private fun glucClear() {
        dirty = true
        glucValue = ""
        updateDisplay()
    }

    private fun updateDisplay() {
        binding.tvGlucVal.text = if (glucValue.isEmpty()) "0" else glucValue
    }
}
