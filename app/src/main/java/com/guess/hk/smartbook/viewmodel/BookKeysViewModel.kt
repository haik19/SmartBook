package com.guess.hk.smartbook.viewmodel

import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.TaskExecutors
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.core.Repo
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.guess.hk.smartbook.db.BooksDataManager
import com.guess.hk.smartbook.model.BookKey
import com.guess.hk.smartbook.repo.KeysRepo
import com.guess.hk.smartbook.repo.KeysRepoImpl
import com.guess.hk.smartbook.repo.Resource
import com.guess.hk.smartbook.usecase.GetVersionUseCase
import com.guess.hk.smartbook.usecase.RecognizeKeyUseCase
import java.security.KeyRep

class BookKeysViewModel : ViewModel() {

    private val booksDataManager = BooksDataManager()
    private val repo: KeysRepo = KeysRepoImpl()
    private val keysLiveData = MutableLiveData<Resource<List<BookKey>>>()

    private val recognizeKeyUseCase = RecognizeKeyUseCase()
    val recognizedKeyLiveData = MediatorLiveData<Resource<List<String>>>()

    val versionLiveData = MediatorLiveData<Resource<String>>()


    fun getKeysFromeNet() {

    }

    fun recognizeKey(firebaseVisionImage: FirebaseVisionImage) {
        val liveData = recognizeKeyUseCase.recognizeText(firebaseVisionImage)
        TaskExecutors.MAIN_THREAD.execute {
            recognizedKeyLiveData.addSource(liveData) {
                recognizedKeyLiveData.postValue(it)
                recognizedKeyLiveData.removeSource(liveData)
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

}