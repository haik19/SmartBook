package com.guess.hk.smartbook.viewmodel

import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.guess.hk.smartbook.db.BooksDataManager
import com.guess.hk.smartbook.model.BookKey
import com.guess.hk.smartbook.repo.Resource
import com.guess.hk.smartbook.usecase.RecognizeKeyUseCase

class KeysViewModel : ViewModel() {

    private val booksDataManager = BooksDataManager()
    private val keysLiveData = MutableLiveData<Resource<List<BookKey>>>()
    private val versionLiveData = MutableLiveData<Resource<String>>()

    private val recognizeKeyUseCase = RecognizeKeyUseCase()
    private val recognizedKeyLiveData = MediatorLiveData<Resource<List<String>>>()


    fun getKeysFromeNet() {

    }

    fun getVersion() {

    }

    fun recognizeKey(firebaseVisionImage: FirebaseVisionImage) {
        val liveData = recognizeKeyUseCase.recognizeText(firebaseVisionImage);
        recognizedKeyLiveData.addSource(liveData) {
            recognizedKeyLiveData.postValue(it)
            recognizedKeyLiveData.removeSource(liveData)
        }
    }

}