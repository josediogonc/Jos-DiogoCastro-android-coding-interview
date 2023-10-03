package com.beon.androidchallenge.ui.main

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.beon.androidchallenge.R
import com.beon.androidchallenge.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initViews()
        initObservers()
    }

    private fun toggleProgressBar(isVisible: Boolean) {
        with(binding) {
            progressBar.isVisible = isVisible
            factTextView.isVisible = !isVisible
        }
    }

    private val handler = Handler()

    private fun initViews() {
        binding.run {
            toggleProgressBar(isVisible = false)

            numberEditText.addTextChangedListener {
                if(it.toString().isNotEmpty()) {
                    toggleProgressBar(isVisible = true)
                }

                val h = handler.postDelayed(Runnable {
                    viewModel.searchNumberFact(it.toString())

                }, 1000)

            }

            numberEditText.setOnEditorActionListener { textView, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.searchNumberFact(textView.text.toString())
                    toggleProgressBar(isVisible = true)
                    return@setOnEditorActionListener true
                } else {
                    return@setOnEditorActionListener false
                }
            }

            retryButton.setOnClickListener {
                viewModel.searchNumberFact(numberEditText.text.toString())
                toggleProgressBar(isVisible = true)
            }

            numberEditText.requestFocus()
        }
    }

    private fun initObservers() {
        viewModel.currentFact.observe(viewLifecycleOwner) {
            if (binding.numberEditText.text.toString().isEmpty()) {
                binding.factTextView.setText(R.string.instructions)
            } else {
                toggleProgressBar(isVisible = false)
                binding.factTextView.text = it?.text
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            toggleProgressBar(isVisible = false)
            binding.factTextView.text = it
        }
    }

}