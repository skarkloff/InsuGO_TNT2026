package com.health.insugo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.health.insugo.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment(R.layout.fragment_welcome) {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWelcomeBinding.bind(view)

        binding.btnList.setOnClickListener {
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainer, ListFragment())
                addToBackStack(null)
            }
        }

        binding.btnAbout.setOnClickListener {
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainer, AboutFragment())
                addToBackStack(null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}