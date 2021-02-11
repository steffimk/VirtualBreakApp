package com.example.virtualbreak.view.view_fragments.question

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PushData
import kotlinx.android.synthetic.main.fragment_question.*

/**
 * Fragment added on top of a questions room. Shows an editable question that is pinned on top of chat
 */
class QuestionFragment : Fragment() {

    private val TAG = "QuestionFragment"

    private val viewModel: QuestionViewModel by viewModels {
        QuestionFragmentViewModelFactory(SharedPrefManager.instance.getRoomId() ?: "")}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_question, container, false)
    }

    /**
     * Set on click listeners and observe LiveData of view model
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        question_text.movementMethod = ScrollingMovementMethod()
        save_question_btn.setOnClickListener {
            onSaveQuestion(question_input.text.toString())
        }
        edit_question_btn.setOnClickListener {
            onEditQuestion(question_text.text.toString())
        }
        // observation of new questions
        viewModel.getQuestion().observe(viewLifecycleOwner, Observer<String?> { observedQuestion ->
            if (observedQuestion != null) {
                question_input.setText(observedQuestion)
                input_layout.visibility = View.GONE
                question_text.text = observedQuestion
                question_text.visibility = View.VISIBLE
                save_question_btn.visibility = View.GONE
                edit_question_btn.visibility = View.VISIBLE
            } else {
                input_layout.visibility = View.VISIBLE
                question_text.visibility = View.GONE
                save_question_btn.visibility = View.VISIBLE
                edit_question_btn.visibility = View.GONE
            }
            Log.d(TAG, "Observed question " + observedQuestion)
        })

        //expand or close sport fragment
        expand_question_relative_layout.setOnClickListener {
            toggleQuestionFragmentVisibility()
        }

        expand_question_btn.setOnClickListener {
            toggleQuestionFragmentVisibility()
        }
    }

    /**
     * Hide and show question fragment on click
     */
    private fun toggleQuestionFragmentVisibility(){
        if (question_content.isShown) {
            collapseQuestionFragment()
        } else {
            expandQuestionFragment()
        }
    }

    private fun expandQuestionFragment() {
        TransitionManager.beginDelayedTransition(
            question_base_view,
            AutoTransition()
        )
        question_content.visibility = View.VISIBLE
        expand_question_btn.setImageResource(R.drawable.ic_baseline_expand_less_24)
    }

    private fun collapseQuestionFragment() {
        TransitionManager.beginDelayedTransition(
            question_base_view,
            AutoTransition()
        )
        question_content.visibility = View.GONE
        expand_question_btn.setImageResource(R.drawable.ic_baseline_expand_more_24)
    }

    /**
     * Call when user entered a new question. Saves it in the database.
     */
    private fun onSaveQuestion(question :String) {
        hideSoftKeyboard(save_question_btn)
        PushData.saveQuestion(SharedPrefManager.instance.getRoomId()?:"", question)
        question_text.visibility = View.VISIBLE
        input_layout.visibility = View.GONE
        edit_question_btn.visibility = View.VISIBLE
        save_question_btn.visibility = View.GONE
    }

    /**
     * Call when user wants to edit the question.
     */
    private fun onEditQuestion(question :String) {
        question_input.setText(question)
        question_text.visibility = View.GONE
        input_layout.visibility = View.VISIBLE
        edit_question_btn.visibility = View.GONE
        save_question_btn.visibility = View.VISIBLE
    }

    /**
     * Closes soft keyboard
     */
    private fun hideSoftKeyboard(button: ImageButton) {
        val imm: InputMethodManager? = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(button.windowToken, 0)
    }

}