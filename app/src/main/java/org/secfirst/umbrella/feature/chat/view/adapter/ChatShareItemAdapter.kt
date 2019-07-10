//package org.secfirst.umbrella.feature.chat.view.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import kotlinx.android.synthetic.main.matrix_share_item.view.*
//import org.secfirst.umbrella.R
//
//class ChatShareItemAdapter(private val items: MutableList<String>,
//                           private val onItemClicked: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    override fun getItemCount(): Int = items.size
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.matrix_share_item, parent, false)
//        return ChatShareItemHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        holder as ChatShareItemHolder
//        holder.bind(items[position], clickListener = { onItemClicked(items[position]) })
//    }
//
//    class ChatShareItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        fun bind(item: String, clickListener: (ChatShareItemHolder) -> Unit) {
//
//            with(item) {
//                itemView.item_type.text = this
//                itemView.item_type.setOnClickListener { clickListener(this@ChatShareItemHolder) }
//            }
//
//        }
//    }
//}