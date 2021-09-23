package com.yonko.yonkocamerax.camera

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import com.yonko.yonkocamerax.CameraController
import com.yonko.yonkocamerax.R
import com.yonko.yonkocamerax.fragment.CameraXFrameLayout
import kotlinx.android.synthetic.main.dialog_camera_view.*


class CameraXDialog(private val ctx: Context) : DialogFragment() {

    companion object {
        fun newInstance(ctx: Context): CameraXDialog {
            return CameraXDialog(ctx)
        }
    }

    private val cameraXFragment by lazy {
        CameraXFrameLayout(ctx)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_camera_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachCameraFragment()
    }

    private fun attachCameraFragment() {
        val params = RelativeLayout.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        dlgMainContainer.addView(cameraXFragment, params)
    }

    fun getCamera(): CameraController {
        return cameraXFragment.getCamera()
    }
}

