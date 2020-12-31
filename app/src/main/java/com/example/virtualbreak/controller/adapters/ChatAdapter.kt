package com.example.virtualbreak.controller.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualbreak.R
import com.example.virtualbreak.controller.communication.PullData
import com.example.virtualbreak.model.Message

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
        private val TAG: String = "ChatAdapter_ViewHolder"

        init {
            messageView = itemView.findViewById(R.id.show_chat_message)
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
        val view = holder.messageView
        Log.i(TAG, "message: " + message)

        view.setText(message.message)


        // highlight own sended messages
        val ownId = PullData.currentUser?.value?.uid
        if (message.sender.equals(ownId)){
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow_green))
        }



    }

}