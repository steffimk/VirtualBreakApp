package com.example.virtualbreak.view.view_fragments.boredapi

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.communication.PushData
import com.squareup.okhttp.*
import kotlinx.android.synthetic.main.fragment_bored_api.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * Bored API extras fragment for a random activity that can be shared with others in break room group chat
 */
class BoredApiFragment : Fragment() {

    private val roomId: String? = SharedPrefManager.instance.getRoomId()
    val TAG = "BoredApiFragment"
    var activityString: String? = null

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

        expand_boredapi_relative_layout.setOnClickListener {
            toggleBoredApiVisibility()
        }
        expand_boredapi_btn.setOnClickListener {
            toggleBoredApiVisibility()
        }

        random_activity_btn.setOnClickListener {
            runBoredApiCall("https://www.boredapi.com/api/activity/")
        }

        send_random_activity_btn.setOnClickListener {
            if(activityString != null){
                roomId?.let{
                    PushData.sendMessage(it, "Neue Bored-Aktivität: \n"+activityString)
                }
            }
        }
    }

    private fun toggleBoredApiVisibility() {
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
            expand_boredapi_btn.setImageResource(R.drawable.ic_baseline_expand_more_24)
        } else {
            TransitionManager.beginDelayedTransition(
                boredapi_base_cardview,
                AutoTransition()
            )
            content_boredapi.setVisibility(View.VISIBLE)
            expand_boredapi_btn.setImageResource(R.drawable.ic_baseline_expand_less_24)
        }
    }

    fun runBoredApiCall(url: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(request: Request?, e: IOException?) {
                Log.d(TAG, "Failure")
            }
            override fun onResponse(response: Response?) {


                try {
                    //example response body: {"activity":"Watch the sunset or the sunrise","type":"recreational","participants":1,"price":0,"link":"","key":"4748214","accessibility":1}
                    if(response != null && response.body()!= null){
                        val json = JSONObject(response.body().string())
                        activityString = json.getString("activity")
                        Log.d(TAG, "Request Successful!! "+activityString.toString())
                        activity?.runOnUiThread {
                            //some animation
                            TransitionManager.beginDelayedTransition(content_boredapi)
                            display_random_activity.text = activityString
                            send_random_activity_btn.visibility = View.VISIBLE
                        }
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
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