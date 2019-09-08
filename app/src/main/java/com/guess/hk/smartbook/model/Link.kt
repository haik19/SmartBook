package com.guess.hk.smartbook.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Link(val title: String, val type: String, val url: String) {
	constructor() : this("", "", "")
}