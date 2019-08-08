package com.guess.hk.smartbook

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.DisplayMetrics
import android.view.WindowManager
import java.io.File
import java.io.FileOutputStream

private fun saveImage(finalBitmap: Bitmap, image_name: String) {
    val pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val photoFile = File(pictures, "my_photo" + System.currentTimeMillis() + ".jpg")
    if (!photoFile.exists()) {
        photoFile.createNewFile()
    }
    val fos = FileOutputStream(photoFile)
    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    fos.close()
}

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

fun getScreenHeigthInPx(context: Context): Int {
    val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val metrics = DisplayMetrics()
    display.getMetrics(metrics)
    return metrics.heightPixels
}

fun convertDpToPixel(dp: Float): Int {
    val metrics = Resources.getSystem().displayMetrics
    return (dp * metrics.density).toInt()
}
