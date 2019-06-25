package org.secfirst.umbrella.feature.chat.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.host_checklist.view.toolbar
import kotlinx.android.synthetic.main.matrix_chat_group_view.*
import kotlinx.android.synthetic.main.matrix_chat_group_view.view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.chat.DaggerChatComponent
import org.secfirst.umbrella.feature.chat.interactor.ChatBaseInteractor
import org.secfirst.umbrella.feature.chat.presenter.ChatBasePresenter
import org.secfirst.umbrella.feature.chat.view.adapter.ChatGroupAdapter
import org.secfirst.umbrella.misc.initHorizontalRecyclerView
import javax.inject.Inject

class ChatGroupController() : BaseController(), ChatView {

    @Inject
    internal lateinit var presenter: ChatBasePresenter<ChatView, ChatBaseInteractor>
    private lateinit var adapter: ChatGroupAdapter
    //    private val contacts by lazy { args.getStringArrayList("contacts") }
    private val onContactClick: (String) -> Unit = this::onContactClick

//    constructor(contacts: MutableList<String>) : this(Bundle().apply {
//        putStringArrayList("contacts", ArrayList(contacts))
//    })

    override fun onInject() {
        DaggerChatComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.matrix_chat_group_view, container, false)

        view.toolbar.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.title = context.getString(R.string.chat_title)
        }
        view.addContactButton.setOnClickListener { addContact() }
        presenter.onAttach(this)

        return view
    }

    override fun onAttach(view: View) {
        presenter.submitUploadFile(context)
        presenter.submitLoadContacts()
        super.onAttach(view)
    }

    override fun showContacts(contacts: MutableList<String>) {
        adapter = ChatGroupAdapter(contacts, onContactClick)
        chat_group_recyclerView?.initHorizontalRecyclerView(adapter)
    }

    private fun onContactClick(room_id: String) {
        router.pushController(RouterTransaction.with(ChatRoomController(room_id)))
    }

    private fun addContact() {
        presenter.submitCreateRoom("Ola")
    }
}