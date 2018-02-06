package com.example.gyatsina.firstapp.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.example.gyatsina.firstapp.R;

public class PermissionsHelper {
    /*
     * ======================== VARIABLES =============================
    */

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 34;
    public static final int STORAGE_PERMISSION_REQUEST_CODE = 35;
    private Activity mActivity;
    private ICameraPermissionResult mCameraCallback;
    private IStoragePermissionResult mStorageCallback;

    private static final int cameraExplanationTitleId = R.string.camera_permission_title;
    private static final int storageExplanationTitleId = R.string.storage_permission_title;
    private static final int cameraExplanationMessageId = R.string.camera_permission_explanation;
    private static final int storageExplanationMessageId = R.string.storage_permission_explanation;

    /*
    * ======================== INTERFACES =============================
    */
    public interface ICameraPermissionResult {

        void onCameraPermissionGranted();

        void onCameraPermissionDenied();
    }

    public interface IStoragePermissionResult {

        void onStoragePermissionGranted();

        void onStoragePermissionDenied();
    }

    /*
    * ======================== CONSTRUCTORS =============================
    */
    public PermissionsHelper(@NonNull Activity activity, @NonNull ICameraPermissionResult callback) {
        mActivity = activity;
        mCameraCallback = callback;
    }

    public PermissionsHelper(@NonNull Activity activity, @NonNull IStoragePermissionResult callback) {
        mActivity = activity;
        mStorageCallback = callback;
    }

    /*
    * ======================== METHODS =============================
    */
    public static boolean isCameraPermissionGranted(@NonNull Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isStogarePermissionGranted(@NonNull Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }


    public void checkCameraPermissions() {
        if (isCameraPermissionGranted(mActivity)) {
            mCameraCallback.onCameraPermissionGranted();
            return;
        }


        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CAMERA)) {
            showExplanation(cameraExplanationTitleId, cameraExplanationMessageId);
        } else {
            requestCameraPermission();
        }
    }

    public void checkStoragePermissions() {
        if (isStogarePermissionGranted(mActivity)) {
            mStorageCallback.onStoragePermissionGranted();
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showExplanation(storageExplanationTitleId, storageExplanationMessageId);
        } else {
            requestStoragePermission();
        }
    }

    private void showExplanation(int titleId, int messageId) {
        if (messageId > 0) {
            showMessage((dialog, which) -> requestCameraPermission(), (dialog, which) -> mStorageCallback.onStoragePermissionDenied(), titleId, messageId);
        } else {
            requestStoragePermission();
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(mActivity,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(mActivity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_REQUEST_CODE);
    }

    private void showMessage(DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener, int titleId, int messageId) {
        new AlertDialog.Builder(mActivity)
                .setTitle(titleId)
                .setMessage(messageId)
                .setCancelable(false)
                .setPositiveButton(mActivity.getString(R.string.ok), okListener)
                .setNegativeButton(mActivity.getString(R.string.do_not_allow), cancelListener)
                .create()
                .show();
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCameraCallback.onCameraPermissionGranted();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CAMERA)) {
                        mCameraCallback.onCameraPermissionDenied();
                    } else {
                        //TODO
                    }
                }
                return;
            case STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mStorageCallback.onStoragePermissionGranted();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        mStorageCallback.onStoragePermissionGranted();
                    } else {
                        //TODO
                    }
                }
                return;
            default:
                return;
        }
    }

}
