package com.sunzk.colortest.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shizhefei.view.largeimage.LargeImageView;
import com.shizhefei.view.largeimage.factory.FileBitmapDecoderFactory;
import com.sunzk.colortest.BaseActivity;
import com.sunzk.colortest.R;
import com.sunzk.colortest.utils.ColorUtils;
import com.sunzk.colortest.utils.Logger;
import com.tbruyelle.rxpermissions3.RxPermissions;
import com.wildma.pictureselector.FileUtils;
import com.wildma.pictureselector.PictureBean;
import com.wildma.pictureselector.PictureSelector;

import androidx.annotation.Nullable;

public class SelectPicActivity extends BaseActivity {

	private static final String TAG = "SelectPicActivity";

	private LargeImageView imageView;
	private FrameLayout flFloat;
	private TextView tvColor;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_pic);
		imageView = findViewById(R.id.activity_select_pic_liv_main);
		flFloat = findViewById(R.id.activity_select_pic_fl_float);
		tvColor = findViewById(R.id.activity_select_pic_tv_color);
		imageView.setOnTouchListener((v, event) -> {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					showColor(v, event);
					break;
				case MotionEvent.ACTION_UP:
					imageView.performClick();
					break;
			}
			return false;
		});
		new RxPermissions(this).request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).subscribe(granted -> {
			if (granted) { // Always true pre-M
				selectPic();
			} else {
				Toast.makeText(this, "请到设置修改权限", Toast.LENGTH_SHORT).show();
				finish();
			}
		});

	}

	private void showColor(View v, MotionEvent event) {
		Bitmap bitmapFromView = getBitmapFromView(v);
		int pixel = bitmapFromView.getPixel((int) event.getX(), (int) event.getY());
		try {
			ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) flFloat.getLayoutParams();
			layoutParams.leftMargin = (int) event.getX();
			layoutParams.topMargin = (int) event.getY();
			flFloat.setLayoutParams(layoutParams);
		} catch (Throwable throwable) {

		}
		flFloat.setVisibility(View.VISIBLE);
		tvColor.setText(Color.red(pixel) + "," + Color.green(pixel) + "," + Color.blue(pixel));
	}

	private void selectPic() {
		PictureSelector.create(SelectPicActivity.this, PictureSelector.SELECT_REQUEST_CODE)
//		.selectPicture(true);
				.selectPicture(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/*结果回调*/
		if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
			if (data != null) {
				PictureBean pictureBean = data.getParcelableExtra(PictureSelector.PICTURE_RESULT);
				if (pictureBean != null) {
					Logger.d(TAG, "SelectPicActivity#onActivityResult- ", pictureBean.isCut(), pictureBean.getPath(), pictureBean.getUri());
					if (pictureBean.isCut()) {
						imageView.setImage(new FileBitmapDecoderFactory(pictureBean.getPath()));
					} else {
						Uri mediaUri;
//						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//							mediaUri = MediaStore.getMediaUri(this, pictureBean.getUri());
//						} else {
						mediaUri = pictureBean.getUri();
//						}
						imageView.setImage(getDrawableFromFileUri(mediaUri));
					}
				}

				//使用 Glide 加载图片
                /*Glide.with(this)
                        .load(pictureBean.isCut() ? pictureBean.getPath() : pictureBean.getUri())
                        .apply(RequestOptions.centerCropTransform()).into(mIvImage);*/
			}
		}
	}

	private Drawable getDrawableFromFileUri(Uri uri) {
		final String scheme = uri.getScheme();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && (ContentResolver.SCHEME_CONTENT.equals(scheme) || ContentResolver.SCHEME_FILE.equals(scheme))) {
			try {
				ImageDecoder.Source src = ImageDecoder.createSource(getContentResolver(), uri);
				return ImageDecoder.decodeDrawable(src, (decoder, info, s) -> {
					decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE);
				});
			} catch (Throwable e) {
				Logger.w(TAG, "Unable to open content: " + uri, e);
			}
		} else {
			return Drawable.createFromPath(uri.toString());
		}
		return null;
	}


	public static Bitmap getBitmapFromView(View v) {
		Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
		Canvas c = new Canvas(b);
		v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
		// Draw background
		Drawable bgDrawable = v.getBackground();
		if (bgDrawable != null) {
			bgDrawable.draw(c);
		} else {
			c.drawColor(Color.TRANSPARENT);
		}
		// Draw view to canvas
		v.draw(c);
		return b;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 销毁裁剪后的无用图片
		FileUtils.deleteAllCacheImage(this);
	}
}
