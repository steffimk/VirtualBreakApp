package com.example.virtualbreak.controller.adapters

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.Constants
import com.example.virtualbreak.controller.SharedPrefManager
import com.example.virtualbreak.model.Message
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class ChatAdapter(
    context: Context,
    messages: MutableList<Message>,
    roomUsernames: HashMap<String, String>?
) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    var messagesList: MutableList<Message> = mutableListOf()
    var roomUsers: HashMap<String, String>?
    var context: Context
    private val TAG: String = "ChatAdapter"

    init {
        Log.i(TAG, "ChatAdapter init()")
        this.messagesList = messages
        this.context = context
        this.roomUsers = roomUsernames

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageView: TextView
        val senderView: TextView
        val chatBubble: CardView
        val timestampView: TextView
        val chatItemWhole: LinearLayout
        private val TAG: String = "ChatAdapter_ViewHolder"

        init {
            messageView = itemView.findViewById(R.id.show_chat_message)
            senderView = itemView.findViewById(R.id.show_chat_sender)
            chatBubble = itemView.findViewById(R.id.chat_message_cardview)
            timestampView = itemView.findViewById(R.id.show_message_timestamp)
            chatItemWhole = itemView.findViewById(R.id.chatitem_whole)
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

        val viewMessage = holder.messageView
        val viewSender = holder.senderView
        val viewTimestamp = holder.timestampView
        val chatBubbleView = holder.chatBubble


        //show sender logic

        if (position > 0) {
            val previousMessageSenderId = messagesList.get(position - 1).sender
            //same sender:
            if (messageSenderId.equals(previousMessageSenderId)) {
                viewSender.visibility = View.GONE
            }
            // not same sender, display username:
            else{
                if (roomUsers != null) {
                    val sender = roomUsers?.get(messageSenderId)
                    Log.i(TAG, "senderName: " + sender)
                    if (sender != null) {
                        viewSender.setText(sender)
                        viewSender.visibility = View.VISIBLE
                    }
                } else{
                    Log.i(TAG, "RoomUsers is NULL")
                }
            }
        }

        setMessage(holder, message)

    }


    private fun setMessage(
        holder: ChatAdapter.ViewHolder, message: Message
    ) {
        //show message text
        holder.messageView.setText(message.message)

        //show date
        val date = message.timestamp
        if(date !=  Constants.DEFAULT_TIME){
            val timestamp = Timestamp(date)

            val currentDate = SimpleDateFormat("dd.MM.yyyy").format(Date())
            val timestampDate = SimpleDateFormat("dd.MM.yyyy").format(timestamp)

            val sdf: SimpleDateFormat
            if(currentDate.equals(timestampDate)){ //message was sent on same day, only show hour and minute
                sdf = SimpleDateFormat(
                    "HH:mm",//"dd.MM.yyyy HH:mm",
                    Locale.getDefault()
                )
            } else{ // message was sent on different day, also show date
                sdf = SimpleDateFormat(
                    "HH:mm, MMM d, ''yy",//"dd.MM.yyyy HH:mm",
                    Locale.getDefault()
                )
            }
            val text: String = sdf.format(timestamp)
            holder.timestampView.setText(text)
        }


        // highlight own sended messages
        val ownId = SharedPrefManager.instance.getUserId() ?: ""
        if (message.sender.equals(ownId)) {
            holder.chatBubble.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.blue_pale
                )
            )
            holder.chatItemWhole.gravity = Gravity.END //own sent messages are aligned right
            holder.messageView.setTextAppearance(android.R.style.TextAppearance_Material_Body1)

        }
        //system messages
        else if(message.sender.equals(Constants.DEFAULT_MESSAGE_SENDER)){
            holder.chatBubble.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            //holder.chatBubble.cardElevation = 0.0f
            holder.chatItemWhole.gravity = Gravity.CENTER //system messages are aligned in center
            holder.messageView.setTextAppearance(android.R.style.TextAppearance_Material_Caption) // smaller and grey text
            holder.senderView.visibility = View.GONE
        }
        //other people's messages
        else{
            holder.chatItemWhole.gravity = Gravity.START
            holder.messageView.setTextAppearance(android.R.style.TextAppearance_Material_Body1)
            holder.chatBubble.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.yellow_green
                )
            )
        }

    }

    //can be used to  update data, but then the  chatview does not scroll to last message, so use new ChatAdapter to update
    fun updateData(messages: MutableList<Message>, roomUsernames: HashMap<String, String>?) {
        roomUsers = roomUsernames
        messagesList = messages
        notifyDataSetChanged()
    }

}