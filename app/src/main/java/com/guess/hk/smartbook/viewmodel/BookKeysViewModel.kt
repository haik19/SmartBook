package com.guess.hk.smartbook.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.guess.hk.smartbook.db.BookKeyDao
import com.guess.hk.smartbook.model.BookKey
import com.guess.hk.smartbook.repo.KeysRepo
import com.guess.hk.smartbook.repo.KeysRepoImpl
import com.guess.hk.smartbook.repo.Resource
import com.guess.hk.smartbook.usecase.DataAvailableUseCase
import com.guess.hk.smartbook.usecase.GetVersionUseCase
import com.guess.hk.smartbook.usecase.RecognizeKeyUseCase

class BookKeysViewModel : ViewModel() {

    private val repo: KeysRepo = KeysRepoImpl()

    private val recognizeKeyUseCase = RecognizeKeyUseCase(repo)
    val recognizedKeyLiveData = MediatorLiveData<Resource<BookKey>>()

    val versionLiveData = MediatorLiveData<Resource<Long>>()

    val dataAvailableLiveData = MediatorLiveData<Resource<String>>()

    fun recognizeKey(fireBaseVisionImage: FirebaseVisionImage) {
        val liveData = recognizeKeyUseCase.recognizeText(fireBaseVisionImage)
        TaskExecutors.MAIN_THREAD.execute {
            recognizedKeyLiveData.addSource(liveData) {
                recognizedKeyLiveData.postValue(it)
                recognizedKeyLiveData.removeSource(liveData)
            }
        }
    }

    fun checkData(isDataChanged: Boolean) {
        val dataAvailableUseCase = DataAvailableUseCase(repo, isDataChanged)
        val liveData = dataAvailableUseCase.fetChData()
        dataAvailableLiveData.addSource(liveData) {
            dataAvailableLiveData.postValue(it)
            if (it !is Resource.Loading) {
                dataAvailableLiveData.removeSource(liveData)
            }
        }
    }

    fun getVersion() {
        val getVersionUseCase = GetVersionUseCase(repo)
        val liveData = getVersionUseCase.getVersion()
        versionLiveData.addSource(liveData) {
            versionLiveData.postValue(it)
            versionLiveData.removeSource(liveData)
        }
    }

    fun initDb(booksDao: BookKeyDao) {
        val im = repo as KeysRepoImpl //TODO CHANGE
        im.bookKeyDao = booksDao
    }

}