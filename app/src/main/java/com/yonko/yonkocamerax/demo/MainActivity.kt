package com.yonko.yonkocamerax.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yonko.yonkocamerax.R
import com.yonko.yonkocamerax.camera.CameraXDialog
import com.yonko.yonkocamerax.listener.OnPermissionInitListener
import com.yonko.yonkocamerax.permission.PermissionUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PermissionUtils.getInstance().requestPermissions(this, object : OnPermissionInitListener {
            override fun onInitFailed(error: String) {
            }

            override fun onInitSuccess() {
            }
        })

        dlgShow.setOnClickListener {
            showCamera()
        }
        dlgShowFragment.setOnClickListener {
            val intent = Intent(this, MainActivityAttach::class.java)
            startActivity(intent)
        }

    }

    fun showCamera() {
        val cameraFragment = CameraXDialog.newInstance(this)
        cameraFragment.show(supportFragmentManager, null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.getInstance().onRequestPermissionsResult(
            requestCode,
            permissions as Array<String>
        )
    }
}
