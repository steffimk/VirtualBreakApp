package com.example.virtualbreak.controller.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Message
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ChatAdapter(context: Context, messages: MutableList<Message>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    var messagesList: MutableList<Message> = mutableListOf()
    var context: Context
    private val TAG: String = "ChatAdapter"

    init {
        this.messagesList = messages
        this.context = context
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageView: TextView
        val senderView: TextView
        val chatItem: RelativeLayout
        private val TAG: String = "ChatAdapter_ViewHolder"

        init {
            messageView = itemView.findViewById(R.id.show_chat_message)
            senderView = itemView.findViewById(R.id.show_chat_sender)
            chatItem = itemView.findViewById(R.id.chat_message)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_message_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun onBindViewHolder(holder: ChatAdapter.ViewHolder, position: Int) {
        val message = messagesList.get(position)
        val messageSenderId = message.sender

        // TODO: format viewMessage and viewSender
        val viewMessage = holder.messageView
        val viewSender = holder.senderView
        val layout = holder.chatItem

        var sameSender = false
        Log.i(TAG, "message: " + message)

        if (position > 0) {
            val previousMessageSenderId = messagesList.get(position - 1).sender
            if (messageSenderId.equals(previousMessageSenderId)) {
                sameSender = true
            }
        }

        setMessage(viewMessage, message, messageSenderId, viewSender, layout)

        // don't show the sender name again, when previous message is from the same sender
        if (!sameSender) {
            val prefs =
                context.getSharedPreferences("com.example.virtualbreak", Context.MODE_PRIVATE)

            val gson = Gson()
            //get hashmap from shared prefs
            val storedHashMapString = prefs.getString("com.example.virtualbreak.roomUser", null)
            val type: Type =
                object : TypeToken<HashMap<String?, String?>?>() {}.getType()
            val roomUsers: HashMap<String, String> =
                gson.fromJson(storedHashMapString, type)

            val sender = roomUsers.get(messageSenderId)

            Log.i(TAG, "senderName: " + sender)
            viewSender.setText(sender)
        }
    }


    private fun setMessage(
        viewMessage: TextView,
        message: Message,
        messageSenderId: String,
        viewSender: TextView,
        layout: RelativeLayout
    ) {
        viewMessage.setText(message.message)

        // highlight own sended messages
        val ownId = PullData.currentUser?.value?.uid
        if (message.sender.equals(ownId)) {
            //viewMessage.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow_green))
            layout.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow_green))
        }
    }

}