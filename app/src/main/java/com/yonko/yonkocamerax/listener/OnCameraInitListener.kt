package com.yonko.yonkocamerax.listener

import androidx.camera.core.Camera

interface OnCameraInitListener {
    fun onInitComplete(camera : Camera)
}