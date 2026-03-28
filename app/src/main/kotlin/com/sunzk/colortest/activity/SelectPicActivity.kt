package com.sunzk.colortest.activity

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.ImageDecoder.ImageInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import com.shizhefei.view.largeimage.LargeImageView
import com.shizhefei.view.largeimage.factory.FileBitmapDecoderFactory
import com.sunzk.base.utils.Logger
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.compose.ui.SelectPicScreen
import com.wildma.pictureselector.FileUtils
import com.wildma.pictureselector.PictureBean
import com.wildma.pictureselector.PictureSelector

class SelectPicActivity : BaseActivity() {

    private var largeImageView: LargeImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SelectPicScreen(
                onLargeImageViewReady = { largeImageView = it },
            )
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
            if (data != null) {
                val pictureBean: PictureBean? =
                    data.getParcelableExtra(PictureSelector.PICTURE_RESULT)
                if (pictureBean != null) {
                    Logger.d(
                        TAG,
                        "SelectPicActivity#onActivityResult- ",
                        pictureBean.isCut,
                        pictureBean.path,
                        pictureBean.uri,
                    )
                    if (pictureBean.isCut) {
                        largeImageView?.setImage(FileBitmapDecoderFactory(pictureBean.path))
                    } else {
                        val mediaUri: Uri = pictureBean.uri
                        largeImageView?.setImage(getDrawableFromFileUri(mediaUri))
                    }
                }
            }
        }
    }

    private fun getDrawableFromFileUri(uri: Uri): Drawable? {
        val scheme = uri.scheme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
            (ContentResolver.SCHEME_CONTENT == scheme || ContentResolver.SCHEME_FILE == scheme)
        ) {
            try {
                val src =
                    ImageDecoder.createSource(contentResolver, uri)
                return ImageDecoder.decodeDrawable(
                    src,
                ) { decoder: ImageDecoder, info: ImageInfo?, s: ImageDecoder.Source? ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                }
            } catch (e: Throwable) {
                Logger.w(
                    TAG,
                    "Unable to open content: $uri",
                    e,
                )
            }
        } else {
            return Drawable.createFromPath(uri.toString())
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        FileUtils.deleteAllCacheImage(this)
    }

    companion object {
        private const val TAG = "SelectPicActivity"

        fun getBitmapFromView(v: View): Bitmap {
            val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.RGB_565)
            val c = Canvas(b)
            v.layout(v.left, v.top, v.right, v.bottom)
            val bgDrawable = v.background
            if (bgDrawable != null) {
                bgDrawable.draw(c)
            } else {
                c.drawColor(Color.TRANSPARENT)
            }
            v.draw(c)
            return b
        }
    }
}
