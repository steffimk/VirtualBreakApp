package com.example.virtualbreak.view.view_fragments.hangman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.virtualbreak.R
import kotlinx.android.synthetic.main.hangman_fragment.*

class HangmanFragment : Fragment() {

    private val TAG = "HangmanFragment"

    private lateinit var viewModel: HangmanViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.hangman_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        a_input.setOnClickListener {
            //a_input.setEnabled(false)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HangmanViewModel::class.java)
        // TODO: Use the ViewModel
    }

}