package com.health.insugo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.health.insugo.databinding.ActivityMainBinding

// Heredamos de AppCompatActivity para soporte de Fragments clásicos [cite: 46]
class MainActivity : AppCompatActivity() {

    // Declaramos el binding para acceso seguro a las vistas [cite: 47]
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflamos la vista usando ViewBinding (Chau findViewById) [cite: 48]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cargamos el LoginFragment dinámicamente si es la primera vez [cite: 83-86]
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                // R.id.fragmentContainer es el ID de tu FragmentContainerView en activity_main.xml [cite: 74]
                add(R.id.fragmentContainer, LoginFragment())
            }
        }
    }
}