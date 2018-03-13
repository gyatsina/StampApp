package com.example.gyatsina.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import android.view.Menu;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    static final int REQUEST_TAKE_PHOTO = 1001;
    private PermissionsHelper permissionsHelper;
    private StampApi stampApi;
    private File file;
    private String realPath;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        realPath = image.getAbsolutePath();
        DebugLogger.e("================uri camera ", realPath);
        return image;
    }

    @Override
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setAction(Intent.ACTION_GET_CONTENT);

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
                        file = new File(realPath);
                        ImageView mImageView = changeStampPhotoVisibility(View.VISIBLE);
                        Picasso.with(this).load(uri).into(mImageView);
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:

// https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media
                if (resultCode == Activity.RESULT_OK) {
                    if (resultData != null) {
                        Bundle extras = resultData.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        ImageView mImageView = changeStampPhotoVisibility(View.VISIBLE);
                        mImageView.setImageBitmap(imageBitmap);

                        Uri tempUri = getImageUri(imageBitmap);
//                        realPath = getRealPathFromURI(tempUri);
//                        DebugLogger.e("================uri camera ", realPath);
                    }
                }
                break;
        }
    }

    public Uri getImageUri(Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), image, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri contentUri) {

        String result;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;

    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    private String getGalleryPhotoName(Uri contentUri) {
        String displayName = null;
        Cursor cursor = getContentResolver().query(contentUri,
                null, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        if (cursor != null && cursor.moveToFirst()) {
            displayName = cursor.getString(
                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        }

        return displayName;
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
        Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.stamp_photo_error), Toast.LENGTH_SHORT);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ImageIdSentEvent event) {
        Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.camera_no_permission), Toast.LENGTH_SHORT);
        cleanRealPathToFile();
        changeStampRecycleViewVisibility(View.GONE);
        changeWelcomeTextVisibility(View.VISIBLE);
    }
}
