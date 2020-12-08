package com.yt.easyfloat.permission.rom;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RomUtils {
    private final static String TAG = "RomUtils";

    public static String getSystemProperty(String propName) {
        String property = null;
        BufferedReader input = null;
        try {
            Process process = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
            property = input.readLine();
            input.close();
        } catch (IOException e) {
            Log.e(TAG, "Unable to read sysprop " + propName, (Throwable) e);
            property = null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return property;
    }

    /**
     * 获取 emui 版本号
     */
    public static double getEmuiVersion() {
        try {
            String emuiVersion = getSystemProperty("ro.build.version.emui");
            if (emuiVersion != null) {
                String version = emuiVersion.substring(emuiVersion.indexOf("_") + 1);
                return Double.parseDouble(version);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 4.0;
    }

    /**
     * 获取小米 rom 版本号，获取失败返回 -1
     *
     * @return miui rom version code, if fail , return -1
     */
    public static int getMiuiVersion() {
        int version = -1;
        try {
            String versionStr = getSystemProperty("ro.miui.ui.version.name");
            versionStr = versionStr.substring(1);
            version = Integer.parseInt(versionStr);
        } catch (Exception e) {
            version = -1;
            Log.e(TAG, "get miui version code error");
        }
        return version;
    }

    public static boolean checkIsMiuiRom() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    public static boolean checkIsMeizuRom() {
        String systemProperty = getSystemProperty("ro.build.display.id");
        if (TextUtils.isEmpty(systemProperty)) {
            return false;
        }
        return systemProperty.toLowerCase().contains("flyme");
    }

    public static boolean checkIsHuaweiRom() {
        return Build.MANUFACTURER != null && Build.MANUFACTURER.contains("HUAWEI");
    }

    public static boolean checkIs360Rom() {
        return Build.MANUFACTURER != null && (Build.MANUFACTURER.contains("QiKU") || Build.MANUFACTURER.contains("360"));
    }

    public static boolean checkIsOppoRom() {
        return Build.MANUFACTURER != null && (Build.MANUFACTURER.toLowerCase().contains("oppo"));
    }

    public static boolean checkIsVivoRom() {
        return Build.MANUFACTURER != null && Build.MANUFACTURER.toLowerCase().contains("vivo");
    }
}
