package com.example.virtualbreak.view.view_fragments.question

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PushData
import kotlinx.android.synthetic.main.fragment_question.*
import kotlinx.android.synthetic.main.fragment_sport_room_extras.*
import kotlinx.android.synthetic.main.fragment_sport_room_extras.expand_sport_btn


class QuestionFragment : Fragment() {

    private val TAG = "QuestionFragment"

    private val viewModel: QuestionFragmentViewModel by viewModels {
        QuestionFragmentViewModelFactory(SharedPrefManager.instance.getRoomId() ?: "")}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        question_text.movementMethod = ScrollingMovementMethod()
        save_question_btn.setOnClickListener {
            onSaveQuestion(question_input.text.toString())
        }
        edit_question_btn.setOnClickListener {
            onEditQuestion(question_text.text.toString())
        }
        viewModel.getQuestion().observe(viewLifecycleOwner, Observer<String?> { observedQuestion ->
            if (observedQuestion != null) {
                question_input.setText(observedQuestion)
                question_input.visibility = View.GONE
                question_text.text = observedQuestion
                question_text.visibility = View.VISIBLE
                save_question_btn.visibility = View.GONE
                edit_question_btn.visibility = View.VISIBLE
            } else {
                question_input.visibility = View.VISIBLE
                question_text.visibility = View.GONE
                save_question_btn.visibility = View.VISIBLE
                edit_question_btn.visibility = View.GONE
            }
            Log.d(TAG, "Observed question " + observedQuestion)
        })

        //expand or close sport fragment when click on expand arrow, textchat adapts to height
        expand_question_btn.setOnClickListener {
            if (question_content.getVisibility() === View.VISIBLE) {
                TransitionManager.beginDelayedTransition(
                    question_base_view,
                    AutoTransition()
                )
                question_content.visibility = View.GONE
                expand_question_btn.setImageResource(R.drawable.ic_baseline_expand_more_24)
            } else {
                TransitionManager.beginDelayedTransition(
                    question_base_view,
                    AutoTransition()
                )
                question_content.visibility = View.VISIBLE
                expand_question_btn.setImageResource(R.drawable.ic_baseline_expand_less_24)
            }

        }
    }

    private fun onSaveQuestion(question :String) {
        PushData.saveQuestion(SharedPrefManager.instance.getRoomId()?:"", question)
        question_text.visibility = View.VISIBLE
        question_input.visibility = View.GONE
        edit_question_btn.visibility = View.VISIBLE
        save_question_btn.visibility = View.GONE
    }

    private fun onEditQuestion(question :String) {
        question_input.setText(question)
        question_text.visibility = View.GONE
        question_input.visibility = View.VISIBLE
        edit_question_btn.visibility = View.GONE
        save_question_btn.visibility = View.VISIBLE
    }

}