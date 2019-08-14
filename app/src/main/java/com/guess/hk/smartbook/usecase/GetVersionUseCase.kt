package com.guess.hk.smartbook.usecase

import com.guess.hk.smartbook.repo.KeysRepo

class GetVersionUseCase(val repo : KeysRepo) {
    fun getVersion() = repo.getVersion()
}
