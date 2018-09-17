package practice.mvp.com.manchassignment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import practice.mvp.com.manchassignment.database.SQLConstants;
import practice.mvp.com.manchassignment.database.SQLManager;
import practice.mvp.com.manchassignment.types.PostItem;
import practice.mvp.com.manchassignment.utilities.Utils;

public class EditPostActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEdtTitle;
    private EditText mEdtDescription;
    private TextView mTitle;
    private TextView mTxtImagePath;
    private Button mBtnCreatePost;
    private String mTitleTxt, mDescriptionTxt, mImagePathTxt;
    private boolean isImageAttached;
    private LinearLayout mPostLayout;

    private final static int PICTURE_FROM_GALLERY = 1;
    private final static int TAKE_PICTURE = 2;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA=3;
    public static final int MY_PERMISSIONS_REQUEST_GALLERY=4;
    private static String[] PERMISSIONS_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


    private File file = null;
    private Bitmap mBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        initializeUI();

    }

    private void initializeUI()
    {
        mEdtTitle = findViewById(R.id.edt_title);
        mEdtDescription = findViewById(R.id.edt_desc);
        mTxtImagePath = findViewById(R.id.image_txt);
        mBtnCreatePost = findViewById(R.id.btn_create);
        mPostLayout=findViewById(R.id.edit_view);
        mTitle=findViewById(R.id.title);
        mTitle.setText(getString(R.string.edit_post_title));

        mBtnCreatePost.setOnClickListener(this);
        mTxtImagePath.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create:
                Utils.hideKeyboard(this, mPostLayout);
                mTitleTxt = mEdtTitle.getText().toString();
                mDescriptionTxt = mEdtDescription.getText().toString();

                if (mTitleTxt.isEmpty() || mTitleTxt.trim().length() == 0) {
                    Utils.displayAlert(v, getString(R.string.missing_title));
                } else if (mDescriptionTxt.isEmpty() || mDescriptionTxt.trim().length() == 0) {
                    Utils.displayAlert(v, getString(R.string.missing_desc));
                } else if (!isImageAttached) {
                    Utils.displayAlert(v, getString(R.string.missing_image));
                } else {
                    PostItem item = new PostItem(mTitleTxt, mDescriptionTxt, mImagePathTxt);
                    SQLManager.getInstance().savePost(item, SQLConstants.POST_TABLE);
                    Intent in= new Intent();
                    in.putExtra("postItem", item);
                    setResult(RESULT_OK, in);
                    finish();
                }
            break;

            case R.id.image_txt:

                imageSourceDialog();
                break;
        }

    }

    private void getPhotosFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICTURE_FROM_GALLERY);

    }

    private void getPhotoFromCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                try {
                    file = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                // Continue only if the File was successfully created
                if (file != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            getApplicationContext().getPackageName()+".fileprovider",
                            file);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                }
            }else{
                file = new File(Environment.getExternalStorageDirectory()
                        + File.separator + SQLConstants.FILE_FOLDER_NAME, "tempPic.png");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            }
            startActivityForResult(takePictureIntent, TAKE_PICTURE);

        }else {
            Toast.makeText(this, "No Camera available in your device.", Toast.LENGTH_LONG).show();
        }
    }
    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("tempPic", ".png", storageDir);
        if(!image.exists())
            image.createNewFile();

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_CANCELED)
            return;

        switch (requestCode)
        {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {

                    if (mBitmap != null)
                        mBitmap.recycle();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inTempStorage = new byte[16 * 1024];
                    {

                        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                        {
                            File dir = new File(
                                    Environment.getExternalStorageDirectory()
                                            + File.separator
                                            + SQLConstants.FILE_FOLDER_NAME);
                            if (dir.exists() == false) {
                                dir.mkdirs();
                            }
                            file = new File(dir, "tempPic.png");
                        }

                    }
                    try {
                        options.inSampleSize=2;
                        mBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);

                        if (file != null) {
                            file.delete();
                        }

                        if(mBitmap != null) {
                            saveBitmap();
                        }

                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;

            // Gallery image
            case PICTURE_FROM_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    Uri imgPath = data.getData();
                    try {
                        if (mBitmap != null)
                            mBitmap.recycle();

                        InputStream is = getContentResolver().openInputStream(
                                imgPath);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inTempStorage = new byte[16 * 1024];

                        mBitmap = BitmapFactory.decodeStream(is, null, options);
                        is.close();

                        file= new File(imgPath.getPath());

                    }catch (Exception e) {
                      e.printStackTrace();
                    }

                    if(mBitmap != null) {
                        saveBitmap();
                    }

                }
                break;

        }

    }

    public void imageSourceDialog()
    {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this)
                .setTitle(getString(R.string.choose_option));
                dialog.setItems(new CharSequence[]
                                {getString(R.string.camera), getString(R.string.gallery), getString(R.string.cancel)},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        checkCameraPermission();
                                        break;
                                    case 1:
                                        checkGalleryPermission();
                                        break;
                                    case 2:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        });
                dialog.show();
            }

    public  void checkCameraPermission()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_CAMERA, MY_PERMISSIONS_REQUEST_CAMERA);
        }else{
            getPhotoFromCamera();
        }
    }
    public  void checkGalleryPermission()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_CAMERA, MY_PERMISSIONS_REQUEST_GALLERY);
        }else{
            getPhotosFromGallery();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
                    getPhotoFromCamera();
                } else {
                    checkCameraPermission();
                }
            }
            break;
            case MY_PERMISSIONS_REQUEST_GALLERY: {
                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
                    getPhotosFromGallery();
                } else {
                    checkGalleryPermission();
                }
            }
            break;
        }
    }

    private void saveBitmap() {
        if (mBitmap == null)
            return;

        int imageWidth=mBitmap.getWidth();
        int imageHeight=mBitmap.getHeight();
        int maxSize = Math.max(imageWidth, imageHeight);
        if(maxSize > 800.0f)
        {
            // if bitmap width greater than height then width equal to 800 and height eqaul to 600
            if(imageWidth > imageHeight) {
                mBitmap = Bitmap.createScaledBitmap(mBitmap,800, 600, false);
            }
            // if bitmap width less than height then width equal to 600 and height eqaul to 800
            else if(imageWidth < imageHeight){
                mBitmap = Bitmap.createScaledBitmap(mBitmap,600, 800, false);
            }
            // If bitmap width and height both are equal then width and height both equal to 800
            else{
                float scalecoef = 800.0f / maxSize;
                mBitmap = Bitmap.createScaledBitmap(mBitmap,
                        (int) (scalecoef * imageWidth),
                        (int) (scalecoef * imageHeight), false);
            }
        }


        final Handler handler = new Handler();  //Optional. Define as a variable in your activity.

        Runnable runBitMap = new Runnable()
        {
            @Override
            public void run()
            {
                // your code here
                handler.post(new Runnable()  //If you want to update the UI, queue the code on the UI thread
                {
                    public void run()
                    {

                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                        File dir = new File(Environment.getExternalStorageDirectory()
                                + File.separator + SQLConstants.FILE_FOLDER_NAME);
                        if (dir.exists() == false) {
                            dir.mkdirs();
                        }


                        mImagePathTxt = Environment.getExternalStorageDirectory()
                                + File.separator + SQLConstants.FILE_FOLDER_NAME + File.separator
                                + UUID.randomUUID().toString() + SQLConstants.IMAGE_TYPE;

                        File f = new File(mImagePathTxt);

                        try {
                            f.createNewFile();
                            FileOutputStream fo = new FileOutputStream(f);
                            fo.write(bytes.toByteArray());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        isImageAttached = true;
                        mTxtImagePath.setText(mImagePathTxt);

                    }
                });
            }
        };
        Thread t = new Thread(runBitMap);
        t.start();

    }
}