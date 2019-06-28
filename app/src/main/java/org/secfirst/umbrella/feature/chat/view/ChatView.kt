package org.secfirst.umbrella.feature.chat.view

import org.secfirst.umbrella.data.database.checklist.Dashboard
import org.secfirst.umbrella.data.database.form.ActiveForm
import org.secfirst.umbrella.data.database.matrix_account.Contact
import org.secfirst.umbrella.data.network.Chunk
import org.secfirst.umbrella.feature.base.view.BaseView
import java.io.File

interface ChatView : BaseView {

    fun showUserRegistrationError() {}

    fun regSuccess(username: String) {}

    fun logInSuccess(username: String, notifications: MutableList<Chunk>) {}

    fun regError() {}

    fun logInError() {}

    fun showRoomMessages(messageList: List<Chunk>, user: String) {}

    fun showContacts(contacts: MutableList<Contact>) {}

    fun updateContacts(contact: Contact) {}

    fun showItemToShare(allDashboard: MutableList<Dashboard.Item>, activeForms: MutableList<ActiveForm>) {}

    fun fileUploadSuccess(uriMCX: String, filename: String) {}

    fun fileDownloadSuccess() {}

    fun shareFormView(file: File){}
}