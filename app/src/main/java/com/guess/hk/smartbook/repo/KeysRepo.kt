package com.guess.hk.smartbook.repo

import androidx.lifecycle.LiveData


interface KeysRepo {

    fun getVersion(): LiveData<Resource<String>>
}