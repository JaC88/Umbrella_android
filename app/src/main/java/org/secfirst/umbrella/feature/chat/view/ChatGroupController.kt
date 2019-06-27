package org.secfirst.umbrella.feature.chat.view

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.add_contact_dialog.view.*
import kotlinx.android.synthetic.main.alert_control.view.*
import kotlinx.android.synthetic.main.host_checklist.view.toolbar
import kotlinx.android.synthetic.main.matrix_chat_group_view.*
import kotlinx.android.synthetic.main.matrix_chat_group_view.view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.data.database.matrix_account.Contact
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.chat.DaggerChatComponent
import org.secfirst.umbrella.feature.chat.interactor.ChatBaseInteractor
import org.secfirst.umbrella.feature.chat.presenter.ChatBasePresenter
import org.secfirst.umbrella.feature.chat.view.adapter.ChatGroupAdapter
import org.secfirst.umbrella.misc.initHorizontalRecyclerView
import javax.inject.Inject

class ChatGroupController : BaseController(), ChatView {

    @Inject
    internal lateinit var presenter: ChatBasePresenter<ChatView, ChatBaseInteractor>
    private lateinit var adapter: ChatGroupAdapter
    private lateinit var contactsArray: MutableList<Contact>
    private val onContactClick: (Contact) -> Unit = this::onContactClick
    private lateinit var addContactDialog: AlertDialog
    private lateinit var addContactView: View

    override fun onInject() {
        DaggerChatComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.matrix_chat_group_view, container, false)
        addContactView = inflater.inflate(R.layout.add_contact_dialog, container, false)

        view.toolbar.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.title = context.getString(R.string.chat_title)
        }

        addContactDialog = AlertDialog
                .Builder(activity)
                .setView(addContactView)
                .create()

        view.addContactButton.setOnClickListener { showAddContactDialog() }
        addContactView.alertControlOk.setOnClickListener { addContact() }
        addContactView.alertControlCancel.setOnClickListener { addContactDialog.dismiss() }
        presenter.onAttach(this)
        presenter.submitLoadContacts()

        return view
    }

    override fun onAttach(view: View) {
        if (!::contactsArray.isInitialized)
            presenter.submitLoadContacts()
        else {
            adapter = ChatGroupAdapter(contactsArray, onContactClick)
            chat_group_recyclerView?.initHorizontalRecyclerView(adapter)
        }
        super.onAttach(view)
    }

    override fun showContacts(contacts: MutableList<Contact>) {
        contactsArray = contacts
        adapter = ChatGroupAdapter(contactsArray, onContactClick)
        chat_group_recyclerView?.initHorizontalRecyclerView(adapter)
    }

    private fun onContactClick(contact: Contact) {
        router.pushController(RouterTransaction.with(ChatRoomController(contact.room_id, contact.name)))
    }

    private fun addContact() {
        val contactName = addContactView.contact_name.text.toString()
        presenter.submitCreateRoom(contactName)
        addContactDialog.dismiss()
    }

    override fun updateContacts(contact: Contact) {
        contactsArray.add(contact)
        adapter.notifyDataSetChanged()

    }

    private fun showAddContactDialog() {
        addContactView.alertControlOk.text = "Add"
        addContactView.alertControlCancel.text = "Cancel"
        addContactDialog.show()
    }
}