package com.yonko.yonkocamerax.fragment

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.camera.core.Camera
import com.yonko.yonkocamerax.CameraController
import com.yonko.yonkocamerax.R
import com.yonko.yonkocamerax.listener.OnCameraInitListener
import kotlinx.android.synthetic.main.fragment_camera_view.view.*

class CameraXFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        initView()
    }

    lateinit var cameraController: CameraController
    fun initView() {
        inflate(context, R.layout.fragment_camera_view, this)
        handelOnClickView()
        setupCamera()
        checkFlashSupport()
    }

    private fun setupCamera() {
        this.context?.let { cameraController = CameraController.build(it, dlgCameraViewFinder) }
        cameraController.setInitListener(object : OnCameraInitListener {
            override fun onInitComplete(camera: Camera) {
                checkFlashSupport()
            }
        })
        cameraController.attachCamera()
    }

    private fun checkFlashSupport() {
        if (cameraController.isSupportFlash()) {
            dlgCameraViewICFlash.visibility = VISIBLE
        } else {
            dlgCameraViewICFlash.visibility = GONE
        }
    }

    private fun handelOnClickView() {
        dlgCameraViewChangeCamera.setOnClickListener {
            cameraController.switchCamera()
        }
        dlgCameraViewICFlash.setOnClickListener {
            cameraController.switchFlash()
        }
        dlgCameraViewSeekbarZoom.setOnProgressChangedListener {
            cameraController.setLinearZoom(it.toFloat())
        }
        dlgCameraViewFinder.setOnClickListener {
            dlgCameraViewSeekbarZoom.visibility = VISIBLE
        }
        dlgCameraViewTakeImage.setOnClickListener {
            cameraController.captureCamera()
        }
    }

    fun enableChangeCamera() {
        setHideChangeImage(true)
    }

    fun disableChangeCamera() {
        setHideChangeImage(false)
    }

    private fun setHideChangeImage(enable: Boolean) {
        if (enable) {
            dlgCameraViewChangeCamera.visibility = View.GONE
        } else {
            dlgCameraViewChangeCamera.visibility = View.VISIBLE
        }
    }

    fun enableZoomCamera() {
        setHideZoom(true)
    }

    fun disableZoomCamera() {
        setHideZoom(false)
    }

    private fun setHideZoom(enable: Boolean) {
        if (enable) {
            dlgCameraViewSeekbarZoom.visibility = View.GONE
        } else {
            dlgCameraViewSeekbarZoom.visibility = View.VISIBLE
        }
    }

    fun getCamera(): CameraController {
        return cameraController
    }
}