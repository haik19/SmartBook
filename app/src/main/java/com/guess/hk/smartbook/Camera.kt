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
import android.hardware.camera2.CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE
import android.hardware.camera2.CaptureRequest.CONTROL_AF_MODE
import android.hardware.camera2.CaptureResult


class Camera( private val textureView: TextureView) {
    private lateinit var previewSize: Size
    private lateinit var cameraId: String
    private val cameraManager: CameraManager =  textureView.context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    
    private var backgroundHandler: Handler? = null
    private var cameraDevice: CameraDevice? = null
    private var backgroundThread: HandlerThread? = null
    private var cameraSession: CameraCaptureSession? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private var rect: Rect? = null
    private var areWeFocused : Boolean = false

    var autoFocusCallback : AutoFocusCallback? = null

    private val mCaptureCallback = object : CameraCaptureSession.CaptureCallback() {
        fun process(result: CaptureResult){
            val afState = result.get(CaptureResult.CONTROL_AF_STATE)
            if (CaptureResult.CONTROL_AF_TRIGGER_START == afState) {
                if (areWeFocused) {
                    autoFocusCallback?.focusDetected()
                }
            }
            areWeFocused = CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED == afState
        }

        override fun onCaptureProgressed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            partialResult: CaptureResult
        ) {
            process(partialResult)
        }

        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            process(result)
        }
    }

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
        for (cameraId in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
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
            cameraManager.openCamera(cameraId, stateCallback, backgroundHandler)
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
                    captureRequestBuilder?.set(CONTROL_AF_MODE, CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                    captureRequestBuilder?.build().apply {
                        session.setRepeatingRequest(this, mCaptureCallback, backgroundHandler)
                    }
                }
                override fun onConfigureFailed(session: CameraCaptureSession) {
                }
            },
            backgroundHandler
        )

    }

    fun lock() {
        captureRequestBuilder?.let {
            cameraSession?.capture(it.build(), mCaptureCallback, backgroundHandler)
        }
    }

    fun unLock() {
        captureRequestBuilder?.let {
            cameraSession?.setRepeatingRequest(it.build(), mCaptureCallback, backgroundHandler)
        }
    }

    fun zoom(zoomLevel: Int) {
        captureRequestBuilder?.let {
            val zoom = Rect(
                rect!!.left + zoomLevel, rect!!.top + zoomLevel,
                rect!!.right - zoomLevel, rect!!.bottom - zoomLevel
            )
            captureRequestBuilder?.set(CaptureRequest.SCALER_CROP_REGION, zoom)
            cameraSession?.setRepeatingRequest(it.build(), mCaptureCallback, backgroundHandler)
        }
    }

    fun turnOnFlash() {
        captureRequestBuilder?.let {
            if (captureRequestBuilder?.get(CaptureRequest.FLASH_MODE) == CaptureRequest.FLASH_MODE_TORCH) {
                captureRequestBuilder?.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF)
            } else {
                captureRequestBuilder?.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH)
            }
            cameraSession?.setRepeatingRequest(it.build(), mCaptureCallback, backgroundHandler)
        }
    }

    interface AutoFocusCallback {
        fun focusDetected()
    }
}
