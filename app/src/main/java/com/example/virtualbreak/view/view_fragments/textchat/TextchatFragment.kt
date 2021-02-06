package com.example.virtualbreak.view.view_fragments.textchat

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
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
        Log.d(TAG, "OnViewCreated")

        var defaultMessages: MutableList<Message> = ArrayList()
        var defaultM = Message("default", "Keine Nachricht", Constants.DEFAULT_TIME)
        defaultMessages.add(defaultM)

        val layoutManager = LinearLayoutManager(context)
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

        chat_messages_recycler_view.post { //post ensures that scoll is done when recyclerview is ready
            chatAdapter?.let {
                chat_messages_recycler_view.scrollToPosition(it.itemCount - 1)
            }
        }

        /*viewModel.getUser().observe(viewLifecycleOwner, Observer<User> { observedUser ->
        if (observedUser != null) {
            userName = observedUser.username
        }})*/


        viewModel.getRoom().observe(viewLifecycleOwner, Observer<Room>
        { observedRoom ->

            room = observedRoom
            //context?.let { viewModel.loadUsersOfRoom(it) }

            //Log.d(TAG, "Observed room: $observedRoom")
            if (observedRoom != null && observedRoom.messages != null && observedRoom.messages.isNotEmpty()) {

                val messages = observedRoom.messages
                var messagesList = ArrayList(messages.values)
                messagesList.sortBy { it.timestamp } //evtl this can be made more efficient by adding new messages and not updating whole list
                //Log.i(TAG, "messagesList: $messages")
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
                chat_messages_recycler_view.post {
                    chatAdapter?.let {
                        chat_messages_recycler_view.scrollToPosition(it.itemCount - 1)
                    }
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

        if (Build.VERSION.SDK_INT >= 11) {
            chat_messages_recycler_view.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                if (bottom < oldBottom) {
                    chat_messages_recycler_view.post {
                        chatAdapter?.let {
                            chat_messages_recycler_view.scrollToPosition(it.itemCount - 1)
                        }
                    }
                }
            })
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    /**
     * closes soft keyboard (don't use, nicer if it stays open because used often in chat)
     * editText: View
     */
    private fun hideSoftKeyboard(button: View) {
        /*val imm: InputMethodManager? = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(button.getWindowToken(), 0)*/

        val view: View? = activity?.getCurrentFocus()

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            val manager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            manager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}