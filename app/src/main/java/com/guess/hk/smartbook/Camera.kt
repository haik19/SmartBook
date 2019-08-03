package com.guess.hk.smartbook

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.core.app.ActivityCompat
import java.util.*
import android.hardware.camera2.CameraCharacteristics


class Camera(private val manager: CameraManager, cameraFacing: Int, private val textureView: TextureView) {
    private lateinit var previewSize: Size
    private lateinit var cameraId: String
    private var backgroundHandler: Handler? = null
    private var cameraDevice: CameraDevice? = null
    private var backgroundThread: HandlerThread? = null
    private var cameraSession: CameraCaptureSession? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private var rect: Rect? = null

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
            cameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
            cameraDevice = null
        }
    }

    init {
        for (cameraId in manager.cameraIdList) {
            val characteristics = manager.getCameraCharacteristics(cameraId)
            if (characteristics.get(CameraCharacteristics.LENS_FACING) == cameraFacing) {
                val streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                if (streamConfigurationMap != null) {
                    previewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture::class.java)[0]
                }
                this.cameraId = cameraId
                rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
            }
        }
    }

    private fun openBackgroundThread() {
        val backgroundThread = HandlerThread("camera_background_thread")
        backgroundThread.start()
        backgroundHandler = Handler(backgroundThread.looper)
    }

    fun openCamera(context: Context) {
        openBackgroundThread()
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            manager.openCamera(cameraId, stateCallback, backgroundHandler)
        }
    }

    fun closeCamera() {
        cameraDevice?.close()
        cameraDevice = null
        releaseThread()
    }

    private fun releaseThread() {
        backgroundThread?.quitSafely()
        backgroundHandler = null
        backgroundThread = null
    }

    fun createPreviewSession() {
        val surfaceView = textureView.surfaceTexture
        surfaceView.setDefaultBufferSize(previewSize.width, previewSize.height)
        val previewSurface = Surface(surfaceView)
        captureRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequestBuilder?.addTarget(previewSurface)
        cameraDevice?.createCaptureSession(
            Collections.singletonList(previewSurface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    cameraSession = session
                    val captureRequest = captureRequestBuilder?.build()
                    session.setRepeatingRequest(captureRequest, null, backgroundHandler)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                }
            },
            backgroundHandler
        )

    }

     fun lock() {
        cameraSession?.capture(captureRequestBuilder?.build(),
            null, backgroundHandler)
    }

    fun unLock() {
        cameraSession?.setRepeatingRequest(captureRequestBuilder?.build(), null, backgroundHandler)
    }

    fun zoom(context: Context, zoomLevel : Int){
        val zoom = Rect(rect!!.left+zoomLevel, rect!!.top+zoomLevel,
            rect!!.right - zoomLevel, rect!!.bottom-zoomLevel
        )
        captureRequestBuilder?.set(CaptureRequest.SCALER_CROP_REGION, zoom)
        cameraSession?.setRepeatingRequest(captureRequestBuilder?.build(), null, backgroundHandler)
    }
}
