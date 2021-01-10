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
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.model.Message
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

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
        val timestampView: TextView
        private val TAG: String = "ChatAdapter_ViewHolder"

        init {
            messageView = itemView.findViewById(R.id.show_chat_message)
            senderView = itemView.findViewById(R.id.show_chat_sender)
            chatItem = itemView.findViewById(R.id.chat_message)
            timestampView = itemView.findViewById(R.id.show_message_timestamp)
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

        Log.d(TAG, "ChatAdapter onBindViewHolder")

        val message = messagesList.get(position)
        val messageSenderId = message.sender

        // TODO: format viewMessage and viewSender
        val viewMessage = holder.messageView
        val viewSender = holder.senderView
        val viewTimestamp = holder.timestampView
        val layout = holder.chatItem

        var sameSender = false
        Log.i(TAG, "message: " + message)

        if (position > 0) {
            val previousMessageSenderId = messagesList.get(position - 1).sender
            if (messageSenderId.equals(previousMessageSenderId)) {
                sameSender = true
            }
        }

        setMessage(viewMessage, message, viewTimestamp, layout)

        // don't show the sender name again, when previous message is from the same sender
        if (!sameSender) {

            val roomUsers: HashMap<String, String>? = SharedPrefManager.instance.getRoomUsersHashmap()
            /*val prefs =
                context.getSharedPreferences("com.example.virtualbreak", Context.MODE_PRIVATE)

            val gson = Gson()
            //get hashmap from shared prefs
            val storedHashMapString = prefs.getString("com.example.virtualbreak.roomUser", null)
            val type: Type =
                object : TypeToken<HashMap<String?, String?>?>() {}.getType()
            val roomUsers: HashMap<String, String>? =
                gson.fromJson(storedHashMapString, type)*/


            if (roomUsers != null) {
                val sender = roomUsers.get(messageSenderId)
                Log.i(TAG, "senderName: " + sender)
                if (sender != null) {
                    viewSender.setText(sender)
                }
            }
        }
    }



    private fun setMessage(
        viewMessage: TextView,
        message: Message,
        viewTimestamp: TextView,
        layout: RelativeLayout
    ) {
        viewMessage.setText(message.message)

        val date = message.timestamp

        if(date !=  Constants.DEFAULT_TIME){
            val timestamp = Timestamp(date)

            val sfd = SimpleDateFormat(
                "dd.MM.yyyy HH:mm",
                Locale.getDefault()
            )
            val text: String = sfd.format(timestamp)
            viewTimestamp.setText(text)
        }


        // highlight own sended messages
        val ownId = SharedPrefManager.instance.getUserId() ?: ""
        if (message.sender.equals(ownId)) {
            //viewMessage.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow_green))
            layout.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow_green))
        } else if(message.sender.equals(Constants.DEFAULT_MESSAGE_SENDER)){
            layout.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_pale))
        }
    }

}