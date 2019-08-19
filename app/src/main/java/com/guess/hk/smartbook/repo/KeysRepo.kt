package com.guess.hk.smartbook.repo
import androidx.lifecycle.LiveData
import com.guess.hk.smartbook.model.BookKey

interface KeysRepo {

    fun getVersion(): LiveData<Resource<String>>

    fun initBookKeys(isDataChanged : Boolean) : LiveData<Resource<String>>

    fun getBookKeys() : List<BookKey>
}