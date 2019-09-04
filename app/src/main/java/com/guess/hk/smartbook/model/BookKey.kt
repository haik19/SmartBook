package com.guess.hk.smartbook.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity
@TypeConverters(LinkConverter::class)
data class BookKey(
	@PrimaryKey
	val id: String,
	var links: List<Link>
) {
	@Exclude
	fun toMap(): Map<String, Any?> {
		return mapOf(
			"id" to id
		)
	}

	constructor() : this("", mutableListOf())
}