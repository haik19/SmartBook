package com.guess.hk.smartbook.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.guess.hk.smartbook.model.BookKey
import com.guess.hk.smartbook.repo.KeysRepo
import com.guess.hk.smartbook.repo.Resource
import java.util.concurrent.Executors

class RecognizeKeyUseCase(val repo: KeysRepo) {

    private val detector = FirebaseVision.getInstance()
        .onDeviceTextRecognizer

    fun recognizeText(visionImage: FirebaseVisionImage): LiveData<Resource<BookKey>> {
        val liveData = MutableLiveData<Resource<BookKey>>()

        if (repo.getBookKeys().isEmpty()){ //TODO refactor
            liveData.postValue(Resource.Error("Something went wrong"))
            return liveData
        }
        val executor = Executors.newCachedThreadPool()
        detector.processImage(visionImage).addOnSuccessListener { it ->
            executor.execute {
                if (it.text != null) {
                    val textKey = it.text.replace("\n", " ")
                    val book = findBookById(textKey)
                    if (book == null) {
                        liveData.postValue(Resource.Error("Something went wrong"))
                    } else {
                        liveData.postValue(Resource.Success(book))
                    }
                } else {
                    liveData.postValue(Resource.Error("Something went wrong"))
                }
            }
        }.addOnFailureListener {
            liveData.postValue(Resource.Error(it.message,null))
        }
        return liveData
    }

    private fun findBookById(key: String): BookKey? {
        for (book in repo.getBookKeys()) { //TODO change with leneshteyin algorithm
            if (key.contains(book.id)) {
                return book
            }
        }
        return null
    }

}