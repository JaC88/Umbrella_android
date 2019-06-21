package org.secfirst.umbrella.feature.chat.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.matrix_contact_item.view.*
import org.secfirst.umbrella.R

class ChatGroupAdapter(private val contacts: MutableList<String>,
                       private val onContactClicked: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = contacts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.matrix_contact_item, parent, false)
        return ChatGroupHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ChatGroupHolder
        holder.bind(contacts[position], clickListener = { onContactClicked(contacts[position]) })
    }

    class ChatGroupHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(contact: String, clickListener: (ChatGroupHolder) -> Unit) {

            with(contact) {
                itemView.contact_name.text = this
                itemView.contact_title.text = this[0].toString().capitalize()
                itemView.contact.setOnClickListener { clickListener(this@ChatGroupHolder) }
            }

        }
    }
}