package com.example.virtualbreak.view.view_fragments.boredapi

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.virtualbreak.R
import kotlinx.android.synthetic.main.fragment_bored_api.*
import kotlinx.android.synthetic.main.hangman_fragment.*

private const val ROOM_ID = "roomId"

/**
 * A simple [Fragment] subclass.
 * Use the [BoredApiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BoredApiFragment : Fragment() {
    //private var roomId: String? = null
    private val roomId: String? = com.example.virtualbreak.controller.SharedPrefManager.instance.getRoomId()
    val TAG = "BoredApiFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*arguments?.let {
            roomId = it.getString(ROOM_ID)
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bored_api, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expand_boredapi_btn.setOnClickListener {
            if (content_boredapi.getVisibility() === View.VISIBLE) {

                // The transition of the hiddenView is carried out
                //  by the TransitionManager class.
                // Here we use an object of the AutoTransition
                // Class to create a default transition.
                TransitionManager.beginDelayedTransition(
                    boredapi_base_cardview,
                    AutoTransition()
                )
                content_boredapi.setVisibility(View.GONE)
                expand_game_btn.setImageResource(R.drawable.ic_baseline_expand_more_24)
            } else {
                TransitionManager.beginDelayedTransition(
                    boredapi_base_cardview,
                    AutoTransition()
                )
                content_boredapi.setVisibility(View.VISIBLE)
                expand_game_btn.setImageResource(R.drawable.ic_baseline_expand_less_24)
            }
        }

        random_activity_btn.setOnClickListener {
            display_random_activity.text = "Hallo"
        }
    }

    /*companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment BoredApiFragment.
         */
        @JvmStatic
        fun newInstance(roomId: String) =
            BoredApiFragment().apply {
                arguments = Bundle().apply {
                    putString(ROOM_ID, roomId)
                }
            }
    }*/
}