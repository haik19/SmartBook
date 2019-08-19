package com.guess.hk.smartbook.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity
data class BookKey(
    @PrimaryKey
    val id: String,
    val url1: String,
    val url2: String,
    val url3: String
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "url1" to url1,
            "url2" to url2,
            "url3" to url3
        )
    }

    constructor() : this("-1", "", "", "")

}