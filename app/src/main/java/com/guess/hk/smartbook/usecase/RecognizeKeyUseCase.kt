package com.guess.hk.smartbook.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.guess.hk.smartbook.repo.KeysRepo
import com.guess.hk.smartbook.repo.Resource

class RecognizeKeyUseCase(val repo: KeysRepo) {

    private val detector = FirebaseVision.getInstance()
        .onDeviceTextRecognizer

    fun recognizeText(visionImage: FirebaseVisionImage): LiveData<Resource<List<String>>> {
        val liveData = MutableLiveData<Resource<List<String>>>()

        if (repo.getBookKeys().isEmpty()){ //TODO refactor
            return liveData
        }
        detector.processImage(visionImage).addOnSuccessListener {
            if (it.text != null) {
                val textKey = it.text.replace("\n", " ")
                val book = findBookById(textKey)
                liveData.postValue(Resource.Success(book))
            } else {
                liveData.postValue(Resource.Error("Something went wrong"))
            }

        }.addOnFailureListener {
            liveData.postValue(Resource.Error("Something went wrong"))
        }
        return liveData
    }

    private fun findBookById(key: String): List<String> {
        for (book in repo.getBookKeys()) { //TODO refactor
            if (key.contains(book.id)) {
                return arrayListOf(book.url1, book.url2, book.url3)
            }
        }
        return listOf()
    }

}