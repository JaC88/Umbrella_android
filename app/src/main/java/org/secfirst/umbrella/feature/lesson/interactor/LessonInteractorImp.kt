package org.secfirst.umbrella.feature.lesson.interactor

import org.secfirst.umbrella.data.database.lesson.LessonRepo
import org.secfirst.umbrella.data.database.lesson.Module
import org.secfirst.umbrella.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class LessonInteractorImp @Inject constructor(private val lessonRepo: LessonRepo, preferenceHelper: AppPreferenceHelper) : BaseInteractorImp(preferenceHelper), LessonBaseInteractor {

    override suspend fun fetchDifficultyBySubject(subjectId: String) = lessonRepo.loadDifficultyBySubject(subjectId)

    override suspend fun fetchAllFavorites() = lessonRepo.loadAllFavoriteSubjects()

    override suspend fun fetchMarkdownBySubject(subjectId: String) = lessonRepo.loadMarkdownBySubject(subjectId)

    override suspend fun fetchLesson(moduleId: String) = lessonRepo.loadLessonBy(moduleId)

    override suspend fun fetchDifficultyPreferredBy(subjectId: String) = lessonRepo.loadDifficultyPreferredBy(subjectId)

    override suspend fun fetchModules(languageId: Int): List<Module> = lessonRepo.loadAllModules(languageId)
}