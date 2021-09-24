package com.yonko.yonkocamerax

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.util.*

class FileStorageController(private val context:Context) {
    companion object{
        fun newInstance(context: Context) = FileStorageController(context)
    }

    private var path : String = ""
    private var folder :String = ""
    private var checkPrivate:Boolean = false

    fun setFolder(folder:String){
        this.folder = folder
    }

    fun setFilePath(filePath:String){
        this.path = filePath
    }

    fun setCheckPrivateStorage(checkPrivate : Boolean){
        this.checkPrivate = checkPrivate
    }

    private fun getPathFileName():String{
        if (path.isEmpty()){
            path = System.currentTimeMillis().toString()
        }
        return path
    }

    private fun createDirectoryPublic():File{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),folder)
            if (!file.exists()){
                file.mkdirs()
            }
            return File(file,getPathFileName() + ".jpg")
        }else{
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),folder)
            if (!file.exists()){
                file.mkdirs()
            }
            return File(file,getPathFileName() + ".jpg")
        }

    }

    private fun createDirectoryPrivate(): File {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + File.separator + "/" + folder)
//        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString())

        file.mkdirs()
        return File(file,getPathFileName() + ".jpg")
    }

    fun getOutput():File{
        return if (!checkPrivate){
            createDirectoryPrivate()
        }else{
            createDirectoryPublic()
        }

    }

    private fun getOutputDirectory(): File {
        var mediaDir = context.externalMediaDirs.firstOrNull().let(fun(mFile: File?): File {
            return if (folder.isNotEmpty()) {
                File(mFile, folder).apply {
                    mkdirs()
                }
            } else {
                File(mFile, "PictureHandle").apply {
                    mkdirs()
                }
            }
        })

        if (mediaDir != null && mediaDir.exists()) {
            mediaDir
        } else {
            mediaDir = context.filesDir
        }

        return File(
            mediaDir,
            getPathFileName() + ".jpg"
        )
    }
}