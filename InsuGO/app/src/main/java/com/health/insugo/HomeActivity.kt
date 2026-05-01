package com.health.insugo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.health.insugo.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAcerca.setOnClickListener {
            startActivity(Intent(this, AcercaDeActivity::class.java))
        }
        binding.btnAnotarGlucosa.setOnClickListener {
            startActivity(Intent(this, GlucosaActivity::class.java))
        }
        binding.btnComida.setOnClickListener {
            Toast.makeText(this, "Próximamente", Toast.LENGTH_SHORT).show()
        }
        binding.btnConsejoCard.setOnClickListener {
            Toast.makeText(this, "Próximamente", Toast.LENGTH_SHORT).show()
        }
        binding.btnHistorial.setOnClickListener {
            Toast.makeText(this, "Próximamente", Toast.LENGTH_SHORT).show()
        }
        // Bottom nav
        binding.navInicio.setOnClickListener { /* ya estás acá */ }
        binding.navAnotar.setOnClickListener {
            startActivity(Intent(this, GlucosaActivity::class.java))
        }
        binding.navConsejos.setOnClickListener {
            Toast.makeText(this, "Próximamente", Toast.LENGTH_SHORT).show()
        }
        binding.navHistorial.setOnClickListener {
            Toast.makeText(this, "Próximamente", Toast.LENGTH_SHORT).show()
        }
    }
}
