package org.secfirst.umbrella.feature.base.interactor


import org.secfirst.umbrella.data.database.content.ContentRepo
import org.secfirst.umbrella.data.network.ApiHelper
import org.secfirst.umbrella.data.network.MatrixApiHelper
import org.secfirst.umbrella.data.preferences.AppPreferenceHelper

open class BaseInteractorImp() : BaseInteractor {

    protected lateinit var apiHelper: ApiHelper
    protected lateinit var matrixApiHelper: MatrixApiHelper
    protected lateinit var preferenceHelper: AppPreferenceHelper
    protected lateinit var contentRepo: ContentRepo

    constructor(apiHelper: ApiHelper, preferenceHelper: AppPreferenceHelper, contentRepo: ContentRepo) : this() {
        this.apiHelper = apiHelper
        this.preferenceHelper = preferenceHelper
        this.contentRepo = contentRepo
    }

    constructor(preferenceHelper: AppPreferenceHelper, matrixApiHelper: MatrixApiHelper) : this() {
        this.preferenceHelper = preferenceHelper
        this.matrixApiHelper = matrixApiHelper
    }

    constructor(apiHelper: ApiHelper, preferenceHelper: AppPreferenceHelper, contentRepo: ContentRepo, matrixApiHelper: MatrixApiHelper): this(){
        this.apiHelper = apiHelper
        this.preferenceHelper = preferenceHelper
        this.contentRepo = contentRepo
        this.matrixApiHelper = matrixApiHelper
    }

    override suspend fun resetContent(): Boolean {
        val res: Boolean = contentRepo.resetContent()
        preferenceHelper.setLoggedIn(false)
        preferenceHelper.setSkipPassword(false)
        preferenceHelper.setMaskApp(false)
        return res
    }

    override fun setDefaultLanguage(isoCountry: String) = preferenceHelper.setLanguage(isoCountry)

    override fun getDefaultLanguage() = preferenceHelper.getLanguage()

    override fun setSkipPassword(isSkip: Boolean) = preferenceHelper.setSkipPassword(isSkip)

    override fun isSkippPassword(): Boolean = preferenceHelper.getSkipPassword()

    override fun enablePasswordBanner(enableBanner: Boolean) = preferenceHelper.enablePasswordBanner(enableBanner)

    override fun isUserLoggedIn() = preferenceHelper.isLoggedIn()

    override fun setLoggedIn(isLogged: Boolean) = preferenceHelper.setLoggedIn(isLogged)
}
