package com.yonko.yonkocamerax.listener

interface OnPermissionInitListener {
    fun onInitFailed(error: String)
    fun onInitSuccess()
}