package com.health.insugo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.health.insugo.databinding.FragmentListBinding
import kotlinx.coroutines.launch


class ListFragment : Fragment(R.layout.fragment_list) {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListaViewModel by viewModels()


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.alimentos.collect { lista ->
                if (binding.llContainer.childCount > 1){
                    binding.llContainer.removeViews(1, binding.llContainer.childCount - 1)
                }
                lista.forEach { alimento ->
                    val textView = TextView(requireContext()).apply{
                        text = "• $alimento"
                        textSize = 18f
                        setPadding(0, 8, 0, 8)
                    }
                    binding.llContainer.addView(textView)
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}