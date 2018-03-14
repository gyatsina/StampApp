package com.example.gyatsina.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gyatsina.firstapp.camera.PermissionsHelper;
import com.example.gyatsina.firstapp.image_processing.RealPathUtil;
import com.example.gyatsina.firstapp.logger.DebugLogger;
import com.example.gyatsina.firstapp.network.StampApi;
import com.example.gyatsina.firstapp.network.StampObj;
import com.example.gyatsina.firstapp.network.events.ImageIdSentEvent;
import com.example.gyatsina.firstapp.network.events.LoginEvent;
import com.example.gyatsina.firstapp.network.events.StampListErrorEvent;
import com.example.gyatsina.firstapp.network.events.StampListReceivedEvent;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.gyatsina.firstapp.camera.PermissionsHelper.CAMERA_PERMISSION_REQUEST_CODE;
import static com.example.gyatsina.firstapp.camera.PermissionsHelper.STORAGE_PERMISSION_REQUEST_CODE;
import static com.example.gyatsina.firstapp.network.events.LoginEvent.SUCCESS;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, IMainContract.MainView, OnGridClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 1000;
    private static final int REQUEST_READ_GALLERY = 42;
    private PermissionsHelper permissionsHelper;
    private StampApi stampApi;
    private String realPath;
    private String cameraPhotoName;
    private File cameraPhotoFile;

    private void initializeApi() {
        StampApplication app = (StampApplication) getApplication();
        if (app != null) {
            stampApi = app.getStampApi();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        StampApplication.getAppComponent().inject(this);

        initializeApi();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        changeProgressBarStatus(View.GONE);
        changeWelcomeTextVisibility(View.VISIBLE);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                stampApi.login();
                if (realPath != null) {
                    changeProgressBarStatus(View.VISIBLE);
                    DebugLogger.e("================FloatingActionButton click ", realPath);
                    stampApi.uploadFile(getApplicationContext(), realPath);
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            getApplicationContext().getResources().getText(R.string.float_button_error),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                permissionsHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            case STORAGE_PERMISSION_REQUEST_CODE:
                permissionsHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            checkCameraPermissions();
        } else if (id == R.id.nav_gallery) {
            openGallery();
            checkStoragePermissions();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void checkCameraPermissions() {
        permissionsHelper = new PermissionsHelper(this, new PermissionsHelper.ICameraPermissionResult() {
            @Override
            public void onCameraPermissionGranted() {
                DebugLogger.d(TAG, "Camera permission granted");
                takePicture();
            }

            @Override
            public void onCameraPermissionDenied() {
                // do nothing
                DebugLogger.d(TAG, "Camera permission not granted");
                Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.camera_no_permission), Toast.LENGTH_SHORT);
            }
        });

        permissionsHelper.checkCameraPermissions();
    }

    @Override
    public void checkStoragePermissions() {
        permissionsHelper = new PermissionsHelper(this, new PermissionsHelper.IStoragePermissionResult() {
            @Override
            public void onStoragePermissionGranted() {
                DebugLogger.d(TAG, "Storage permission granted");
            }

            @Override
            public void onStoragePermissionDenied() {
                // do nothing
                DebugLogger.d(TAG, "Storage permission not granted");
                Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.storage_no_permission), Toast.LENGTH_SHORT);
            }
        });

        permissionsHelper.checkStoragePermissions();
    }

    @Override
    public void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created

            if (photoFile != null) {
                DebugLogger.e("===== photoFile ", photoFile+"");
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.gyatsina.firstapp.fileprovider",
                        photoFile);
//                Uri photoURI = Uri.fromFile(photoFile);
                DebugLogger.e("===== photoURI ", photoURI+"");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String suffix = ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                suffix,         /* suffix */
                storageDir      /* directory */
        );
        cameraPhotoFile = image;
        cameraPhotoName = imageFileName + suffix;

        // Save a file: path for use with ACTION_VIEW intents
        realPath = image.getAbsolutePath();
        DebugLogger.e("================uri camera ", realPath);
        return image;
    }

    @Override
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, REQUEST_READ_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        changeWelcomeTextVisibility(View.GONE);

        switch (requestCode) {
            case REQUEST_READ_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    if (resultData != null) {
                        Uri uri = resultData.getData();
                        DebugLogger.e("================uri gallery ", uri + "");

                        realPath = RealPathUtil.getRealPathUri(this, uri);
                        DebugLogger.e("================realPath gallery ", realPath + "");
                        ImageView mImageView = changeStampPhotoVisibility(View.VISIBLE);
                        Picasso.with(this).load(uri).into(mImageView);
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:

// https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media
                if (resultCode == Activity.RESULT_OK) {
                    if (resultData != null) {
//                        Bundle extras = resultData.getExtras();
////                        if(extras!=null) {
//                            Bitmap imageBitmap = (Bitmap) extras.get("data");
//                            ImageView mImageView = changeStampPhotoVisibility(View.VISIBLE);
//                            mImageView.setImageBitmap(imageBitmap);
////                        }
//
////                        Uri tempUri = getImageUri(imageBitmap);
////                        realPath = getRealPathFromURI(tempUri);
////                        DebugLogger.e("================uri camera ", realPath);


//                        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath(), cameraPhotoName);
//                        Uri uri = Uri.fromFile(file);
                        Uri uri = Uri.fromFile(cameraPhotoFile);

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            bitmap = crupAndScale(bitmap, 300); // if you mind scaling
                            ImageView mImageView = changeStampPhotoVisibility(View.VISIBLE);
                            mImageView.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }

    public static  Bitmap crupAndScale (Bitmap source,int scale){
        int factor = source.getHeight() <= source.getWidth() ? source.getHeight(): source.getWidth();
        int longer = source.getHeight() >= source.getWidth() ? source.getHeight(): source.getWidth();
        int x = source.getHeight() >= source.getWidth() ?0:(longer-factor)/2;
        int y = source.getHeight() <= source.getWidth() ?0:(longer-factor)/2;
        source = Bitmap.createBitmap(source, x, y, factor, factor);
        source = Bitmap.createScaledBitmap(source, scale, scale, false);
        return source;
    }

    private ImageView changeStampPhotoVisibility(int visibility) {
        ImageView mImageView = findViewById(R.id.picked_photo);
        mImageView.setVisibility(visibility);

        return mImageView;
    }

    private void changeStampRecycleViewVisibility(int visibility) {
        RecyclerView mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setVisibility(visibility);
    }

    private void changeWelcomeTextVisibility(int visibility) {
        TextView mWelcomeText = findViewById(R.id.welcome);
        mWelcomeText.setVisibility(visibility);
    }

    private void changeProgressBarStatus(int visibility) {
        ProgressBar pgsBar = findViewById(R.id.pBar);
        pgsBar.setVisibility(visibility);
    }

    private void initStampRecycleView(RecyclerView rView, List<StampObj> stampList) {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        rView.setLayoutManager(mLayoutManager);

        RecyclerView.Adapter mAdapter = new GridAdapter(this, stampList, this);
        rView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(StampObj item) {
        stampApi.sendImageId(item.getId());
    }

    private void cleanRealPathToFile() {
        realPath = null;
    }

    // -------------------- =================== EVENTS ===================== ---------------------------------
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginEvent event) {
        int responseCode = event.getResponseCode();
        if (responseCode == SUCCESS) {
            Toast.makeText(this, "all ok", Toast.LENGTH_LONG).show();
            DebugLogger.v("onMessageEvent", "success");
//            stampApi.uploadFile(getApplicationContext(), file);
        } else {
            Toast.makeText(this, R.string.request_error, Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StampListReceivedEvent event) {
        List<StampObj> stampList = event.getStampObjects();
        changeStampPhotoVisibility(View.GONE);

        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        changeStampRecycleViewVisibility(View.VISIBLE);
        changeProgressBarStatus(View.GONE);

        initStampRecycleView(recyclerView, stampList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StampListErrorEvent event) {
        changeProgressBarStatus(View.GONE);
        Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.stamp_photo_error), Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ImageIdSentEvent event) {
        Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.id_chosen), Toast.LENGTH_SHORT).show();
        cleanRealPathToFile();
        changeStampRecycleViewVisibility(View.GONE);
        changeWelcomeTextVisibility(View.VISIBLE);
    }
}
