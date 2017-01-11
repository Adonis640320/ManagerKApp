package com.sincere.kboss;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
public class PhotoSelectMainActivity extends Activity {

    public static final String TAG = PhotoSelectMainActivity.class.getSimpleName();

    private Camera mCamera;
    private boolean bRecording = false;

    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private ImageView btnBack, btnCameraTake, btnCameraGallery;
    private Context myContext;
    private LinearLayout layout_preview;
    private boolean bCameraFront = false;

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

    private String photo_path = "";
    private Uri photo_uri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkCameraHardware();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select_photo);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;

        initialize();
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                bCameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                bCameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "미안하지만 카메라기능을 사용할수 없습니다.", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(this, "프론트 카메라가 없습니다.", Toast.LENGTH_LONG).show();
                //btn_switchCamera.setVisibility(View.GONE);
            }
            releaseCamera();
            chooseCamera();
        }

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

    public void initialize() {
        layout_preview = (LinearLayout) findViewById(R.id.cert_preview);
        mPreview = new CameraPreview(myContext, mCamera);
        layout_preview.addView(mPreview);

        btnBack = (ImageView) findViewById(R.id.btnCameraBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnCameraTake = (ImageView) findViewById(R.id.btnCameraTake);
        btnCameraTake.setOnClickListener(onClickTakePhoto);

        btnCameraGallery = (ImageView)findViewById(R.id.btnCameraGallery);
        btnCameraGallery.setOnClickListener(onClickGallery);
    }

    public View.OnClickListener onClickTakePhoto = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if ( Build.VERSION.SDK_INT>= 23 ) {
                if (ContextCompat.checkSelfPermission(PhotoSelectMainActivity.this, "android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PhotoSelectMainActivity.this, new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"}, 0);
                    return;
                }
            }

            mCamera.takePicture(null, null, mPicture);

        }
    };

    public View.OnClickListener onClickGallery = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if ( Build.VERSION.SDK_INT>= 23 ) {
                if (ContextCompat.checkSelfPermission(PhotoSelectMainActivity.this, "android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PhotoSelectMainActivity.this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
                    return;
                }
            }
            DisplayGallery();
        }
    };
    void DisplayGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, SELECT_IMAGE_ACTIVITY_REQ);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_ACTIVITY_REQ)
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

    public void chooseCamera() {
        //if the camera preview is the front
        if (bCameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private boolean hasCamera(Context context) {
        //check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }
    private static File getOutputMediaFile() {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File("/sdcard/", "managerk");

        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
//        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "Image_Upload.jpg");

        return mediaFile;
    }
    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                Uri photoUri = null;
                File pictureFile = getOutputMediaFile();
                fileUri = Uri.fromFile(pictureFile);
                photoUri = fileUri;

                if (pictureFile == null) {
                    return;
                }
                try {
                    //write the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
//                    Toast toast = Toast.makeText(myContext, "Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG);
//                    toast.show();

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }

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
//                            Toast.makeText(this, "Error occurred while loading photograph", Toast.LENGTH_SHORT).show();
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
                catch (Exception ex)
                {
                    ex.printStackTrace();
//                    Toast.makeText(this, "Exception occurred while taking photograph", Toast.LENGTH_SHORT).show();
                }

                //refresh camera to continue preview
//                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }
    private String getOutputPhotoFile()
    {

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getPackageName());
        if (!directory.exists())
        {
            if (!directory.mkdirs())
                return null;
        }

        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        return directory.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
//        return new File(directory.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
