package com.maiziedu.clipsquare.demo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;

import com.maiziedu.clipsquare.views.ClipSquareImageView;

public class MainActivity extends Activity implements OnClickListener {
	/** Constants */
	public static final String AVATAR_CACHE_DIR = Environment.getExternalStorageDirectory() + File.separator + "AvatarTemp";
	public static final String AVATAR_TEMP_URL = AVATAR_CACHE_DIR + File.separator + "avatar_temp.jpg";
	public static final String AVATAR_URL = AVATAR_CACHE_DIR + File.separator + "avatar.jpg";

	private static final int RESULT_TAKE_CAMEAR = 10;
	private static final int RESULT_TAKE_ALBUM = 11;

	/** Views */
	private ClipSquareImageView clipImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		File dir = new File(AVATAR_CACHE_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.t2);
		clipImageView = (ClipSquareImageView) findViewById(R.id.clipSquareIV);
		clipImageView.setImageBitmap(bmp);

		findViewById(R.id.doneBtn).setOnClickListener(this);
		findViewById(R.id.photoBtn).setOnClickListener(this);
		findViewById(R.id.selectBtn).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.photoBtn:// 拍照
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(AVATAR_TEMP_URL)));
			startActivityForResult(cameraIntent, RESULT_TAKE_CAMEAR);
			break;
		case R.id.selectBtn:// 从相册选择
			Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
			albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(albumIntent, RESULT_TAKE_ALBUM);
			break;
		case R.id.doneBtn:// 完成
			// 此处获取剪裁后的bitmap
			Bitmap bitmap = clipImageView.clip();

			// 由于Intent传递bitmap不能超过40k,此处使用二进制数组传递
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] bitmapByte = baos.toByteArray();

			Intent intent = new Intent(MainActivity.this, ResultActivity.class);
			intent.putExtra("bitmap", bitmapByte);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_TAKE_CAMEAR && resultCode == RESULT_OK) {
			Bitmap bmp = BitmapFactory.decodeFile(AVATAR_TEMP_URL);
			Matrix matrix = new Matrix();
			matrix.postScale(0.5f, 0.5f);
			bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
			clipImageView.setIniting(false);
			clipImageView.setImageBitmap(bmp);
		} else if (requestCode == RESULT_TAKE_ALBUM && resultCode == RESULT_OK) {
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				return;
			}
			Uri uri = data.getData();
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
				clipImageView.setIniting(false);
				clipImageView.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
