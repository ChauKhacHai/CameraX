package com.yonko.yonkocamerax

import android.content.Context
import android.net.Uri
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.yonko.yonkocamerax.listener.OnCameraInitListener
import com.yonko.yonkocamerax.model.ImageCaptureModel
import java.io.File
import java.util.concurrent.ExecutorService

class CameraController private constructor(
    private val context: Context,
    private val previewConfig: Preview,
    private val viewFinder: PreviewView,
) {
    private lateinit var cameraExcutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var camera: Camera
    private var hasInit = false
    private var listener: OnCameraInitListener? = null


    init {
        viewFinder.setOnTouchListener { _, p1 ->
            if (p1.action != MotionEvent.ACTION_UP) {
                true
            }
            val focusPoint = SurfaceOrientedMeteringPointFactory(
                viewFinder.height.toFloat(),
                viewFinder.width.toFloat()
            ).createPoint(p1.x, p1.y)
            setManualFocus(focusPoint)
            true
        }
    }

    fun setInitListener(listener: OnCameraInitListener) {
        this.listener = listener
    }

    fun attachCamera() {
        buildCamera(cameraSelector)
    }

    private fun buildCamera(cameraSelector: CameraSelector) {
        this.cameraSelector = cameraSelector
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    context as AppCompatActivity,
                    cameraSelector,
                    previewConfig,
                    imageCapture
                )
                hasInit = true
                listener?.onInitComplete(camera)
            } catch (exc: Exception) {
                hasInit = false
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        buildCamera(cameraSelector)

    }

    fun switchFlash() {
        if (hasInit) {
            if (camera.cameraInfo.hasFlashUnit()) {
                camera.cameraControl.enableTorch((camera.cameraInfo.torchState.value == TorchState.OFF))
            } else {
                Toast.makeText(context, "Flash not support", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getTorchState(): Int? {
        return camera.cameraInfo.torchState.value
    }

    fun isSupportFlash(): Boolean {
        if (hasInit) {
            return camera.cameraInfo.hasFlashUnit()
        }
        return false
    }

    private fun captureCamera(imageCaptureModel: ImageCaptureModel) {
//        val photoFile = File(
//            imageCaptureModel.fileDirector,
//            imageCaptureModel.fileName + ".jpg"
//        )
//        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        //trungtd11
        val fileStorageController = FileStorageController.newInstance(context)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(fileStorageController.getOutput()).build()


        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(context,exc.message, Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    //Todo
                    // 1. using canvas draw addtion info over image
                    // 2. callback to controller
                    val savedUri = Uri.fromFile(fileStorageController.getOutput())
                    Toast.makeText(context, "Photo Saved$savedUri",Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun captureCamera() {
        //Todo need to build path
        val imageCaptureModel = ImageCaptureModel()
//        imageCaptureModel.setFolderName("....")
//        imageCaptureModel.setFileName("Demo")
        captureCamera(imageCaptureModel)
    }

    fun setManualFocus(point: MeteringPoint) {
        if (hasInit) {
            val action = FocusMeteringAction.Builder(point).build()
            camera.cameraControl.startFocusAndMetering(action)
        }
    }

    fun setZoom(scaleFactor: Float) {
        if (hasInit) {
            val scale = camera.cameraInfo.zoomState.value?.zoomRatio!! * scaleFactor
            camera.cameraControl.setZoomRatio(scale)
        }
    }

    fun setLinearZoom(scaleFactor: Float) {
        if (hasInit) {
            val scale = scaleFactor / 100.toFloat()
            camera.cameraControl.setLinearZoom(scale)
        }
    }

    companion object {
        private fun createPreviewConfig(viewFinder: PreviewView): Preview {
            return Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }
        }

        fun build(context: Context, viewFinder: PreviewView): CameraController {
            return CameraController(
                context,
                createPreviewConfig(viewFinder),
                viewFinder
            )
        }
    }
}