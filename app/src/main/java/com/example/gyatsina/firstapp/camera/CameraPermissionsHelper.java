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

public class CameraPermissionsHelper {
    /*
     * ======================== VARIABLES =============================
    */

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 34;
    private Activity mActivity;
    private IPermissionResult mCallback;

    private static final int explanationTitleId = R.string.camera_permission_title;
    private static final int explanationMessageId = R.string.camera_permission_explanation;

    /*
    * ======================== INTERFACES =============================
    */
    public interface IPermissionResult {

        void onPermissionGranted();

        void onPermissionDenied();
    }

    /*
    * ======================== CONSTRUCTORS =============================
    */
    public CameraPermissionsHelper(@NonNull Activity activity, @NonNull IPermissionResult callback) {
        mActivity = activity;
        mCallback = callback;
    }

    /*
    * ======================== METHODS =============================
    */
    public static boolean isCameraPermissionGranted(@NonNull Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }


    public void checkPermissions() {
        if (isCameraPermissionGranted(mActivity)) {
            mCallback.onPermissionGranted();
            return;
        }


        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CAMERA)) {
            showExplanation();
        } else {
            requestPermission();
        }
    }

    private void showExplanation() {
        if (explanationMessageId > 0) {
            showMessage((dialog, which) -> requestPermission(), (dialog, which) -> mCallback.onPermissionDenied());
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(mActivity,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void showMessage(DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(mActivity)
                .setTitle(explanationTitleId)
                .setMessage(explanationMessageId)
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
                    mCallback.onPermissionGranted();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CAMERA)) {
                        mCallback.onPermissionDenied();
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
