package org.secfirst.umbrella.feature.account.view

import org.secfirst.umbrella.data.database.reader.FeedLocation
import org.secfirst.umbrella.data.database.reader.FeedSource
import org.secfirst.umbrella.feature.base.view.BaseView
import java.io.File

interface AccountView : BaseView {

    fun isUserLogged(res: Boolean) {}

    fun isTokenChanged(res: Boolean) {}

    fun exportDatabaseSuccessfully() {}

    fun onShareContent(backupFile: File) {}

    fun onImportBackupSuccess() {}

    fun onImportBackupFail() {}

    fun loadDefaultValue(feedLocation: FeedLocation?, refreshFeedInterval: Int, feedSource: List<FeedSource>) {}

    fun onSwitchServer(isSwitch: Boolean) {}

    fun getSkipPassword(res: Boolean) {}

    fun onResetContent(res: Boolean) {}

    fun getDefaultLanguage(isoCountry: String){}

    fun getMaskApp(isMaskApp : Boolean){}

    fun onChangedLanguageSuccess(){}

    fun onChangedLanguageFail(){}

    fun matrixLogout(username: String) {}
}
