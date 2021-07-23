package com.sunzk.colortest.activity

import android.Manifest
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
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import com.shizhefei.view.largeimage.factory.FileBitmapDecoderFactory
import com.sunzk.base.utils.Logger
import com.sunzk.colortest.BaseActivity
import com.sunzk.colortest.activity.SelectPicActivity
import com.sunzk.colortest.databinding.ActivitySelectPicBinding
import com.tbruyelle.rxpermissions3.RxPermissions
import com.wildma.pictureselector.FileUtils
import com.wildma.pictureselector.PictureBean
import com.wildma.pictureselector.PictureSelector

class SelectPicActivity : BaseActivity() {
    private lateinit var viewBinding: ActivitySelectPicBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySelectPicBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.livMain.setOnTouchListener { v: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> showColor(v, event)
                MotionEvent.ACTION_UP -> viewBinding!!.livMain.performClick()
            }
            false
        }
        RxPermissions(this).request(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).subscribe { granted: Boolean ->
            if (granted) { // Always true pre-M
                selectPic()
            } else {
                Toast.makeText(this, "请到设置修改权限", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showColor(v: View, event: MotionEvent) {
        val bitmapFromView = getBitmapFromView(v)
        val pixel = bitmapFromView.getPixel(event.x.toInt(), event.y.toInt())
        try {
            val layoutParams =
                viewBinding!!.flFloat.layoutParams as MarginLayoutParams
            layoutParams.leftMargin = event.x.toInt()
            layoutParams.topMargin = event.y.toInt()
            viewBinding!!.flFloat.layoutParams = layoutParams
        } catch (throwable: Throwable) {
        }
        viewBinding!!.flFloat.visibility = View.VISIBLE
        viewBinding!!.tvColor.text = Color.red(pixel)
            .toString() + "," + Color.green(pixel) + "," + Color.blue(
            pixel
        )
    }

    private fun selectPic() {
        PictureSelector.create(
            this@SelectPicActivity,
            PictureSelector.SELECT_REQUEST_CODE
        ) //		.selectPicture(true);
            .selectPicture(false)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        /*结果回调*/if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
            if (data != null) {
                val pictureBean: PictureBean? =
                    data.getParcelableExtra(PictureSelector.PICTURE_RESULT)
                if (pictureBean != null) {
                    Logger.d(
                        TAG,
                        "SelectPicActivity#onActivityResult- ",
                        pictureBean.isCut,
                        pictureBean.path,
                        pictureBean.uri
                    )
                    if (pictureBean.isCut) {
                        viewBinding!!.livMain.setImage(FileBitmapDecoderFactory(pictureBean.path))
                    } else {
                        val mediaUri: Uri
                        //						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//							mediaUri = MediaStore.getMediaUri(this, pictureBean.getUri());
//						} else {
                        mediaUri = pictureBean.uri
                        //						}
                        viewBinding!!.livMain.setImage(getDrawableFromFileUri(mediaUri))
                    }
                }

                //使用 Glide 加载图片
                /*Glide.with(this)
                        .load(pictureBean.isCut() ? pictureBean.getPath() : pictureBean.getUri())
                        .apply(RequestOptions.centerCropTransform()).into(mIvImage);*/
            }
        }
    }

    private fun getDrawableFromFileUri(uri: Uri): Drawable? {
        val scheme = uri.scheme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && (ContentResolver.SCHEME_CONTENT == scheme || ContentResolver.SCHEME_FILE == scheme)) {
            try {
                val src =
                    ImageDecoder.createSource(contentResolver, uri)
                return ImageDecoder.decodeDrawable(
                    src
                ) { decoder: ImageDecoder, info: ImageInfo?, s: ImageDecoder.Source? ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                }
            } catch (e: Throwable) {
                Logger.w(
                    TAG,
                    "Unable to open content: $uri",
                    e
                )
            }
        } else {
            return Drawable.createFromPath(uri.toString())
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // 销毁裁剪后的无用图片
        FileUtils.deleteAllCacheImage(this)
    }

    companion object {
        private const val TAG = "SelectPicActivity"
        fun getBitmapFromView(v: View): Bitmap {
            val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.RGB_565)
            val c = Canvas(b)
            v.layout(v.left, v.top, v.right, v.bottom)
            // Draw background
            val bgDrawable = v.background
            if (bgDrawable != null) {
                bgDrawable.draw(c)
            } else {
                c.drawColor(Color.TRANSPARENT)
            }
            // Draw view to canvas
            v.draw(c)
            return b
        }
    }
}