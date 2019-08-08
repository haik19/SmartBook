package com.guess.hk.smartbook

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.TextureView
import android.view.Window
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : FragmentActivity() {

    private lateinit var cameraManage: CameraManager
    private var cameraFacing: Int = -1
    private lateinit var textureView: TextureView
    private lateinit var textureListener: TextureListener
    private lateinit var camera: Camera
    private lateinit var bottomSheetFragment : BookSheetDialog

    private val detector = FirebaseVision.getInstance()
        .onDeviceTextRecognizer

    private val booksDataManager = BooksDataManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        textureView = texture_view
        textureView.isOpaque = false
        cameraManage = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK

        rectView.setZoomChangeListener {
            zoom_seek_bar.progress = (it * 10).toInt()
        }

        textureListener = object : TextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                super.onSurfaceTextureAvailable(surface, width, height)
                camera = Camera(cameraManage, cameraFacing, textureView).apply {
                    openCamera(textureView.context)
                }

                camera.autoFocusCallback = object : Camera.AutoFocusCallback {
                    override fun focusDetected() {
                        if (textureView.isAvailable) {
                            recognizePicture()
                        }
                    }
                }
            }
        }
        textureView.setOnClickListener {
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
    }

    override fun onResume() {
        super.onResume()
        if (textureView.isAvailable) {
            camera = Camera(cameraManage, cameraFacing, textureView).apply {
                openCamera(textureView.context)
            }
        } else {
            textureView.surfaceTextureListener = textureListener
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
            textureView.bitmap,
            rectView.points[1].x.toInt(),
            rectView.points[1].y.toInt(),
            (rectView.points[2].x - rectView.points[1].x).toInt(),
            (rectView.points[3].y - rectView.points[2].y).toInt()  //crop bitmap
        )
        camera.unLock()
        val image = FirebaseVisionImage.fromBitmap(finalBitmap)
        detector.processImage(image).addOnSuccessListener { it1 ->
            val textKey = it1.text.replace("\n", " ")
            val book = booksDataManager.findBookById(textKey)
            bottomSheetFragment = BookSheetDialog()
            if (book.isNotEmpty()) {
                bottomSheetFragment.urls = book as ArrayList<String>
                bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
            } else {
                Toast.makeText(this, "Not recognized!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener() {
            Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show()
            camera.unLock()
        }
    }
}
