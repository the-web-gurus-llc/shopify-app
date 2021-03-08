package com.john.shopifyApplication.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore

class ImageHelper {

    companion object {

        @JvmStatic
        fun getSquredBitmap(srcBmp: Bitmap) : Bitmap {

            if (srcBmp.width >= srcBmp.height){

                return Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.width /2 - srcBmp.height /2,
                    0,
                    srcBmp.height,
                    srcBmp.height
                )

            }else{

                return Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.height /2 - srcBmp.width /2,
                    srcBmp.width,
                    srcBmp.width
                )
            }

        }

        @JvmStatic
        fun getResizedBitmap(image : Bitmap,maxSize : Int) : Bitmap {
            var width = image.width
            var height = image.height

            val bitmapRatio = width.toFloat() / height.toFloat()
            if (bitmapRatio > 1) {
                width = maxSize
                height = (width.toFloat() / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height.toFloat() * bitmapRatio).toInt()
            }
            return Bitmap.createScaledBitmap(image, width, height, true)
        }


        fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {

            val matrix = Matrix()
            matrix.postRotate(degrees)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
            val matrix = Matrix()
            matrix.preScale(if (horizontal) -1.0f else 1.0f, if (vertical) -1.0f else 1.0f)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        @JvmStatic
        fun modificationGallery(bitmap : Bitmap, image_absolute_path_uri : Uri, context: Context) : Bitmap {

            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(image_absolute_path_uri, filePathColumn, null, null, null)
            cursor.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val imageAbsolutePath = cursor.getString(columnIndex)
            cursor.close()

            val ei = ExifInterface(imageAbsolutePath)

            when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 ->
                    return rotate(bitmap, 90.0f)

                ExifInterface.ORIENTATION_ROTATE_180 ->
                    return rotate(bitmap, 180.0f)

                ExifInterface.ORIENTATION_ROTATE_270->
                    return rotate(bitmap, 270.0f)

                ExifInterface.ORIENTATION_FLIP_HORIZONTAL->
                    return flip(bitmap, true, false)

                ExifInterface.ORIENTATION_FLIP_VERTICAL->
                    return flip(bitmap, false, true)

                else -> return bitmap
            }
        }
    }

}