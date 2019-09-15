package com.guess.hk.smartbook.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.guess.hk.smartbook.Camera
import com.guess.hk.smartbook.SmartBookApplication
import com.guess.hk.smartbook.repo.Resource
import com.guess.hk.smartbook.view.liseners.TextureListener
import com.guess.hk.smartbook.viewmodel.BookKeysViewModel
import kotlinx.android.synthetic.main.activity_main.*
import com.guess.hk.smartbook.R
import kotlinx.android.synthetic.main.permisson_view_container.*


class MainActivity : FragmentActivity() {

    companion object {
        const val DATA_VERSION_KEY = "app.version.key"
        const val CAMERA_REQUEST_CODE = 101
    }
    private lateinit var camera: Camera
    private var menuFragment: MenuFragment? = null
    private lateinit var bookKeysViewModel: BookKeysViewModel
    private val textureListener = object : TextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            super.onSurfaceTextureAvailable(surface, width, height)
           initCamera()
        }
    }
    private var dataVersionCode: Long = 0
    private var isDataAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)
        dataVersionCode = preferenceManager.getLong(DATA_VERSION_KEY, 0)

        texture_view.isOpaque = false
        initListeners()

        bookKeysViewModel = ViewModelProviders.of(this).get(BookKeysViewModel::class.java)
        bookKeysViewModel.initDb((applicationContext as SmartBookApplication).db.booksKeyDao())

        bookKeysViewModel.getVersion()

        bookKeysViewModel.recognizedKeyLiveData.observe(this, Observer {
            Log.d("MainActivityTest", "recognize")
            when (it) {
                is Resource.Success -> {
                    if (supportFragmentManager.findFragmentByTag("dialogTag") != null) {
                        camera.inProgress = false
                        return@Observer
                    }
                    val d = MenuFragment()
                    d.camera = camera
                    d.bookKey = it.data

                    d.show(supportFragmentManager,"dialogTag")
                    camera.inProgress = false
                    camera.stopRepeating()
                    if(menuFragment?.isVisible == false){
                        camera.unLock()
                    }
                }
                is Resource.Error -> {
                    camera.inProgress = false
                    if(menuFragment?.isVisible == false){
                        camera.unLock()
                    }
                }
		    }
	    })

        bookKeysViewModel.versionLiveData.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    val isDataChanged = it.data!! > dataVersionCode
                    if (isDataChanged) {
                        preferenceManager.edit().putLong(DATA_VERSION_KEY, it.data).apply()
                    }
                    Log.d("MainActivityTest", "version Success $isDataChanged")
                    bookKeysViewModel.checkData(isDataChanged)
                }
                is Resource.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    Log.d("MainActivityTest", "version error")
                }
            }
        })

        bookKeysViewModel.dataAvailableLiveData.observe(this, Observer {
            isDataAvailable = false
            when (it) {
                is Resource.Loading -> {
                    Log.d("MainActivityTest", "check data loading")
                }
                is Resource.Success -> {
                    isDataAvailable = true
                    Log.d("MainActivityTest", "check data succses")
                }
                is Resource.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    Log.d("MainActivityTest", "error")
                }
            }
        })
        allow_access_btn.setOnClickListener {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (texture_view.isAvailable) {
                if (::camera.isInitialized) {
                    camera.openCamera()
                } else {
                    initCamera()
                }
            } else {
                texture_view.surfaceTextureListener = textureListener
            }
            permission_view.visibility = View.GONE
        } else {
            permission_view.visibility = View.VISIBLE
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
        if (!isDataAvailable && menuFragment?.isVisible == true) {
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

    private fun initListeners() {
        texture_view.setOnClickListener {
            recognizePicture()
        }

        flash.setOnClickListener {
            camera.turnOnFlash()
        }

        zoom_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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

    private fun initCamera(){
        camera = Camera(texture_view).apply {
            openCamera()
        }
        camera.autoFocusCallback = object : Camera.AutoFocusCallback {
            override fun focusDetected() {
                recognizePicture()
            }
        }
    }
}
