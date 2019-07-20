package com.guess.hk.smartbook

import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.TextureView
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : FragmentActivity() {

    private lateinit var cameraManage: CameraManager
    private var cameraFacing: Int = -1
    private lateinit var textureView: TextureView
    private lateinit var textureListener: TextureListener
    private lateinit var camera: Camera
    private val detector = FirebaseVision.getInstance()
        .onDeviceTextRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        val booksDataManager = BooksDataManager()
        textureView = texture_view
        cameraManage = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK
        textureListener = object : TextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                super.onSurfaceTextureAvailable(surface, width, height)
                camera = Camera(cameraManage, cameraFacing, textureView).apply {
                    openCamera(textureView.context)
                }
            }
        }
        textureView.setOnClickListener {
            camera.lock()
            val image = FirebaseVisionImage.fromBitmap(textureView.bitmap)
            detector.processImage(image).addOnSuccessListener { it1 ->
                camera.unLock()
                val textKey = it1.text.replace("\n", " ")
                val book = booksDataManager.findBookById(textKey)
                val bottomSheetFragment = BookSheetDialog()
                if (book is ArrayList) {
                    bottomSheetFragment.urls = book
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
        camera.closeCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.closeCamera()
    }
}
