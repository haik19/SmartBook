package com.guess.hk.smartbook.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class KeysRepoImpl : KeysRepo {

    companion object {
        const val KEYS_FIELD_NAME = "keys" // top paret object
        const val VERSION_NAME = "versionName"
    }

    private val versionField = FirebaseDatabase.getInstance().getReference(VERSION_NAME)
    private val keysField = FirebaseDatabase.getInstance().getReference(KEYS_FIELD_NAME)


    override fun getVersion(): LiveData<Resource<String>> {
        val versionLivaData = MutableLiveData<Resource<String>>()
        versionField.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(data: DataSnapshot) {
                val version = data.getValue(String::class.java) ?: ""
                versionLivaData.postValue(Resource.Success(version))
            }

            override fun onCancelled(data: DatabaseError) {
                versionLivaData.postValue(Resource.Error(data.message))
            }
        })
        return versionLivaData
    }


}