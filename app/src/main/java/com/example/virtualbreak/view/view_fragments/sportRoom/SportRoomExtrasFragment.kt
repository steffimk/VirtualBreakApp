package com.example.virtualbreak.view.view_fragments.sportRoom

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
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
import com.example.virtualbreak.databinding.FragmentSportRoomExtrasBinding
import java.util.*


class SportRoomExtrasFragment : Fragment() {

    private val TAG = "SportRoomExtrasFragment"

    private val viewModel: SportRoomExtrasViewModel by viewModels {
            SportRoomExtrasViewModelFactory(SharedPrefManager.instance.getRoomId() ?: "")}

    private var _binding: FragmentSportRoomExtrasBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var timerArray = arrayOf("0", "0", "0", "0")
    private var timerIndex = 0
    private var timerIsRunning = false

    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentSportRoomExtrasBinding.inflate(inflater, container, false)

        binding.startTimerBtn.setOnClickListener { startNewTimer() }

        binding.inputBtn0.setOnClickListener { addNumber("0") }
        binding.inputBtn1.setOnClickListener { addNumber("1") }
        binding.inputBtn2.setOnClickListener { addNumber("2") }
        binding.inputBtn3.setOnClickListener { addNumber("3") }
        binding.inputBtn4.setOnClickListener { addNumber("4") }
        binding.inputBtn5.setOnClickListener { addNumber("5") }
        binding.inputBtn6.setOnClickListener { addNumber("6") }
        binding.inputBtn7.setOnClickListener { addNumber("7") }
        binding.inputBtn8.setOnClickListener { addNumber("8") }
        binding.inputBtn9.setOnClickListener { addNumber("9") }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getTimerEndDate().observe(viewLifecycleOwner, Observer<Long?> { timerEndDate ->
            this.timerIsRunning = timerEndDate != null
            if (timerEndDate != null) {
                binding.inputRow1.visibility = View.GONE
                binding.inputRow2.visibility = View.GONE
                binding.startTimerBtn.visibility = View.GONE
                countDownTimer = getCountDownTimer(timerEndDate).start()
            } else {
                binding.timerView.text = "00m 00s"
                timerArray = arrayOf("0", "0", "0", "0")
                timerIndex = 0
                binding.inputRow1.visibility = View.VISIBLE
                binding.inputRow2.visibility = View.VISIBLE
                binding.startTimerBtn.visibility = View.VISIBLE
            }
            Log.d(TAG, "Observed timerEndDate " + timerEndDate)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startNewTimer() {
        if (this.timerIsRunning) return
        val timeString = binding.timerView.text.toString()
        val mins = timeString.subSequence(0, 2).toString().toInt()
        val secs = timeString.subSequence(4, 6).toString().toInt()
        val roomId = SharedPrefManager.instance.getRoomId() ?: return
        PushData.startNewTimer(roomId, mins, secs)
    }

    private fun addNumber(n: String){
        if (this.timerIsRunning) return
        var number = n
        // Mins and secs cannot be bigger than 59
        if ((timerIndex == 0 || timerIndex == 2) && n.toInt() > 5) number = "5"
        timerArray[timerIndex] = number
        timerIndex = (timerIndex+1) % 4
        binding.timerView.text = timerArrayToTimerString()
    }

    private fun timerArrayToTimerString(): String{
        return timerArray[0] + timerArray[1] + "m " + timerArray[2] + timerArray[3] + "s"
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
                val roomId = SharedPrefManager.instance.getRoomId() ?: return
                PushData.removeTimer(roomId)
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
        val timerEnd = viewModel.getTimerEndDate().value?:return
        countDownTimer = getCountDownTimer(timerEnd).start()
    }

}