package com.example.virtualbreak.view.view_fragments.textchat

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.controller.adapters.ChatAdapter
import com.example.virtualbreak.controller.communication.PushData
import com.example.virtualbreak.model.Message
import com.example.virtualbreak.model.Room
import com.example.virtualbreak.model.User
import com.example.virtualbreak.view.view_activitys.breakroom.BreakRoomViewModel
import com.example.virtualbreak.view.view_activitys.breakroom.BreakRoomViewModelFactory
import kotlinx.android.synthetic.main.textchat_fragment.*

//class TextchatFragment(private var userName: String?) : Fragment() {
class TextchatFragment() : Fragment() {

    private val TAG = "TextchatFragment"

    //private lateinit var viewModel: TextchatViewModel
    //private val viewModel = ViewModelProvider(this).get(TextchatViewModel::class.java)
    private val viewModel: TextchatViewModel by viewModels{
        TextchatViewModelFactory(
            SharedPrefManager.instance.getRoomId() ?: ""
        )
    }

    private var room: Room? = null
    private val roomId: String? =
        com.example.virtualbreak.controller.SharedPrefManager.instance.getRoomId()
    private val userName: String? = SharedPrefManager.instance.getUserName()
    private var chatAdapter: ChatAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.textchat_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var defaultMessages: MutableList<Message> = ArrayList()
        var defaultM = Message("default", "Keine Nachricht", Constants.DEFAULT_TIME)
        defaultMessages.add(defaultM)

        var layoutManager = LinearLayoutManager(context)
        layoutManager.setStackFromEnd(true)
        //var chatMessageRecyclerView = root.findViewById(chat_messages_recycler_view)
        //Log.i(TAG, "recyclerView " + chatMessageRecyclerView)
        chat_messages_recycler_view.layoutManager = layoutManager


        chatAdapter = context?.let {
            ChatAdapter(
                it,
                defaultMessages,
                SharedPrefManager.instance.getRoomUsersHashmap()
            )
        }
        chat_messages_recycler_view.adapter = chatAdapter
        chatAdapter?.let {
            chat_messages_recycler_view.smoothScrollToPosition(it.itemCount)
        }

        /*viewModel.getUser().observe(viewLifecycleOwner, Observer<User> { observedUser ->
        if (observedUser != null) {
            userName = observedUser.username
        }})*/


        viewModel.getRoom().observe(viewLifecycleOwner, Observer<Room>
        { observedRoom ->

            room = observedRoom
            //context?.let { viewModel.loadUsersOfRoom(it) }

            Log.d(TAG, "Observed room: $observedRoom")
            if (observedRoom != null && observedRoom.messages != null && observedRoom.messages.isNotEmpty()) {
                val messages = observedRoom.messages
                var messagesList = ArrayList(messages.values)
                messagesList.sortBy { it.timestamp }
                Log.i(TAG, "messagesList: $messages")
                //chat_messages_recycler_view.adapter = context?.let { ChatAdapter(it, messagesList, SharedPrefManager.instance.getRoomUsersHashmap()) }
                if (chatAdapter == null) {
                    chatAdapter = context?.let {
                        ChatAdapter(
                            it,
                            messagesList,
                            SharedPrefManager.instance.getRoomUsersHashmap()
                        )
                    }
                    chat_messages_recycler_view.adapter = chatAdapter
                } else {
                    chatAdapter?.updateData(
                        messagesList,
                        SharedPrefManager.instance.getRoomUsersHashmap()
                    )
                }
            }
        })

        send_message_button.setOnClickListener {
            val input = chat_message_input.text
            val message = input.toString()
            if (!message.isEmpty()) {
                if (roomId != null) {
                    PushData.sendMessage(roomId, message)
                }
            } else {
                Toast.makeText(
                    context, R.string.toast_enter_message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            input.clear()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}