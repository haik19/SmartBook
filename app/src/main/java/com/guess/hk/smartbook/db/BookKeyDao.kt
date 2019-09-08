package com.guess.hk.smartbook.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.guess.hk.smartbook.model.BookKey

@Dao
interface BookKeyDao {

    @Query("SELECT * FROM BookKey")
    fun getAllBookKeys() : List<BookKey>

    @Insert
    fun insert(bookKey: BookKey)

    @Query("DELETE FROM BookKey")
    fun deleteTableData()

}