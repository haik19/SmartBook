package com.guess.hk.smartbook.view

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Window
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.guess.hk.smartbook.Camera
import com.guess.hk.smartbook.R
import com.guess.hk.smartbook.repo.Resource
import com.guess.hk.smartbook.view.liseners.TextureListener
import com.guess.hk.smartbook.viewmodel.BookKeysViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : FragmentActivity() {

    companion object {
        const val DATA_VERSION_KEY = "app.version.key"
    }

    private lateinit var camera: Camera
    private lateinit var bottomSheetFragment : BookSheetDialog
    private lateinit var bookKeysViewModel : BookKeysViewModel
    private val textureListener = object : TextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            super.onSurfaceTextureAvailable(surface, width, height)
            camera = Camera(texture_view).apply {
                openCamera(texture_view.context)
            }
            camera.autoFocusCallback = object : Camera.AutoFocusCallback {
                override fun focusDetected() {
                    recognizePicture()
                }
            }
        }
    }
    private var dataVersion: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)
        dataVersion = preferenceManager.getString(DATA_VERSION_KEY, "")

        texture_view.isOpaque = false
        initListeners()

        bookKeysViewModel = ViewModelProviders.of(this).get(BookKeysViewModel::class.java)
        bookKeysViewModel.recognizedKeyLiveData.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    val book = it.data
                    bottomSheetFragment = BookSheetDialog()
                    if (book?.isNotEmpty() == true) {
                        bottomSheetFragment.urls = book as ArrayList<String>
                        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
                    } else {
                        Toast.makeText(this, "Not recognized!", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show()
                }
            }
            camera.unLock()
        })

        bookKeysViewModel.versionLiveData.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (dataVersion.equals(it.data)) {
                        println("read from db")
                    } else {
                        println("read from net")
                        preferenceManager.edit().putString(DATA_VERSION_KEY, it.data).apply()
                    }

                }
                is Resource.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        bookKeysViewModel.getVersion()

    }

    override fun onResume() {
        super.onResume()
        if (texture_view.isAvailable) {
            camera.openCamera(texture_view.context)
        } else {
            texture_view.surfaceTextureListener = textureListener
        }
    }

    override fun onStop() {
        super.onStop()
        if (::camera.isInitialized) {
            camera.closeCamera()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::camera.isInitialized) {
            camera.closeCamera()
        }
    }

    private fun recognizePicture() {
        if (::bottomSheetFragment.isInitialized && bottomSheetFragment.isVisible) {
            return
        }
        camera.lock()
        val finalBitmap = Bitmap.createBitmap(
            texture_view.bitmap,
            rectView.points[1].x.toInt(),
            rectView.points[1].y.toInt(),
            (rectView.points[2].x - rectView.points[1].x).toInt(),
            (rectView.points[3].y - rectView.points[2].y).toInt()  //crop bitmap
        )
        val image = FirebaseVisionImage.fromBitmap(finalBitmap)
        bookKeysViewModel.recognizeKey(image)
    }


    private fun initListeners(){
        texture_view.setOnClickListener {
            recognizePicture()
        }

        take_photo.setOnClickListener{
            recognizePicture()
        }

        flash.setOnClickListener{
            camera.turnOnFlash()
        }

        zoom_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                camera.zoom(progress * 20)
            }
        })

        rectView.setZoomChangeListener {
            zoom_seek_bar.progress = (it * 10).toInt()
        }
    }

}
