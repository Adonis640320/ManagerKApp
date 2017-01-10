package com.sincere.kboss;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: KimHakMin
 * Date: 13-11-29
 * Time: 上午10:59
 * To change this template use File | Settings | File Templates.
 */
public class SelectPhotoActivity extends Activity
{
	private final int MAX_RESOLUTION = 800;
	//public static int IMAGE_WIDTH = 320, IMAGE_HEIGHT = 240;
	public static int IMAGE_WIDTH = 400, IMAGE_HEIGHT = 300;

	static int WIDTH_WEIGHTSUM = 480, HEIGHT_WEIGHTSUM = 800;
	static int WIDTH_WEIGHT = 450, HEIGHT_WEIGHT = 300;
	static int FONTSIZE = 20;

	LinearLayout back_layout = null;
	int nScrWidth = 0, nScrHeight = 0;

	static Uri fileUri = null;

	public static int CAPTURE_IMAGE_ACTIVITY_REQ = 0;
	public static int SELECT_IMAGE_ACTIVITY_REQ = 1;

	public static String szRetCode = "RET";
	public static String szRetPath = "PATH";
	public static String szRetUri = "URI";

	public static int nRetSuccess = 1;
	public static int nRetCancelled = 0;
	public static int nRetFail = -1;

	private String photo_path = "";
	private Uri photo_uri = null;
	private TextView takePicture;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Here, thisActivity is the current activity

		checkCameraHardware();
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		getWindow().setFlags(WindowManager.LayoutParams.ALPHA_CHANGED,
				WindowManager.LayoutParams.ALPHA_CHANGED);
		getWindow().setGravity(Gravity.CENTER);
		this.setContentView(R.layout.activity_select_photo);

		initControls();
	}

	@Override
	public void finish()
	{
		super.finish();    //To change body of overridden methods use File | Settings | File Templates.
	}

	public void initControls()
	{
		back_layout = (LinearLayout)findViewById(R.id.back_layout);
		takePicture=(TextView) findViewById(R.id.album_camera);
		takePicture.setOnClickListener(onClickTakePhoto);
		findViewById(R.id.album_library).setOnClickListener(onClickSelImage);
		findViewById(R.id.album_cancel).setOnClickListener(onClickCancel);
	}


	@Override
	protected void onResume()
	{
		super.onResume();    //To change body of overridden methods use File | Settings | File Templates.

		if (photo_path.equals("") && photo_uri == null)
			return;

		Intent retIntent = new Intent();
		retIntent.putExtra(szRetCode, nRetSuccess);

		if (!photo_path.equals("") && photo_path != null)
			retIntent.putExtra(szRetPath, photo_path);

		if (photo_uri != null)
			retIntent.putExtra(szRetUri, photo_uri);

		setResult(RESULT_OK, retIntent);

		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQ)
		{
			if (resultCode == RESULT_OK)
			{
				Uri photoUri = null;

				if (data == null)
					photoUri = fileUri;
				else
					photoUri = data.getData();

				try
				{
					BitmapFactory.Options onlyBoundsOptions;
					if (photoUri != null) onlyBoundsOptions = getBitmapFactoryFromUri(photoUri);
					else onlyBoundsOptions = getBitmapFactoryFromUri(fileUri);
					if ((onlyBoundsOptions.outWidth == -1)
							|| (onlyBoundsOptions.outHeight == -1))
					return;

					double ratio = getRatio(onlyBoundsOptions);

					if (photoUri != null)
					{
						String szPath = photoUri.getPath();
						if (szPath == null || szPath.equals(""))
						{
							Toast.makeText(this, "Error occurred while loading photograph", Toast.LENGTH_SHORT).show();
						}
						else
						{
							photo_path = szPath;
							photo_uri = null;
						}
					}
					else
					{
						photo_path = fileUri.getPath();
						photo_uri = null;
					}

					updateBasicSecWithPath(photo_path,ratio);

				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					Toast.makeText(this, "Exception occurred while taking photograph", Toast.LENGTH_SHORT).show();
				}
			}
		}
		else if (requestCode == SELECT_IMAGE_ACTIVITY_REQ)
		{
			if (resultCode == RESULT_OK)
			{
				Uri selImage = data.getData();
				if (selImage != null)
				{
					photo_path = "";
					photo_uri = selImage;
				}
			}
		}
	}

	private void updateBasicSecWithPath(String szPath, double ratio)
	{
		try {
			/* Update user photo info view */
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
			options.inDither = true;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap bitmap = BitmapFactory.decodeFile(szPath, options);

			if (bitmap != null)
			{
//				int nWidth = bitmap.getWidth(), nHeight = bitmap.getHeight();
//				int nScaledWidth = 0, nScaledHeight = 0;
//				if (nWidth > nHeight)
//				{
//					nScaledWidth = SelectPhotoActivity.IMAGE_WIDTH;
//					nScaledHeight = nScaledWidth * nHeight / nWidth;
//				}
//				else
//				{
//					nScaledHeight = SelectPhotoActivity.IMAGE_HEIGHT;
//					nScaledWidth = nScaledHeight * nWidth / nHeight;
//				}

				//bitmap = Bitmap.createScaledBitmap(bitmap, nScaledWidth, nScaledHeight, false);
				bitmap = checkOrientationAndRotate(bitmap ,szPath);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public BitmapFactory.Options getBitmapFactoryFromUri(Uri uri) {
		InputStream input = null;
		try {
			input = getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;// optional
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return onlyBoundsOptions;
	}

	public double getRatio(BitmapFactory.Options onlyBoundsOptions) {
		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
				: onlyBoundsOptions.outWidth;
		double ratio = (originalSize > MAX_RESOLUTION) ? (originalSize / MAX_RESOLUTION)
				: 1.0;
		return ratio;
	}

	public Bitmap checkOrientationAndRotate(Bitmap bitmap, String filename) {
		ExifInterface ei = null;
		int orientation = -1;
		try {
			ei = new ExifInterface(filename);
			orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
		} catch (IOException e) {
			e.printStackTrace();
		}

		switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				bitmap = rotateImage(bitmap, 90);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				bitmap = rotateImage(bitmap, 180);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				bitmap = rotateImage(bitmap, 270);
				break;
		}
		storeImage(bitmap,filename);
		return bitmap;
	}

	public static Bitmap rotateImage(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}

	private void storeImage(Bitmap bitmap, String filePath) {
		File copyFile = new File(filePath);
		BufferedOutputStream out = null;
		try {
			copyFile.createNewFile();
			out = new BufferedOutputStream(new FileOutputStream(copyFile));
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (copyFile != null && copyFile.exists()) {
				copyFile.delete();
			}
		}
	}

	private static int getPowerOfTwoForSampleRatio(double ratio) {
		int k = Integer.highestOneBit((int) Math.floor(ratio));
		if (k == 0)
			return 1;
		else
			return k;
	}

	private boolean checkCameraHardware() {

		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Log.e("Camera", "Number of available camera : " + Camera.getNumberOfCameras());
			return true;
		} else {
			Toast.makeText(this, "No camera found!", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == 0) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
					&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
				TakePhoto();
			}
		}
		else if ( requestCode==1 )
		{
			if ( grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED )
			{
				DisplayGallery();
			}
		}
	}
	public View.OnClickListener onClickTakePhoto = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if ( Build.VERSION.SDK_INT>= 23 ) {
				if (ContextCompat.checkSelfPermission(SelectPhotoActivity.this, "android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(SelectPhotoActivity.this, new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"}, 0);
					return;
				}
			}
			TakePhoto();
		}
	};
	void TakePhoto()
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file = getOutputPhotoFile();
		fileUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQ);
	}
	public View.OnClickListener onClickSelImage = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{

			if ( Build.VERSION.SDK_INT>= 23 ) {
				if (ContextCompat.checkSelfPermission(SelectPhotoActivity.this, "android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(SelectPhotoActivity.this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
					return;
				}
			}
			DisplayGallery();
		}
	};
	void DisplayGallery()
	{
//		Intent intent = new Intent();
//		intent.setType("image/*");
//		intent.setAction(Intent.ACTION_GET_CONTENT);
//		startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE_ACTIVITY_REQ);

		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
		// Utility.setSelectingPhotoState(true);
		startActivityForResult(intent, SELECT_IMAGE_ACTIVITY_REQ);
	}
	public View.OnClickListener onClickCancel = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			cancelWithData();
		}
	};

	private File getOutputPhotoFile()
	{
		File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getPackageName());
		if (!directory.exists())
		{
			if (!directory.mkdirs())
				return null;
		}

		String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
//		String timeStamp = "userPhoto";
		return new File(directory.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
	}

	private void cancelWithData()
	{
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		SelectPhotoActivity.this.finish();
	}
}
