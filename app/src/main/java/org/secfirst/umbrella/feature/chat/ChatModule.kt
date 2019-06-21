package org.secfirst.umbrella.feature.chat

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import org.secfirst.umbrella.di.module.AppModule
import org.secfirst.umbrella.di.module.MatrixNetworkModule
import org.secfirst.umbrella.di.module.RepositoryModule
import org.secfirst.umbrella.feature.chat.interactor.ChatBaseInteractor
import org.secfirst.umbrella.feature.chat.interactor.ChatInteractorImp
import org.secfirst.umbrella.feature.chat.presenter.ChatBasePresenter
import org.secfirst.umbrella.feature.chat.presenter.ChatPresenterImp
import org.secfirst.umbrella.feature.chat.view.ChatController
import org.secfirst.umbrella.feature.chat.view.ChatGroupController
import org.secfirst.umbrella.feature.chat.view.ChatRoomController
import org.secfirst.umbrella.feature.chat.view.ChatView
import javax.inject.Singleton

@Module
class ChatModule {

    @Provides
    internal fun provideChatInteractor(interactor: ChatInteractorImp): ChatBaseInteractor = interactor

    @Provides
    internal fun provideChatPresenter(presenter: ChatPresenterImp<ChatView, ChatBaseInteractor>)
            : ChatBasePresenter<ChatView, ChatBaseInteractor> = presenter
}

@Singleton
@Component(modules = [ChatModule::class,
    RepositoryModule::class,
    AppModule::class,
    MatrixNetworkModule::class,
    AndroidInjectionModule::class])

interface ChatComponent {
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ChatComponent
    }

    fun inject(chatController: ChatController)

    fun inject(chatGroupController: ChatGroupController)

    fun inject(chatRoomController: ChatRoomController)

}