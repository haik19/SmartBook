package com.guess.hk.smartbook.repo

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.guess.hk.smartbook.db.BookKeyDao
import com.guess.hk.smartbook.model.BookKey
import com.guess.hk.smartbook.model.Link

class KeysRepoImpl : KeysRepo {

    companion object {
        const val VERSION_NAME = "versionName"
    }

    private val versionField = FirebaseDatabase.getInstance().getReference(VERSION_NAME)
    private val keysField = FirebaseDatabase.getInstance().reference
    private val booksData = arrayListOf<BookKey>()
    lateinit var bookKeyDao: BookKeyDao

    override fun getVersion(): LiveData<Resource<String>> {
        val versionLivaData = MutableLiveData<Resource<String>>()
        versionField.addListenerForSingleValueEvent(object : ValueEventListener {
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

    override fun initBookKeys(isDataChanged: Boolean): LiveData<Resource<String>> {
        val dataAvailableLiveData = MutableLiveData<Resource<String>>()
        dataAvailableLiveData.value = Resource.Loading()

        if (isDataChanged) {
            getFromFairBaseAndStore(Runnable {
                booksData.addAll(bookKeyDao.getAllBookKeys()) //TODO ADD CHECK for loading
                dataAvailableLiveData.value = Resource.Success("")
                Log.d("MainActivityTest", "get data from firebasee size ${booksData.size}")
            })
            return dataAvailableLiveData
        }
        booksData.addAll(bookKeyDao.getAllBookKeys()) // data not changed
        dataAvailableLiveData.value = Resource.Success("")
        Log.d("MainActivityTest", "get data from db  ${booksData.size}")
        return dataAvailableLiveData
    }


    private fun getFromFairBaseAndStore(successRunnable: Runnable) {
        keysField.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                bookKeyDao.deleteTable() // delete all
                for (postSnapshot in dataSnapshot.children) {
                    if (VERSION_NAME == postSnapshot.key) {
                        continue
                    }
                    val book = postSnapshot.getValue(BookKey::class.java)
                    val mutableList = mutableListOf<Link>()
                    for (link in postSnapshot.children) {
                        if (link.key.equals("id")) {
                            continue
                        }
                        val linkItem = link.getValue(Link::class.java)
                        linkItem?.let {
                            mutableList.add(it)
                        }
                    }
                    book?.links = mutableList
                    book?.let {
                        bookKeyDao.insert(it)
                    }
                }
                keysField.removeEventListener(this)
                successRunnable.run()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                keysField.removeEventListener(this)
            }
        })
    }

    override fun getBookKeys() = booksData

}