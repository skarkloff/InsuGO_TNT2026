package com.health.insugo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.health.insugo.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        binding.btnLogin.setOnClickListener {
            val user = binding.etUsername.text.toString()
            val pass = binding.etPassword.text.toString()

            if (user == "admin" && pass == "123456") {
                parentFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.fragmentContainer, WelcomeFragment())
                }
            } else {
                Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}