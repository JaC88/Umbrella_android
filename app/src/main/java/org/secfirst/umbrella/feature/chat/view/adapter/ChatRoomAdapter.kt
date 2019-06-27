package org.secfirst.umbrella.feature.chat.view.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.chat_item_message_received.view.*
import kotlinx.android.synthetic.main.chat_item_message_received.view.text_message_time
import kotlinx.android.synthetic.main.chat_item_message_sent.view.*
import kotlinx.android.synthetic.main.chat_item_message_sent.view.text_message_body
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.network.Chunk
import org.secfirst.umbrella.misc.toContactName
import org.secfirst.umbrella.misc.toDate
import org.secfirst.umbrella.misc.toMatrixUsername

class ChatRoomAdapter(private val messageList: List<Chunk>,
                      private val user: String,
                      private val messageClick: (Chunk) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].sender.equals(user.toMatrixUsername())) VIEW_TYPE_MESSAGE_SENT
        else VIEW_TYPE_MESSAGE_RECEIVED
    }


    override fun getItemCount() = messageList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_item_message_sent, parent, false)
            return SentMessageHolder(view)
        } else {
            view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_item_message_received, parent, false)
            return ReceivedMessageHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                holder as ReceivedMessageHolder
                holder.bind(messageList[position], clickListener = { messageClick(messageList[position]) })
            }
            VIEW_TYPE_MESSAGE_SENT -> {
                holder as SentMessageHolder
                holder.bind(messageList[position], clickListener = { messageClick(messageList[position]) })
            }
        }
    }

    class SentMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(message: Chunk, clickListener: (SentMessageHolder) -> Unit) {
            with(message) {
                itemView.text_message_body.text = this.content.body
                itemView.text_message_time.text = this.origin_server_ts.toDate()
                if (message.content.msgtype == "m.file")
                    itemView.text_message_body.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    itemView.setOnClickListener { clickListener(this@SentMessageHolder) }
            }
        }
    }

    class ReceivedMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: Chunk, clickListener: (ReceivedMessageHolder) -> Unit) {
            with(message) {
                itemView.text_message_name.text = this.sender.toContactName()
                itemView.text_message_body.text = this.content.body
                itemView.text_message_time.text = this.origin_server_ts.toDate()
                if (message.content.msgtype == "m.file") {
                    itemView.text_message_body.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    itemView.setOnClickListener { clickListener(this@ReceivedMessageHolder) }
                }
            }
        }
    }

    companion object {
        const val VIEW_TYPE_MESSAGE_SENT = 1
        const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }
}