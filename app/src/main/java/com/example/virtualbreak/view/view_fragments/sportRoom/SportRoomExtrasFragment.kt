package com.example.virtualbreak.view.view_fragments.sportRoom

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.databinding.FragmentSportRoomExtrasBinding
import kotlinx.android.synthetic.main.fragment_sport_room_extras.*
import kotlinx.android.synthetic.main.hangman_fragment.*
import kotlinx.android.synthetic.main.hangman_fragment.expand_game_btn
import java.util.*


class SportRoomExtrasFragment : Fragment() {

    private val TAG = "SportRoomExtrasFragment"

    private val viewModel: SportRoomExtrasViewModel by viewModels {
            SportRoomExtrasViewModelFactory(SharedPrefManager.instance.getRoomId() ?: "")}

    private var _binding: FragmentSportRoomExtrasBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var timerIsRunning = false
    private var countDownTimer: CountDownTimer? = null
    private var fitnessIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentSportRoomExtrasBinding.inflate(inflater, container, false)

        binding.startTimerBtn.setOnClickListener { startNewTimer() }
        binding.fitnessNextBtn.setOnClickListener {
            fitnessIndex = (fitnessIndex + 1) % Constants.FITNESS_IDEAS.size
            onSelectedNewExercise(Constants.FITNESS_IDEAS[fitnessIndex])
        }
        binding.fitnessPreviousBtn.setOnClickListener {
            if (fitnessIndex == 0) fitnessIndex = Constants.FITNESS_IDEAS.size
            fitnessIndex = (fitnessIndex - 1) % Constants.FITNESS_IDEAS.size
            onSelectedNewExercise(Constants.FITNESS_IDEAS[fitnessIndex])
        }

        binding.minPicker.maxValue = 30 // TODO: Maximum length
        val secPicker = binding.secPicker
        secPicker.maxValue = 5
        // Formatter to make steps of 10
        val secFormatter = NumberPicker.Formatter { value ->
            var tmp = (value * 10).toString()
            if (tmp == "0") tmp = "00"
            tmp
        }
        secPicker.setFormatter(secFormatter)
        // Following code fixes bug that formatter does not format on first render
        for(index in secPicker.minValue..secPicker.maxValue) {
            val edit = secPicker.getChildAt(index-secPicker.minValue)
            if (edit != null && edit is EditText) {
                edit.filters = arrayOfNulls(0)
            }
        }
        onSelectedNewExercise(Constants.FITNESS_IDEAS[fitnessIndex])
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //expand or close sport fragment when click on expand arrow, textchat adapts to height
        expand_sport_btn.setOnClickListener {
            if (sport_content_layout.getVisibility() === View.VISIBLE) {

                // The transition of the hiddenView is carried out
                //  by the TransitionManager class.
                // Here we use an object of the AutoTransition
                // Class to create a default transition.
                TransitionManager.beginDelayedTransition(
                    sport_base_view,
                    AutoTransition()
                )
                sport_content_layout.setVisibility(View.GONE)
                expand_sport_btn.setImageResource(R.drawable.ic_baseline_expand_more_24)
            } else {
                TransitionManager.beginDelayedTransition(
                    sport_base_view,
                    AutoTransition()
                )
                sport_content_layout.setVisibility(View.VISIBLE)
                expand_sport_btn.setImageResource(R.drawable.ic_baseline_expand_less_24)
            }

        }

        viewModel.startPullingExercise()
        viewModel.getTimerEndDate().observe(viewLifecycleOwner, Observer<Long?> { timerEndDate ->
            handleNewTimerEndDate(timerEndDate)
            Log.d(TAG, "Observed timerEndDate " + timerEndDate)
        })
    }

    private fun handleNewTimerEndDate(timerEndDate: Long?) {
        this.timerIsRunning = (timerEndDate != null && timerEndDate > Date().time)
        if (timerIsRunning) {
            binding.timePicker.visibility = View.GONE
            binding.timerView.visibility = View.VISIBLE
            binding.startTimerBtn.text = getString(R.string.cancelTimer)
            binding.fitnessNextBtn.visibility = View.INVISIBLE
            binding.fitnessPreviousBtn.visibility = View.INVISIBLE
            binding.fitnessText.text = viewModel.fitnessExercise?:"Fehler beim Laden der Übung"
            countDownTimer = getCountDownTimer(timerEndDate!!).start()
            if (viewModel.fitnessExercise == null) Log.d(TAG, "Fitness exercise is null")
        } else {
            if (this.countDownTimer != null) this.countDownTimer?.cancel()
            binding.timePicker.visibility = View.VISIBLE
            binding.timerView.visibility = View.GONE
            binding.startTimerBtn.text = getString(R.string.startTimer)
            binding.fitnessNextBtn.visibility = View.VISIBLE
            binding.fitnessPreviousBtn.visibility = View.VISIBLE
        }
    }

    private fun onSelectedNewExercise(exercise: Pair<String, Double>) {
        binding.fitnessText.text = exercise.first
        val defaultTime = exercise.second as Double
        binding.minPicker.value = defaultTime.toInt()
        binding.secPicker.value = ((defaultTime - defaultTime.toInt()) * 10).toInt() // Get first decimal
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startNewTimer() {
        val roomId = SharedPrefManager.instance.getRoomId() ?: return
        if (this.timerIsRunning) {
            PushData.removeTimer(roomId)
            return
        }
        val mins = binding.minPicker.value
        val secs = binding.secPicker.value * 10
        PushData.startNewTimer(roomId, mins, secs, binding.fitnessText.text.toString())
    }

    private fun secondsToTimerString(sec: Long): String{
        val minutes = (sec/60).toInt().toString()
        val seconds = (sec%60).toInt().toString()
        return minutes + "m " + seconds + "s"
    }

    private fun getCountDownTimer(timerEndDate: Long): CountDownTimer {
        val remainingMilliSeconds = timerEndDate - Date().time
        countDownTimer?.cancel()
        return object : CountDownTimer(remainingMilliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerView.text = (secondsToTimerString(millisUntilFinished / 1000))
            }
            override fun onFinish() {
                MediaPlayer.create(context, R.raw.alarm_sound).start()
                handleNewTimerEndDate(null)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if(countDownTimer == null) return
        countDownTimer?.cancel()
    }

    override fun onResume() {
        super.onResume()
        handleNewTimerEndDate(viewModel.getTimerEndDate().value)
    }

}