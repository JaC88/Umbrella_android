package org.secfirst.umbrella.feature.chat.view

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.chat_room_view.*
import kotlinx.android.synthetic.main.chat_room_view.view.*
import org.jetbrains.anko.toast
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.checklist.Dashboard
import org.secfirst.umbrella.data.database.checklist.covertToHTML
import org.secfirst.umbrella.data.network.Chunk
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.chat.DaggerChatComponent
import org.secfirst.umbrella.feature.chat.interactor.ChatBaseInteractor
import org.secfirst.umbrella.feature.chat.presenter.ChatBasePresenter
import org.secfirst.umbrella.feature.chat.view.adapter.ChatRoomAdapter
import org.secfirst.umbrella.feature.checklist.view.adapter.DashboardAdapter
import org.secfirst.umbrella.misc.createDocument
import org.secfirst.umbrella.misc.initRecyclerView
import org.secfirst.umbrella.misc.requestExternalStoragePermission
import javax.inject.Inject

class ChatRoomController(bundle: Bundle) : BaseController(bundle), ChatView {

    @Inject
    internal lateinit var presenter: ChatBasePresenter<ChatView, ChatBaseInteractor>
    private val room_id by lazy { args.getString("room_id") }
    private val contact_name by lazy { args.getString("contact_name") }
    private lateinit var adapter: ChatRoomAdapter
    private lateinit var messages: List<Chunk>
    private lateinit var user: String
    private lateinit var shareDialog: AlertDialog
    private lateinit var shareDialogView: View
    private var items = mutableListOf<Dashboard.Item>()
    private lateinit var itemsRv: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var itemAdapter: DashboardAdapter
    private val dashboardItemClick: (Checklist) -> Unit = this::onItemClicked
    private val shareChecklistClick: (Checklist) -> Unit = this::onChecklistShareClick
    private val starPathwaysClick: (Checklist, Int) -> Unit = this::onPathwaysStarClick
    private val footerClick: () -> Unit = this::onFooterClick
    private val messageClick: (Chunk) -> Unit = this::onMessageClick


    override fun onInject() {
        DaggerChatComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    constructor(room_id: String, contact_name: String) : this(Bundle().apply {
        putString("room_id", room_id)
        putString("contact_name", contact_name)
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        val view = inflater.inflate(R.layout.chat_room_view, container, false)
        shareDialogView = inflater.inflate(R.layout.item_to_share_view, container, false)

        shareDialog = AlertDialog
                .Builder(activity)
                .setView(shareDialogView)
                .create()

        itemAdapter = DashboardAdapter(items, dashboardItemClick, shareChecklistClick, starPathwaysClick, footerClick)

        linearLayoutManager = LinearLayoutManager(shareDialog.context)
        view.button_attach.setOnClickListener { checkPermission() }
        view.button_chatbox_send.setOnClickListener { sendMessage() }
        presenter.onAttach(this)

        return view

    }

    override fun onAttach(view: View) {
        mainActivity.hideNavigation()
        if (!::messages.isInitialized)
            presenter.submitShowRoomMessages(room_id)
        else {
            adapter = ChatRoomAdapter(messages, user, messageClick)
            recyclerview_message_list?.initRecyclerView(adapter)
            recyclerview_message_list.scrollToPosition(adapter.itemCount - 1)
        }

        recyclerview_message_list?.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                recyclerview_message_list.scrollBy(0, oldBottom - bottom);
            }
        }
        view.chat_name?.text = contact_name
        super.onAttach(view)
    }

    override fun onDetach(view: View) {
        mainActivity.showNavigation()
        super.onDetach(view)
    }

    override fun showRoomMessages(messageList: List<Chunk>, user: String) {
        messages = messageList
        this.user = user
        adapter = ChatRoomAdapter(messages, user, messageClick)
        recyclerview_message_list?.initRecyclerView(adapter)
        recyclerview_message_list.scrollToPosition(adapter.itemCount - 1)
    }

    private fun sendMessage() {
        presenter.submitSendMessage(room_id, edittext_chatbox.text.toString(), "", "m.text")
        edittext_chatbox.text.clear()
    }

    private fun attachFile() {
        presenter.submitLoadItemToShare()

    }

    override fun showItemToShare(allDashboard: MutableList<Dashboard.Item>) {
        shareDialog.show()
        items.clear()
        items.addAll(allDashboard)
        itemAdapter.notifyDataSetChanged()
        itemsRv = shareDialog.findViewById(R.id.itemToShareRecyclerView)
        itemsRv.layoutManager = linearLayoutManager
        itemsRv.setHasFixedSize(true)
        itemsRv.adapter = itemAdapter
    }

    private fun onItemClicked(checklist: Checklist) {
        val checklistHtml = checklist.covertToHTML()
        val doc = Jsoup.parse(checklistHtml)
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        val fileToShare = createDocument(doc, context.getString(R.string.checklistDetail_title), context.getString(R.string.pdf_name), context)
        presenter.submitUploadFile(fileToShare, context)
    }


    override fun fileUploadSuccess(uriMCX: String, filename: String) {
        presenter.submitSendMessage(room_id, filename, uriMCX, "m.file")
        shareDialog.dismiss()

    }

    override fun fileDownloadSuccess() {
        context.toast("Download completed")
    }

    private fun onMessageClick(message: Chunk) {
        presenter.submitDownloadFile(context, message.content.url)

    }

    private fun checkPermission() {
        Permissions.check(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, null, object : PermissionHandler() {
            override fun onGranted() {
                showAttachDialog()
            }
        })
    }

    private fun showAttachDialog() {
        if (ContextCompat.checkSelfPermission(mainActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Handler().postDelayed({
                attachFile()
            }, 500)
        } else {
            mainActivity.requestExternalStoragePermission()
        }
    }


    private fun onChecklistShareClick(checklist: Checklist) {

    }

    private fun onPathwaysStarClick(checklist: Checklist, position: Int) {

    }

    private fun onFooterClick() {}
}