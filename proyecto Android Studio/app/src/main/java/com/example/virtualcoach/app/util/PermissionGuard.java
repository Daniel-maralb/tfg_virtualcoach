package com.example.virtualcoach.app.util;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.virtualcoach.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionGuard {

    public interface OnPermissionGranted extends Runnable {
    }

    public interface OnPermissionDenied extends Runnable {
    }

    private static final Logger LOG = LoggerFactory.getLogger(PermissionGuard.class);
    private static final String ERROR_MSG_TEMPLATE = "Action requires permission: %s";

    private final Fragment fragment;
    private final String permission;
    private final int rationale_resId;
    private OnPermissionGranted onPermissionGranted;

    private OnPermissionDenied onPermissionDenied;

    private final ActivityResultLauncher<String> permissionLauncher;

    public PermissionGuard(Fragment fragment, String permission, int rationale_resId) {
        this.fragment = fragment;
        this.permission = permission;
        this.rationale_resId = rationale_resId;
        this.onPermissionDenied = () -> Toast.makeText(fragment.getContext(), getErrorMsg(), Toast.LENGTH_LONG).show();

        this.permissionLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {
                        LOG.debug(getErrorMsg());

                        if (fragment.shouldShowRequestPermissionRationale(permission)) {
                            getRationaleDialog().show();
                            return;
                        }
                        onPermissionDenied.run();
                        return;
                    }

                    LOG.debug("Permission granted: " + permission);
                    onPermissionGranted.run();
                });
    }

    public void request(OnPermissionGranted onPermissionGranted) {
        this.onPermissionGranted = onPermissionGranted;

        request();
    }

    private void request() {
        if (isGranted(permission)) {
            onPermissionGranted.run();
            return;
        }
        startRequest(permission);
    }

    public void setOnPermissionDenied(OnPermissionDenied onPermissionDenied) {
        this.onPermissionDenied = onPermissionDenied;
    }

    private boolean isGranted(String permission) {
        return ContextCompat.checkSelfPermission(fragment.getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private String getErrorMsg() {
        return String.format(ERROR_MSG_TEMPLATE,
                permission.replace("android.permission.", ""));
    }

    private void startRequest(String permission) {
        permissionLauncher.launch(permission);
    }

    private AlertDialog getRationaleDialog() {
        return new AlertDialog.Builder(fragment.getContext())
                .setTitle(R.string.permission_guard_title)
                .setMessage(R.string.permission_storage_rationale)
                .setPositiveButton(fragment.getString(R.string.permission_guard_possitive),
                        (dialog, which) -> startRequest(permission))
                .setNegativeButton(fragment.getString(R.string.permission_guard_negative),
                        (dialog, which) -> onPermissionDenied.run())
                .create();
    }
}