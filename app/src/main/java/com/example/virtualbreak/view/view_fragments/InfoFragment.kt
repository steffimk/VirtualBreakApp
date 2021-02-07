package com.example.virtualbreak.view.view_fragments

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.virtualbreak.R
import kotlinx.android.synthetic.main.fragment_info.*

/**
 * A simple [Fragment] subclass.
 * Use the [InfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //at first close all fragments
        expand_general()
        expandAboutUs()
        expandSources()

        //expand or close general fragment
        expand_general_relative_layout.setOnClickListener {
            expand_general()
        }

        expand_general_btn.setOnClickListener {
            expand_general()
        }


        expand_about_us_relative_layout.setOnClickListener {
            expandAboutUs()
        }

        expand_about_us_btn.setOnClickListener {
            expandAboutUs()
        }


        expand_sources_relative_layout.setOnClickListener {
            expandSources()
        }

        expand_sources_btn.setOnClickListener {
            expandSources()
        }
    }

    private fun expandSources() {
        if (sources_view.getVisibility() === View.VISIBLE) {
            TransitionManager.beginDelayedTransition(
                info_base,
                AutoTransition()
            )
            sources_view.setVisibility(View.GONE)
            expand_sources_btn.setImageResource(R.drawable.ic_baseline_expand_more_24)
        } else {
            TransitionManager.beginDelayedTransition(
                info_base,
                AutoTransition()
            )
            sources_view.setVisibility(View.VISIBLE)
            expand_sources_btn.setImageResource(R.drawable.ic_baseline_expand_less_24)
        }
    }

    private fun expandAboutUs() {
        if (about_us_info.getVisibility() === View.VISIBLE) {
            TransitionManager.beginDelayedTransition(
                info_base,
                AutoTransition()
            )
            about_us_info.setVisibility(View.GONE)
            expand_about_us_btn.setImageResource(R.drawable.ic_baseline_expand_more_24)
        } else {
            TransitionManager.beginDelayedTransition(
                info_base,
                AutoTransition()
            )
            about_us_info.setVisibility(View.VISIBLE)
            expand_about_us_btn.setImageResource(R.drawable.ic_baseline_expand_less_24)
        }
    }

    private fun expand_general() {
        if (general_info.getVisibility() === View.VISIBLE) {
            TransitionManager.beginDelayedTransition(
                info_base,
                AutoTransition()
            )
            general_info.setVisibility(View.GONE)
            expand_general_btn.setImageResource(R.drawable.ic_baseline_expand_more_24)
        } else {
            TransitionManager.beginDelayedTransition(
                info_base,
                AutoTransition()
            )
            general_info.setVisibility(View.VISIBLE)
            expand_general_btn.setImageResource(R.drawable.ic_baseline_expand_less_24)
        }
    }
}