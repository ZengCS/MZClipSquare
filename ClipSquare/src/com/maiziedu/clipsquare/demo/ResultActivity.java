package com.maiziedu.clipsquare.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;

public class ResultActivity extends Activity {
	private static final float AVATAR_SIZE = 220f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		byte[] bis = getIntent().getByteArrayExtra("bitmap");
		if (bis != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
			if (bitmap != null) {
				((ImageView) findViewById(R.id.clipResultIV)).setImageBitmap(bitmap);
			}
			saveFile(bitmap);
		}
	}

	private void saveFile(Bitmap bitmap) {
		bitmap = zoomImage(bitmap, AVATAR_SIZE, AVATAR_SIZE);
		File file = new File(MainActivity.AVATAR_URL);
		if (file.exists()) {
			file.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Bitmap zoomImage(Bitmap srcBm, double newWidth, double newHeight) {
		// 获取这个图片的宽和高
		float width = srcBm.getWidth();
		float height = srcBm.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(srcBm, 0, 0, (int) width, (int) height, matrix, true);
		return bitmap;
	}
}
