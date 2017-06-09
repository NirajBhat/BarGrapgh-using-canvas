package com.nexteducation.downloadmanager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by suresh on 2/26/2016.
 */
public class DownloadUtils {
    private static int TYPE_WIFI = 1;
    private static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            }

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE;
            }
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi";
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

    public static String getBasePath(Context context) {
        String state = Environment.getExternalStorageState();
        String baseDir;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File baseDirFile = context.getExternalFilesDir(null);
            if (baseDirFile == null) {
                baseDir = context.getFilesDir().getAbsolutePath();
            } else {
                baseDir = baseDirFile.getAbsolutePath();
            }
        } else {
            baseDir = context.getFilesDir().getAbsolutePath();
        }
        return baseDir;
    }

    public static String getContentPath(Context context) {
        File file = context.getExternalFilesDir(null);
        return file.getAbsolutePath() + File.separator + "NextBooks/data/content";
    }

    public static String getAppMainPath(Context context) {
        File file = context.getExternalFilesDir("NextBooks");
        return file.getAbsolutePath();
    }

    public static String getQuestionAttachmentPath(Context context) {
        File file = context.getExternalFilesDir(null);
        return file.getAbsolutePath() + File.separator + "NextBooks/data/previewQuestionAttachment";
    }

    public static boolean isFileExistsInTemp(Context context, String uri) {
        File file = context.getExternalFilesDir("NextBooks/temp");
        System.out.println("inside isFileExistsInTemp = " + file.getAbsolutePath());
        file = new File(file.getAbsolutePath() + uri);
        System.out.println("inside isFileExistsInTemp, appended file = " + file.getAbsolutePath());
        System.out.println("inside isFileExistsInTemp, file exists = " + file.exists());
        return (file.exists());
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        }
        return false;
    }

    public static int getCurrentDownloadState(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD, Context.MODE_PRIVATE);
        int currentDownloadState = sharedPreferences.getInt(DownloadConstants.DOWNLOAD_STATE, DownloadService.DownloadState.COMPLETED.getDownloadCode());
        return currentDownloadState;
    }

    public synchronized static void storeDownloadState(Context context, int state) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(DownloadConstants.DOWNLOAD_STATE, state);
        editor.commit();
    }

    public static void storeWifiSettingState(Context context, boolean iswifiConnected) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DownloadConstants.PREF_WIFI, iswifiConnected);
        editor.commit();

    }

    public static boolean getWifiSettingsState(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD, Context.MODE_PRIVATE);
        boolean wifiSetting = sharedPreferences.getBoolean(DownloadConstants.PREF_WIFI, false);
        return wifiSetting;
    }


    public static void storeApplicationState(Context context, boolean appstate) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DownloadConstants.APP_STATE, appstate);
        editor.commit();
    }

    public static boolean getAppState(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD, Context.MODE_PRIVATE);
        boolean appstate = sharedPreferences.getBoolean(DownloadConstants.APP_STATE, false);
        return appstate;
    }

    public static void storeSessionId(Context context, String sessionId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DownloadConstants.SESSION_ID, sessionId);
        editor.commit();
    }

    public static void storeStringData(Context context, String key, String value) {
        System.out.println("key,value" + key + "========" + value);
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String retrieveStringData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, "");
        System.out.println("value" + value);
        return value;
    }

    public static String getSessionId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(DownloadConstants.SESSION_ID, "");
        return sessionId;
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void storeTotalQueueInPreference(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(DownloadManager.getInstance().getTotalQueue());
        editor.putString(DownloadConstants.PREF_TOTALQUEUE, json);
        editor.commit();
    }

    public static ArrayList<DownloadRequest> getTotalDownloadQueue(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(DownloadConstants.PREF_TOTALQUEUE, null);
        Type type = new TypeToken<ArrayList<DownloadRequest>>() {
        }.getType();
        ArrayList<DownloadRequest> arrayList = gson.fromJson(json, type);
        return arrayList;
    }

    public static void removeAllFilesInDir(Context context) {
        System.out.println("insdie removeAllFilesInDir, " + DownloadUtils.class.getName());
        deleteAll(context, new File(getContentPath(context)));
        deleteAll(context, new File(getQuestionAttachmentPath(context)));
    }

    public static void storeDownloadConfig(Context context, JsonObject configObject) {
        System.out.println("inside storeDownloadConfiguration, config = " + configObject);
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD_CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String data = configObject.toString();
        editor.putString("config", data);
        editor.commit();
    }

    public static String getDownloadConfig(Context context) {
        System.out.println("inside getDownloadConfig");
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD_CONFIG, Context.MODE_PRIVATE);
        String config = sharedPreferences.getString("config", null);
        return config;
    }

    public static String getHostUrl(Context context) {
        String downloadConfig = getDownloadConfig(context);
        String hostUrl = null;
        try {
            JSONObject jsonObject = new JSONObject(downloadConfig);
            hostUrl = jsonObject.getString("hostUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hostUrl;
    }

    public static String getAppDir(Context context) {
        String downloadConfig = getDownloadConfig(context);
        String appDir = null;
        try {
            JSONObject jsonObject = new JSONObject(downloadConfig);
            appDir = jsonObject.getString("appDir");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return appDir;
    }


    private static void deleteAll(Context context, File file) {
        System.out.println("inside deleteAll...");
        File downloadQueueFile = new File(DownloadUtils.getContentPath(context) + File.separator + "downloadqueue");
        if (file.exists()) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    if (new File(children[i]) != downloadQueueFile) {
                        new File(file, children[i]).delete();
                    }

                }
            }
        }
    }
}
