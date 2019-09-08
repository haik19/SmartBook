package com.guess.hk.smartbook.repo

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.guess.hk.smartbook.db.BookKeyDao
import com.guess.hk.smartbook.model.BookKey

class KeysRepoImpl : KeysRepo {

	companion object {
		const val VERSION_NAME = "versionName"
	}

	private val versionField = FirebaseDatabase.getInstance().getReference(VERSION_NAME)
	private val keysField = FirebaseDatabase.getInstance().getReference("data")
	private val booksData = arrayListOf<BookKey>()
	lateinit var bookKeyDao: BookKeyDao

	override fun getVersion(): LiveData<Resource<Long>> {
		val versionLivaData = MutableLiveData<Resource<Long>>()
		versionField.addListenerForSingleValueEvent(object : ValueEventListener {
			override fun onDataChange(data: DataSnapshot) {
				val version = data.getValue(Long::class.java) ?: -1
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
				val bookKeyType = object : GenericTypeIndicator<List<@JvmSuppressWildcards BookKey>>() {}
				val bookData = dataSnapshot.getValue(bookKeyType)
				if (bookData?.isEmpty() == false) {
					bookKeyDao.deleteTableData()
					bookData.forEach { it1 -> bookKeyDao.insert(it1) }
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