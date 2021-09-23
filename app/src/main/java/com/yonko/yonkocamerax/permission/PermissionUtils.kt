package com.yonko.yonkocamerax.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yonko.yonkocamerax.Constant
import com.yonko.yonkocamerax.R
import com.yonko.yonkocamerax.listener.OnPermissionInitListener

class PermissionUtils {

    companion object {
        private var INSTANCE: PermissionUtils? = null
        fun getInstance(): PermissionUtils {
            if (INSTANCE == null) {
                INSTANCE = PermissionUtils()
            }
            return INSTANCE!!
        }
    }

    private var mContext: Context? = null
    private var listener: OnPermissionInitListener? = null
    private val permissionsDefault = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )


    fun requestPermissions(context: Context, callback: OnPermissionInitListener) {
        listener = callback
        mContext = context
        if (mContext != null) {
            val permissionRequest = getPermissionNotGrand(mContext!!, permissionsDefault)
            if (permissionRequest.isNotEmpty()) {
                requestPermission(mContext!!, permissionRequest)
            } else {
                listener?.onInitSuccess()
            }
        } else {
            listener?.onInitFailed(context.getString(R.string.error_context_is_null))
        }
    }

    fun requestPermissionWithAdditional(
        context: Context,
        additionalPermission: ArrayList<String>,
        callback: OnPermissionInitListener
    ) {
        for (single in additionalPermission) {
            permissionsDefault[permissionsDefault.lastIndex + 1] = single
        }
        requestPermissions(context, callback)
    }

    private fun getPermissionNotGrand(
        context: Context,
        permissions: Array<String>
    ): ArrayList<String> {
        val permissionNonRequest = arrayListOf<String>()
        for (permission in permissions) {
            val result = ContextCompat.checkSelfPermission(context, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionNonRequest.add(permission)
            }
        }
        return permissionNonRequest
    }

    private fun requestPermission(context: Context, permissionRequest: ArrayList<String>) {
        showLog(permissionRequest)
        if (isRotatePermisison(permissionRequest)) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + context.packageName)
            context.startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                permissionRequest.toTypedArray(),
                Constant.CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun isRotatePermisison(permissionRequest: ArrayList<String>): Boolean {
        for (permission in permissionRequest) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    mContext as Activity,
                    permission
                )
            ) {
                return true
            }
        }
        return false
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>
    ) {
        if (requestCode == Constant.CAMERA_PERMISSION_CODE) {
            val permissionNotGranted = mContext?.let { getPermissionNotGrand(it, permissions) }
            if (permissionNotGranted != null && permissionNotGranted.isNotEmpty()) {
                mContext?.let { requestPermission(it, permissionNotGranted) }
            } else {
                listener?.onInitSuccess()
            }
        }
    }

    private fun showLog(permissions: ArrayList<String>) {
        for (single in permissions) {
            Log.d("HAICK", "Permission with : $single")
        }
    }
}