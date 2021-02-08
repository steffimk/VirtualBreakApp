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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
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
import com.example.virtualbreak.model.Roomtype
import kotlinx.android.synthetic.main.textchat_fragment.*


class TextchatFragment() : Fragment() {

    private val TAG = "TextchatFragment"

    private val viewModel: TextchatViewModel by viewModels {
        TextchatViewModelFactory(
            SharedPrefManager.instance.getRoomId() ?: ""
        )
    }

    private var room: Room? = null
    private val roomId: String? = SharedPrefManager.instance.getRoomId()
    private val userName: String? = SharedPrefManager.instance.getUserName()
    private var chatAdapter: ChatAdapter? = null
    private var messagesList =
        arrayListOf<Message>() //keep old messages saved to check for new ones


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

        // when receiving that hangman is clicked, remove focus of edittext
        parentFragmentManager.setFragmentResultListener(
            Constants.REQUEST_KEY_GAME_FRAGMENT_CLICK,
            this,
            FragmentResultListener { requestKey, bundle ->
                val result = bundle.getString(Constants.BUNDLE_KEY_GAME_FRAGMENT_CLICK)
                if(result.equals(Constants.CLICK)){
                    chat_message_input.clearFocus()
                }
            })

        val defaultMessages: MutableList<Message> = ArrayList()
        val defaultM = Message("default", "Keine Nachricht", Constants.DEFAULT_TIME)
        defaultMessages.add(defaultM)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.setStackFromEnd(true)
        chat_messages_recycler_view.layoutManager = layoutManager


        chatAdapter = context?.let {
            ChatAdapter(
                it,
                defaultMessages,
                SharedPrefManager.instance.getRoomUsersHashmap()
            )
        }
        chat_messages_recycler_view.adapter = chatAdapter

        chat_messages_recycler_view.post { //post ensures that scroll is done when recyclerview is ready
            chatAdapter?.let {
                chat_messages_recycler_view.scrollToPosition(it.itemCount - 1)
            }
        }


        viewModel.getRoom().observe(viewLifecycleOwner, Observer<Room>
        { observedRoom ->

            room = observedRoom

            if (observedRoom != null && observedRoom.messages != null && observedRoom.messages.isNotEmpty()) {

                val newMessages = observedRoom.messages
                var newMessagesList = ArrayList(newMessages.values)
                newMessagesList.sortBy { it.timestamp } //evtl this can be made more efficient by adding new messages and not updating whole list
                if (chatAdapter == null) {
                    chatAdapter = context?.let {
                        ChatAdapter(
                            it,
                            newMessagesList,
                            SharedPrefManager.instance.getRoomUsersHashmap()
                        )
                    }
                    chat_messages_recycler_view.adapter = chatAdapter
                } else { //ChatAdapter already exists
                    Log.d(TAG, "Old " + messagesList.size + " new: " + newMessagesList.size)
                    if (messagesList.size < newMessagesList.size) {
                        chatAdapter?.updateData(
                            newMessagesList,
                            SharedPrefManager.instance.getRoomUsersHashmap()
                        )
                    }
                }
                messagesList = newMessagesList
                chat_messages_recycler_view.post {
                    chatAdapter?.let {
                        chat_messages_recycler_view.scrollToPosition(it.itemCount - 1)
                    }
                }

            }
        })

        // when edittext is focused send info to close hangman fragment
        chat_message_input.setOnFocusChangeListener { view, b ->
            if(room != null){
                if(room!!.type.equals(Roomtype.GAME)){
                    val result = b
                    Log.i(TAG, "sending edittext event " + b)
                    parentFragmentManager.setFragmentResult(
                        Constants.REQUEST_KEY_GAME_FRAGMENT,
                        bundleOf(Constants.BUNDLE_KEY_GAME_FRAGMENT to result)
                    )
                    if(!b){
                        Log.i(TAG, "edit text lose focus")
                        val imm =
                            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                        imm!!.hideSoftInputFromWindow(chat_message_input.getWindowToken(), 0)
                    }
                }
            }
        }

        // sends entered message if not empty
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

        // if keyboard is opened or closed also scroll down
        if (Build.VERSION.SDK_INT >= 11) {
            chat_messages_recycler_view.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                Log.d(TAG, "Layout CHANGE")
                if (bottom < oldBottom || bottom > oldBottom) {
                    chat_messages_recycler_view.post {
                        chatAdapter?.let {
                            chat_messages_recycler_view?.scrollToPosition(it.itemCount - 1)
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
            val manager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            manager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}