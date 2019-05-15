package org.secfirst.umbrella.feature.form.interactor

import org.secfirst.umbrella.data.database.form.ActiveForm
import org.secfirst.umbrella.data.database.form.Answer
import org.secfirst.umbrella.data.database.form.FormRepo
import org.secfirst.umbrella.data.database.form.Screen
import org.secfirst.umbrella.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class FormInteractorImp @Inject constructor(private val formRepo: FormRepo, preferenceHelper: AppPreferenceHelper) : BaseInteractorImp(preferenceHelper), FormBaseInteractor {

    override suspend fun fetchForm(formTitle: String) = formRepo.loadForm(formTitle)

    override suspend fun deleteActiveForm(activeForm: ActiveForm) = formRepo.removeActiveForm(activeForm)

    override suspend fun fetchAnswerBy(formId: Long): List<Answer> = formRepo.loadAnswerBy(formId)

    override suspend fun fetchScreenBy(sh1ID: String): List<Screen> = formRepo.loadScreenBy(sh1ID)

    override suspend fun insertActiveForm(activeForm: ActiveForm) = formRepo.persistActiveForm(activeForm)

    override suspend fun fetchActiveForms(): List<ActiveForm> = formRepo.loadActiveForms()

    override suspend fun insertFormData(answer: Answer) = formRepo.persistFormData(answer)

    override suspend fun fetchModalForms(languageId: Int) = formRepo.loadModelForms(languageId)
}