package org.secfirst.umbrella.feature.chat.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.chat_room_view.*
import kotlinx.android.synthetic.main.chat_room_view.view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.data.network.Chunk
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.chat.DaggerChatComponent
import org.secfirst.umbrella.feature.chat.interactor.ChatBaseInteractor
import org.secfirst.umbrella.feature.chat.presenter.ChatBasePresenter
import org.secfirst.umbrella.feature.chat.view.adapter.ChatRoomAdapter
import org.secfirst.umbrella.misc.initRecyclerView
import javax.inject.Inject

class ChatRoomController(bundle: Bundle) : BaseController(bundle), ChatView {

    @Inject
    internal lateinit var presenter: ChatBasePresenter<ChatView, ChatBaseInteractor>
    private val room_id by lazy { args.getString("room_id") }
    private lateinit var adapter: ChatRoomAdapter

    override fun onInject() {
        DaggerChatComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    constructor(room_id: String) : this(Bundle().apply {
        putString("room_id", room_id)
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        val view = inflater.inflate(R.layout.chat_room_view, container, false)

        view.button_chatbox_send.setOnClickListener { sendMessage() }
        presenter.onAttach(this)
        presenter.submitShowRoomMessages(room_id)
        return view

    }

    override fun onAttach(view: View) {
        mainActivity.hideNavigation()
        super.onAttach(view)
    }

    override fun onDetach(view: View) {
        mainActivity.showNavigation()
        super.onDetach(view)
    }

    override fun showRoomMessages(messageList: List<Chunk>, user: String) {
        adapter = ChatRoomAdapter(messageList, user)
        recyclerview_message_list?.initRecyclerView(adapter)
    }

    private fun sendMessage() {
        presenter.submitSendMessage(room_id, edittext_chatbox.text.toString())
        edittext_chatbox.text.clear()
    }
}