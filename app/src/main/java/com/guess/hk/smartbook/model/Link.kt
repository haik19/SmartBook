package com.guess.hk.smartbook.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Link(val title: String, val type: String, val url: String) {

	@Exclude
	fun toMap(): Map<String, Any?> {
		return mapOf(
			"title" to type,
			"type" to url,
			"url" to title
		)
	}
	constructor() : this("", "", "")
}