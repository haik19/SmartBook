package com.guess.hk.smartbook

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class BooksDataManager {

    private val booksData = arrayListOf<Book>()

    init {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("books")

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val book = postSnapshot.getValue(Book::class.java)
                    book?.let {
                        booksData.add(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    fun findBookById(key: String): List<String> {
        for (book in booksData) {
            if (key.contains(book.id)) {
                return arrayListOf(book.url1, book.url2, book.url3)
            }
        }
        return listOf()
    }

    fun costOfSubstitution(a: Char, b: Char): Int {
        return if (a == b) 0 else 1
    }

    fun min(vararg numbers: Int): Int {
        return Arrays.stream(numbers)
            .min().orElse(Integer.MAX_VALUE)
    }


    fun calculates(x: String, y: String): Int {
        val dp = Array(x.length + 1) { IntArray(y.length + 1) }

        for (i in 0..x.length) {
            for (j in 0..y.length) {
                if (i == 0) {
                    dp[i][j] = j
                } else if (j == 0) {
                    dp[i][j] = i
                } else {
                    dp[i][j] = min(
                        dp[i - 1][j - 1] + costOfSubstitution(x[i - 1], y[j - 1]),
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1
                    )
                }
            }
        }

        return dp[x.length][y.length]
    }

}