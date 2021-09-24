package com.yonko.yonkocamerax.model

import org.json.JSONObject

class ImageCaptureModel(
     private var fileDirector : String = "",
     private var fileName : String = "",
     var additionalInfo : JSONObject? = null
){
     fun setFolderName(fileDirector: String){
          this.fileDirector = fileDirector
     }

     fun getFolderName():String{
          return fileDirector
     }

     fun setFileName(fileName: String){
          this.fileName = fileName
     }

     fun getFileName():String{
          if (fileName.isEmpty()){
               fileName = System.currentTimeMillis().toString()
          }
          return fileName
     }
}