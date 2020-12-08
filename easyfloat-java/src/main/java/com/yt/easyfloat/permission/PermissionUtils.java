package com.yt.easyfloat.permission;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.yt.easyfloat.interfaces.OnPermissionResult;
import com.yt.easyfloat.permission.rom.HuaweiUtils;
import com.yt.easyfloat.permission.rom.MeizuUtils;
import com.yt.easyfloat.permission.rom.MiuiUtils;
import com.yt.easyfloat.permission.rom.OppoUtils;
import com.yt.easyfloat.permission.rom.QikuUtils;
import com.yt.easyfloat.permission.rom.RomUtils;
import com.yt.easyfloat.utils.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class PermissionUtils {
    public final static int requestCode = 1199;
    private final static String TAG = "PermissionUtils";

    /**
     * 检测是否有悬浮窗权限
     * 6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
     */
    public static boolean checkPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (RomUtils.checkIsHuaweiRom()) {
                return huaweiPermissionCheck(context);
            }
            if (RomUtils.checkIsMiuiRom()) {
                return miuiPermissionCheck(context);
            }
            if (RomUtils.checkIsOppoRom()) {
                return oppoROMPermissionCheck(context);
            }
            if (RomUtils.checkIsMeizuRom()) {
                return meizuPermissionCheck(context);
            }
            if (RomUtils.checkIs360Rom()) {
                return qikuPermissionCheck(context);
            }
            return true;
        }
        return commonROMPermissionCheck(context);
    }


    /**
     * 申请悬浮窗权限
     */
    public static void requestPermission(Activity activity, OnPermissionResult onPermissionResult) {
        PermissionFragment.requestPermission(activity, onPermissionResult);
    }

    public static void requestPermission(Fragment fragment) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (RomUtils.checkIsHuaweiRom()) {
                HuaweiUtils.applyPermission(fragment.getActivity());
            } else if(RomUtils.checkIsMiuiRom()) {
                MiuiUtils.applyMiuiPermission(fragment.getActivity());
            } else if (RomUtils.checkIsOppoRom()) {
                OppoUtils.applyOppoPermission(fragment.getActivity());
            } else if (RomUtils.checkIsMeizuRom()) {
                MeizuUtils.applyPermission(fragment);
            } else if (RomUtils.checkIs360Rom()) {
                QikuUtils.applyPermission(fragment.getActivity());
            } else {
                Logger.i(TAG, "native Android 6.0 not need apply");
            }
        } else {
            commonROMPermissionCheck(fragment);
        }
    }

    private static boolean huaweiPermissionCheck(Context context) {
        return HuaweiUtils.checkFloatWindowPermission(context);
    }

    private static boolean miuiPermissionCheck(Context context) {
        return MiuiUtils.checkFloatWindowPermission(context);
    }

    private static boolean meizuPermissionCheck(Context context) {
        return MeizuUtils.checkFloatWindowPermission(context);
    }

    private static boolean qikuPermissionCheck(Context context) {
        return QikuUtils.checkFloatWindowPermission(context);
    }

    private static boolean oppoROMPermissionCheck(Context context) {
        return OppoUtils.checkFloatWindowPermission(context);
    }

    /**
     * 6.0以后，通用悬浮窗权限检测
     * 但是魅族6.0的系统这种方式不好用，需要单独适配一下
     */
    private static boolean commonROMPermissionCheck(Context context) {
        if (RomUtils.checkIsMeizuRom()) {
            return meizuPermissionCheck(context);
        }
        boolean ret = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Method method = Settings.class.getDeclaredMethod("canDrawOverlays", Context.class);
                ret = (boolean) method.invoke(null, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 通用 rom 权限申请
     */
    private static void commonROMPermissionCheck(Fragment fragment) {
        if (RomUtils.checkIsMeizuRom()) {
            MeizuUtils.applyPermission(fragment);
            return;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            commonROMPermissionApplyInternal(fragment);
        } else {
            Logger.d(TAG, "user manually refuse OVERLAY_PERMISSION");
        }
    }

    public static void commonROMPermissionApplyInternal(Fragment fragment) {
        try {
            Field field = Settings.class.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
            Intent intent = new Intent(field.get(null).toString());
            StringBuilder sb = new StringBuilder();
            sb.append("package:").append(fragment.getActivity().getPackageName());
            intent.setData(Uri.parse(sb.toString()));
            fragment.startActivityForResult(intent,requestCode);
        } catch (Exception e) {
            Logger.e(TAG, e.getMessage());
        }
    }
}
