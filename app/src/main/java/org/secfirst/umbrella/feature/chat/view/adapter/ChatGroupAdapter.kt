package org.secfirst.umbrella.feature.chat.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.matrix_contact_item.view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.database.matrix_account.Contact

class ChatGroupAdapter(private val contacts: MutableList<Contact>,
                       private val onContactClicked: (Contact) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

        fun bind(contact: Contact, clickListener: (ChatGroupHolder) -> Unit) {

            with(contact) {
                itemView.contact_name.text = this.name
                itemView.contact_title.text = this.name[0].toString().capitalize()
                itemView.contact.setOnClickListener { clickListener(this@ChatGroupHolder) }
            }

        }
    }
}