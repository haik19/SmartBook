package com.guess.hk.smartbook.usecase

import androidx.lifecycle.LiveData
import com.guess.hk.smartbook.repo.KeysRepo
import com.guess.hk.smartbook.repo.Resource

class DataAvailableUseCase(private val repo: KeysRepo, private val isDataChanged: Boolean) {
    fun fetChData() : LiveData<Resource<String>> = repo.initBookKeys(isDataChanged)
}
