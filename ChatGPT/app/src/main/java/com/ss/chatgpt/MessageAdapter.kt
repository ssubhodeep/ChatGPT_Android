package com.ss.chatgpt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ss.chatgpt.databinding.MessageItemBinding

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ItemViewHolder>() {
    private var messageList: ArrayList<MessageModel> = ArrayList()


    fun message(mList: ArrayList<MessageModel>) {
        messageList = mList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val message = messageList[position]
        if (message.sentBy.equals(MessageModel.SENT_BY_USER)) {
            holder.binding.apply {
                leftChatView.visibility = View.GONE
                rightChatView.visibility = View.VISIBLE
                rightTextView.text = message.getMessage()
            }
        } else {
            holder.binding.apply {
                rightChatView.visibility = View.GONE
                leftChatView.visibility = View.VISIBLE
                leftTextView.text = message.getMessage()
            }

        }

    }


    override fun getItemCount(): Int {
        return messageList.size

    }

    class ItemViewHolder(itemBinding: MessageItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding = itemBinding

    }

}