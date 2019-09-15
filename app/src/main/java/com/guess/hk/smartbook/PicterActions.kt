package com.guess.hk.smartbook

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.util.DisplayMetrics
import android.view.WindowManager

fun openLink(context: Context, url: String) {
	val intent = Intent(
		Intent.ACTION_VIEW,
		Uri.parse(url)
	)
	if (intent.resolveActivity(context.packageManager) != null) {
		context.startActivity(intent)
	}
}

fun getScreenWidthInPx(context: Context): Int {
	val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
	val metrics = DisplayMetrics()
	display.getMetrics(metrics)
	return metrics.widthPixels
}

fun getScreenHeightInPx(context: Context): Int {
	val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
	val metrics = DisplayMetrics()
	display.getMetrics(metrics)
	return metrics.heightPixels
}

fun convertDpToPixel(dp: Float): Int {
	val metrics = Resources.getSystem().displayMetrics
	return (dp * metrics.density).toInt()
}

fun String.calculateLevDistance(recKey: String): Int {
	val lfd = levenshteinDistance(this, recKey)
	return (lfd * 100) / recKey.length
}


fun levenshteinDistance(str1: String, str2: String): Int {
	val Di_1 = IntArray(str2.length + 1)
	val Di = IntArray(str2.length + 1)

	for (j in 0..str2.length) {
		Di[j] = j // (i == 0)
	}

	for (i in 1..str1.length) {
		System.arraycopy(Di, 0, Di_1, 0, Di_1.size)

		Di[0] = i // (j == 0)
		for (j in 1..str2.length) {
			val cost = if (str1[i - 1] != str2[j - 1]) 1 else 0
			Di[j] = min(
				Di_1[j] + 1,
				Di[j - 1] + 1,
				Di_1[j - 1] + cost
			)
		}
	}

	return Di[Di.size - 1]
}

private fun min(n1: Int, n2: Int, n3: Int): Int {
	return Math.min(Math.min(n1, n2), n3)
}

fun String.levContains(key: String): Boolean { // levenshtat
	val keyArr = key.split(" ")
	val self = this.split(" ").toMutableList()

	if (self.size < keyArr.size) {
		return false
	}
	val notEquals = arrayListOf<String>()
	for (k in keyArr) {
		val index = self.indexOf(k)
		if (index == -1) {
			notEquals.add(k)
		} else {
			self.removeAt(index)
		}
	}
	if (notEquals.isNotEmpty()) {
		val deviatedWordsPercent = (notEquals.size * 100) / self.size
		if (deviatedWordsPercent <= 50) {
			val notEqualsIterator = notEquals.iterator()
			while (notEqualsIterator.hasNext()){
				val notEqual = notEqualsIterator.next()
				for (d in self){
					val percent = d.calculateLevDistance(notEqual)
					if (percent <= 50) {
						notEqualsIterator.remove()
						break
					}
				}
			}
		}
	}
	return notEquals.isEmpty()
}

