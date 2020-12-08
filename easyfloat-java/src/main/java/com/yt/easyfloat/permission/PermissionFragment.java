package com.yt.easyfloat.permission;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.yt.easyfloat.interfaces.OnPermissionResult;
import com.yt.easyfloat.utils.Logger;


public class PermissionFragment extends Fragment {
    private final static String TAG = "PermissionFragment";
    private static OnPermissionResult onPermissionResult;

    public static void requestPermission(Activity activity, OnPermissionResult onPermissionResult) {
        PermissionFragment.onPermissionResult = onPermissionResult;
        activity.getFragmentManager().beginTransaction().add(new PermissionFragment(), activity.getLocalClassName()).commitAllowingStateLoss();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PermissionUtils.requestPermission(this);
        Logger.i("PermissionFragment：requestPermission");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Activity activity = getActivity();
        if (requestCode == PermissionUtils.requestCode) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                   boolean check = PermissionUtils.checkPermission(activity);
                   Logger.i("PermissionFragment onActivityResult: " + check);
                   if (PermissionFragment.onPermissionResult != null) {
                       PermissionFragment.onPermissionResult.permissionResult(check);
                   }
                   PermissionFragment.this.getFragmentManager().beginTransaction().remove(PermissionFragment.this).commitAllowingStateLoss();
                }
            }, 500L);
        }
    }
}
