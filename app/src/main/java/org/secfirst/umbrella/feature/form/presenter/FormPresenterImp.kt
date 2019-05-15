package org.secfirst.umbrella.feature.form.presenter

import org.secfirst.umbrella.data.VirtualStorage
import org.secfirst.umbrella.data.database.form.ActiveForm
import org.secfirst.umbrella.data.database.form.Answer
import org.secfirst.umbrella.data.database.form.Form
import org.secfirst.umbrella.data.database.form.asHTML
import org.secfirst.umbrella.data.disk.getCurrentLanguageId
import org.secfirst.umbrella.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.feature.form.interactor.FormBaseInteractor
import org.secfirst.umbrella.feature.form.view.FormView
import org.secfirst.umbrella.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.misc.FORM_HOST
import org.secfirst.umbrella.misc.deepLinkIdentifier
import org.secfirst.umbrella.misc.launchSilent
import javax.inject.Inject


class FormPresenterImp<V : FormView, I : FormBaseInteractor>
@Inject internal constructor(
        private val virtualStorage: VirtualStorage,
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), FormBasePresenter<V, I> {


    override fun submitFormByURI(uriString: String) {
        launchSilent(uiContext) {
            interactor?.let {
                val uriWithoutHost = uriString.substringAfterLast("$FORM_HOST/")
                val uriSplitted = uriWithoutHost.split("/")
                val formName = uriSplitted.last().deepLinkIdentifier()
                val form = it.fetchForm(formName)
                form?.let { safeForm -> getView()?.openSpecificForm(safeForm) }
            }
        }
    }

    override fun submitShareFormHtml(activeForm: ActiveForm) {
        launchSilent(uiContext) {
            val shareFile = virtualStorage.mountFilesystem(activeForm.asHTML(), activeForm.title)

            getView()?.showShareForm(shareFile)
        }
    }

    override fun submitDeleteActiveForm(activeForm: ActiveForm) {
        launchSilent(uiContext) {
            interactor?.deleteActiveForm(activeForm)
        }
    }

    override fun submitActiveForm(activeForm: ActiveForm) {
        launchSilent(uiContext) {
            val res = interactor?.insertActiveForm(activeForm)
            res?.let {
                getView()?.showActiveFormWLoad(it)
            }
        }
    }

    override fun submitLoadAllForms() {
        launchSilent(uiContext) {
            interactor?.let {
                val activeForms = it.fetchActiveForms()
                val languageId = getCurrentLanguageId(it.getDefaultLanguage())
                val modelForms = it.fetchModalForms(languageId)
                populateReferenceId(activeForms, modelForms)

                getView()?.showModelAndActiveForms(modelForms.toMutableList(),
                        activeForms.toMutableList())
            }
        }
    }

    private fun populateReferenceId(activeForms: List<ActiveForm>, modelForms: List<Form>) {
        activeForms.forEach { activeForm ->
            modelForms.forEach { modelForm ->
                if (activeForm.sha1Form == modelForm.path)
                    activeForm.form = modelForm
            }
        }
    }

    override fun submitInsert(answer: Answer) {
        launchSilent(uiContext) {
            interactor?.insertFormData(answer)
        }
    }
}