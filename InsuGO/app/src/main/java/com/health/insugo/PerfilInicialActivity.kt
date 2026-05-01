package com.health.insugo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.health.insugo.databinding.ActivityPerfilInicialBinding

class PerfilInicialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilInicialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilInicialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }

        // Chips selección única
        val chips = listOf(binding.chipDiabetes, binding.chipPre)
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

        val goHome = {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
        binding.btnSiActivar.setOnClickListener { goHome() }
        binding.btnAhoraNo.setOnClickListener { goHome() }
        binding.btnContinuar.setOnClickListener { goHome() }
    }
}
