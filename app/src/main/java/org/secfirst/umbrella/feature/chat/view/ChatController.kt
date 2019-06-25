package org.secfirst.umbrella.feature.chat.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.matrix_first_screen.*
import kotlinx.android.synthetic.main.matrix_first_screen.view.*
import kotlinx.android.synthetic.main.matrix_register_screen.*
import kotlinx.android.synthetic.main.matrix_register_screen.view.*
import kotlinx.android.synthetic.main.matrix_sign_in_screen.*
import kotlinx.android.synthetic.main.matrix_sign_in_screen.view.*
import org.jetbrains.anko.toast
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.data.network.Chunk
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.chat.DaggerChatComponent
import org.secfirst.umbrella.feature.chat.interactor.ChatBaseInteractor
import org.secfirst.umbrella.feature.chat.presenter.ChatBasePresenter
import org.secfirst.umbrella.misc.makeLinks
import javax.inject.Inject


class ChatController : BaseController(), ChatView {

    @Inject
    internal lateinit var presenter: ChatBasePresenter<ChatView, ChatBaseInteractor>
    private lateinit var notifications: MutableList<Chunk>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.matrix_first_screen, container, false)

        view.toolbar.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.title = context.getString(R.string.chat_title)
        }

        view.signInInsteadButton.setOnClickListener {
            matrix_sign_in.visibility = View.VISIBLE
            matrix_register.visibility = View.GONE
        }

        view.createAccountButton.setOnClickListener {
            matrix_register.visibility = View.VISIBLE
            matrix_sign_in.visibility = View.GONE
        }

        view.registerButton.setOnClickListener { registerUserClick() }

        view.signInButton.setOnClickListener { loginClick() }

        view.reset_password.makeLinks(Pair(context.getString(R.string.change_password_link), View.OnClickListener {
            println("change password")
        }))

        presenter.onAttach(this)
        return view
    }

    override fun onAttach(view: View) {
        println(presenter.isLoggedIn())
        if (presenter.isLoggedIn())
            router.pushController(RouterTransaction.with(ChatGroupController()))
        super.onAttach(view)
    }

    override fun onInject() {
        DaggerChatComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    private fun registerUserClick() {
        val username = username.text.toString()
        val password = password.text.toString()
        val confirmPassword = confirm_password.text.toString()
        val email = email.text.toString()
        if (password == confirmPassword)
            presenter.submitRegisterUser(username, password, email)
        else
            context.toast(context.getString(R.string.confirm_password_error_message))
        userID_error.visibility = View.INVISIBLE
    }

    private fun loginClick() {
        val username = user.text.toString()
        val password = pwd.text.toString()
        presenter.submitLogin(username, password)
    }

    override fun showUserRegistrationError() {
        userID_error.visibility = View.VISIBLE
    }

    override fun regSuccess(username: String) {
        context.toast("Welcome $username")
    }

    override fun logInSuccess(username: String, contacts: MutableList<String>, notifications: MutableList<Chunk>) {
        this.notifications = notifications
        if (notifications.isNotEmpty())
            context.toast("You have ${notifications.size} new invites")
        router.pushController(RouterTransaction.with(ChatGroupController()))
    }

    override fun regError() {
        context.toast("Error registering")
    }

    override fun logInError() {
        context.toast("Error Login")
    }


}