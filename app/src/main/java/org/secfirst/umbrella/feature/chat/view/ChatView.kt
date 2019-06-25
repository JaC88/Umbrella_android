package org.secfirst.umbrella.feature.chat.view

import org.secfirst.umbrella.data.network.Chunk
import org.secfirst.umbrella.feature.base.view.BaseView

interface ChatView : BaseView {

    fun showUserRegistrationError() {}

    fun regSuccess(username: String) {}

    fun logInSuccess(username: String, contacts: MutableList<String>, notifications: MutableList<Chunk>) {}

    fun regError() {}

    fun logInError() {}

    fun showRoomMessages(messageList: List<Chunk>, user: String) {}

    fun showContacts(contacts: MutableList<String>) {}
}