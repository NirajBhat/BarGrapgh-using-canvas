package com.nexteducation.downloadmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suresh on 2/16/2016.
 */
public class ServiceBroadCastReceiver extends BroadcastReceiver {
    private Context _context;

    @Override
    public void onReceive(Context context, Intent intent) {
        _context = context;
        boolean wifiSetting = DownloadUtils.getWifiSettingsState(context);
        Log.v("cordova", "inside service broadcast");
        String action = intent.getAction();

        DownloadManager dmInstance = DownloadManager.getInstance();
        if(dmInstance == null) {
            dmInstance = DownloadManager.createInstance(context, DownloadUtils.getAppDir(context), DownloadUtils.getHostUrl(context));
        }


        if (action.equalsIgnoreCase(DownloadConstants.SERVICE_STARTED)) {
            String storedQueueJson = DownloadManager.getDownloadRequestsFromAlreadyStoredFile(_context);

            ArrayList<DownloadRequest> downloadRequests = new Gson().fromJson(storedQueueJson, new TypeToken<List<DownloadRequest>>() {
            }.getType());
            Log.v("cordova", "stored file size i added" + dmInstance.getDownloadRequestQueueItems().size());
            if (downloadRequests != null && downloadRequests.size() > 0) {
                Log.v("cordova", "inside stored file" + downloadRequests.size());
                for (DownloadRequest downloadRequest : downloadRequests) {
                    if (!dmInstance.getDownloadRequestQueueItems().contains(downloadRequest)) {
                        dmInstance.getDownloadRequestQueueItems().add(downloadRequest);
                    }
                }
//                File file = new File(DownloadUtils.getContentPath(_context) + File.separator + "downloadqueue");
                File file = new File(DownloadUtils.getAppDir(_context) + File.separator + "downloadqueue");
                deleteRecursive(file);
            }
            if (DownloadUtils.getTotalDownloadQueue(context) != null && DownloadUtils.getTotalDownloadQueue(context).size() > 0) {
                for (DownloadRequest downloadRequest : DownloadUtils.getTotalDownloadQueue(context)) {
                    if (!dmInstance.getTotalQueue().contains(downloadRequest)) {
                        dmInstance.getTotalQueue().add(downloadRequest);
                    }
                }
            }


            Log.v("cordova", "coming to download" + DownloadUtils.getCurrentDownloadState(context));

            if ((DownloadUtils.getCurrentDownloadState(context) == DownloadService.DownloadState.INPROGRESS.getDownloadCode())) {
                Log.v("cordova", "init download" + dmInstance.getDownloadRequestQueueItems().size());

                dmInstance.initDownload(context);
                DownloadUtils.storeApplicationState(context, true);
            }
        } else if (action.equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE")) {
            WifiManager wifiManager = (WifiManager) _context.getSystemService(Context.WIFI_SERVICE);
            boolean wifistatus = wifiManager.isWifiEnabled();
            Log.v("downloadplugin", "wifistatus" + wifistatus);
            if ((DownloadUtils.isWifiConnected(_context) && DownloadUtils.getCurrentDownloadState(context) == DownloadService.DownloadState.NETWORKPAUSE.getDownloadCode() && wifiSetting) || (!wifiSetting && DownloadUtils.getCurrentDownloadState(context) == DownloadService.DownloadState.NETWORKPAUSE.getDownloadCode() && DownloadUtils.isNetworkAvailable(_context))) {
                Log.v("downloadplugin", "init download");
                dmInstance.initDownload(context);
                DownloadUtils.storeApplicationState(context, true);
            }
        }
    }

    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.exists()) {
            if (fileOrDirectory.isDirectory()) {
                for (File child : fileOrDirectory.listFiles()) {
                    deleteRecursive(child);
                }
            }

            fileOrDirectory.delete();
        }

    }

    public String getDownloadRequestsFromAlreadyStoredFile() {
        StringBuffer buffer = new StringBuffer();
        try {
            File dirPath = new File(DownloadUtils.getContentPath(_context) + File.separator + "downloadqueue" + File.separator + "downloadqueue.txt");

            if (dirPath.exists()) {
                FileReader fileReader = new FileReader(dirPath);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                }
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }


}
