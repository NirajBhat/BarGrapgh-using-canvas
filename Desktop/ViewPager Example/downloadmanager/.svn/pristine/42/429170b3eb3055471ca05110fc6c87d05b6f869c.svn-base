package com.nexteducation.downloadmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by suresh on 2/10/2016.
 */
public class DownloadManager
{
    private static DownloadManager _instance;
    private static Context _context;
    private String mAppDir;
    private ArrayList<DownloadRequest> _items;
    private ArrayList<DownloadRequest> _currentQueue;
    private ArrayList<DownloadRequest> _incompleteQueue;
    private ArrayList<DownloadRequest> _totalQueue;
    private ArrayList<DownloadProgressListener> _progressListeners;
    private String mHostUrl;

    private String[] types = {"All", "Video", "Activity"};

    private DownloadManager() {
        _items = new ArrayList<DownloadRequest>();
        _currentQueue = new ArrayList<DownloadRequest>();
        _incompleteQueue = new ArrayList<DownloadRequest>();
        _totalQueue = new ArrayList<DownloadRequest>();
        _progressListeners = new ArrayList<>();
    }

    public synchronized static DownloadManager getInstance() {
        return _instance;
    }

    public synchronized static DownloadManager createInstance(Context context, String appDir, String hostUrl) {

        if(null == context || null == appDir || null == hostUrl || appDir.isEmpty() || hostUrl.isEmpty()) {
            return null;
        }

        _context = context;

        if (_instance == null) {
            _instance = new DownloadManager();
            JsonObject configObject = new JsonObject();
            configObject.addProperty("hostUrl", hostUrl);
            configObject.addProperty("appDir", appDir);
            DownloadUtils.storeDownloadConfig(context, configObject);
        }
        return _instance;
    }

    public ArrayList<DownloadRequest> getTotalQueue() {
        return _totalQueue;
    }

    public String removeAllFromQueue(Context context) throws JSONException {
        DownloadManager.getInstance().getDownloadRequestQueueItems().clear();
        _totalQueue.clear();
        DownloadUtils.storeTotalQueueInPreference(context);
        storeDownloadRequestQueueIntoFile(context, _items);
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(DownloadManager.getInstance().getDownloadRequestQueueItems()).getAsJsonArray();
        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.REMOVEALL_FROM_QUEUE);
        jsonObject.put("currentDRQ", jsonArray1.toString());
        return jsonObject.toString();
    }

    public String addToQueue(DownloadProgressListener listener, ArrayList<DownloadRequest> downloadRequests, Context context) throws JSONException {
        Log.v("DownloadPlugin", "addtoqueue method started");

        if (downloadRequests != null) {
            for (DownloadRequest downloadRequestItem : downloadRequests)
            {
                if (!_items.contains(downloadRequestItem)) {
                    _items.add(downloadRequestItem);
                    _totalQueue.add(downloadRequestItem);
                    _progressListeners.add(listener);
                }
            }
        }
        DownloadUtils.storeTotalQueueInPreference(context);
        storeDownloadRequestQueueIntoFile(context, _items);
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(_items).getAsJsonArray();
        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.ADDTO_QUEUE);
        jsonObject.put("currentDRQ", jsonArray1);
        return jsonObject.toString();
    }

    public String addToQueue(DownloadProgressListener listener, DownloadRequest downloadRequest) throws JSONException {
        Log.v("DownloadPlugin", "addtoqueue method started");

        ArrayList<DownloadRequest> downloadRequests = new ArrayList<>();
        downloadRequests.add(downloadRequest);
        return addToQueue(listener, downloadRequests, _context);
    }

    public void startOnDemandDownload(Context context, ArrayList<DownloadRequest> list, DownloadProgressListener listener) throws JSONException {
        if(list == null || list.size() == 0) {
            return;
        }
        pauseDownload(context);
        for (int i = list.size() - 1; i>=0; i-- )
        {
            final DownloadRequest dr = list.get(i);
            if (!_items.contains(dr)) {
                _items.add(0, dr);
                _totalQueue.add(0, dr);
                if(!_progressListeners.contains(listener)) {
                    _progressListeners.add(listener);
                }
            }
        }
        DownloadUtils.storeTotalQueueInPreference(context);
        storeDownloadRequestQueueIntoFile(context, _items);
        resumeDownload(context);
    }

    public void startOnDemandDownload(Context context, DownloadRequest dr, DownloadProgressListener listener) throws JSONException {
        if(dr == null || dr.getDownloadItemVOs().size() == 0) {
            return;
        }
        ArrayList<DownloadRequest> list = new ArrayList<>();
        list.add(dr);
        startOnDemandDownload(context, list, listener);
    }

    public void setStorageDir(String dir) {

    }

    public void setHostUrl() {

    }

    public synchronized void initDownload(Context context) {
        Log.v("cordova", "init " + "initdownload" + _items.size() + "===" + DownloadService.getInstance());
        if (_items.size() > 0) {
            DownloadUtils.storeDownloadState(context, DownloadService.DownloadState.INPROGRESS.getDownloadCode());
            if (DownloadUtils.isMyServiceRunning(context, DownloadService.class)) {
                Log.v("cordova", "download service started" + DownloadUtils.getCurrentDownloadState(context));
                DownloadService.getInstance().startDownload();
            } else {
                Log.v("cordova", "download service not started");
                Intent intent = new Intent(context, DownloadService.class);
                context.startService(intent);
            }
        }
    }

    public void startDownload(Context context) {
        if (_items.size() > 0) {

            // if the status is PAUSE or NETWORKPAUSE, don't change to INPPROGRESS
            // until and unless user won't click on resume explicitly
            int status = DownloadUtils.getCurrentDownloadState(context);
            if (status != DownloadService.DownloadState.NETWORKPAUSE .getDownloadCode() &&
                    status != DownloadService.DownloadState.PAUSE.getDownloadCode()) {
                DownloadUtils.storeDownloadState(context, DownloadService.DownloadState.INPROGRESS.getDownloadCode());
            }
            /*if (DownloadUtils.isMyServiceRunning(context, DownloadService.class)) {
                Log.v("cordova", "download service started" + DownloadUtils.getCurrentDownloadState(context));
                DownloadService.getInstance().startDownload();
            } else {
                Log.v("cordova", "download service not started");
                Intent intent = new Intent(context, DownloadService.class);
                context.startService(intent);
            }*/

            Intent intent = new Intent(context, DownloadService.class);
            context.startService(intent);
        }
    }

    public void startDownload() {
        startDownload(_context);
    }

    public String removeFromQueue(Context context, ArrayList<DownloadRequest> downloadRequests) throws JSONException {
        Log.v("cordova", "remove from queue" + _items.size());
        if (DownloadManager.getInstance().getDownloadRequestQueueItems().size() > 0) {
            for (DownloadRequest downloadRequestItem : downloadRequests) {
                if (_items.contains(downloadRequestItem)) {
                    _items.remove(downloadRequestItem);
                    _totalQueue.remove(downloadRequestItem);
                }
            }
            if (_items.size() == 0) {
                DownloadUtils.storeDownloadState(context, DownloadService.DownloadState.COMPLETED.getDownloadCode());
                storeDownloadRequestQueueIntoFile(context, _items);
                _totalQueue.clear();
            }
        }
        DownloadUtils.storeTotalQueueInPreference(context);
        storeDownloadRequestQueueIntoFile(context, _items);
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(_items).getAsJsonArray();
        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.REMOVE_FROM_QUEUE);
        jsonObject.put("currentDRQ", jsonArray1);
        return jsonObject.toString();
    }

    public String setSessionId(Context context, String sessionId) throws JSONException {
        DownloadUtils.storeSessionId(context, sessionId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.SET_SESSION_ID);
        return jsonObject.toString();
    }

    public String storeData(Context context, String key, String value) throws JSONException {
        DownloadUtils.storeStringData(context, key, value);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.STORE_DATA);
        return jsonObject.toString();
    }

    public String retrieveData(Context context, String key) throws JSONException {
        String data = DownloadUtils.retrieveStringData(context, key);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.RETRIEVE_DATA);
        jsonObject.put("data", data);
        return jsonObject.toString();
    }

    public String retrieveDataWithFileExistence(Context context, String key, String uri) throws JSONException {
        boolean isExist = DownloadUtils.isFileExistsInTemp(context, uri);

        String data = DownloadUtils.retrieveStringData(context, key);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.RETRIEVE_DATA_WITH_FILE_EXISTENCE);
        jsonObject.put("data", data);
        jsonObject.put("fileExist", isExist);
        return jsonObject.toString();
    }

    public String handleOrientationLock(Activity context, Boolean flag) throws JSONException {
        if (flag) {
            int orientation = context.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } else {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.SET_ORIENTATION);
        return jsonObject.toString();
    }

    public String getCompletedQueue(Context context) {
        String storedDatabaseJson = DBHelper.getInstance(context).getDataFromDatabase();
        return storedDatabaseJson;
    }


    public String fetchStatus(Context context) throws JSONException {
        Gson gson = new GsonBuilder().create();
        ArrayList<DownloadRequest> downloadRequests = new ArrayList<DownloadRequest>();
        if (_items.size() > 0) {
            DownloadRequest downloadRequest = _items.get(0);
            downloadRequests.add(downloadRequest);
        }

        JsonArray jsonArrayCompleted = gson.toJsonTree(DownloadManager.getInstance().getCompletedQueueItems()).getAsJsonArray();
        JsonArray jsonArrayInComplete = gson.toJsonTree(DownloadManager.getInstance().getIncompleteQueue()).getAsJsonArray();
        JsonArray jsonArrayDrq = gson.toJsonTree(downloadRequests).getAsJsonArray();
        JSONArray jsonArray1 = new JSONArray(jsonArrayDrq.toString());
        JSONArray jsonArray2 = new JSONArray(jsonArrayCompleted.toString());
        JSONArray jsonArray3 = new JSONArray(jsonArrayInComplete.toString());


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.FETCH_STATUS);
        jsonObject.put("currentDRQ", jsonArray1);
        jsonObject.put("completedDRQ", jsonArray2);
        jsonObject.put("incompleteDRQ", jsonArray3);
        jsonObject.put("downloadstate", DownloadUtils.getCurrentDownloadState(context));
        Log.v("DownloadPlugin", "===" + "storing json status" + jsonObject.toString());
        DownloadManager.getInstance().getCompletedQueueItems().clear();
        DownloadManager.getInstance().getIncompleteQueue().clear();
        return jsonObject.toString();
    }

    public String fetchDRQ(Context context) throws JSONException {
        ArrayList<DownloadRequest> totalDRQ = new ArrayList<DownloadRequest>();
        totalDRQ.addAll(DownloadManager.getInstance().getDownloadRequestQueueItems());
        Gson gson = new GsonBuilder().create();
        JsonArray drq = gson.toJsonTree(totalDRQ).getAsJsonArray();
        JSONArray jsonArray = new JSONArray(drq.toString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.FETCH_DRQ);
        jsonObject.put("DRQ", jsonArray);

        return jsonObject.toString();
    }


    public ArrayList<DownloadRequest> getDownloadRequestQueueItems() {
        return _items;
    }

    public String editDownloadQueue(Context context, ArrayList<DownloadRequest> downloadRequests) throws JSONException {
        if (DownloadManager.getInstance().getDownloadRequestQueueItems().size() > 0) {
            for (DownloadRequest downloadRequest : downloadRequests) {
                if (_items.contains(downloadRequest)) {
                    int index = _items.indexOf(downloadRequest);
                    _items.set(index, downloadRequest);

                    int index1 = _totalQueue.indexOf(downloadRequest);
                    _totalQueue.set(index1, downloadRequest);

                }
            }
        }
        DownloadUtils.storeTotalQueueInPreference(context);
        storeDownloadRequestQueueIntoFile(context, _items);
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(_items).getAsJsonArray();
        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.EDIT_DOWNLOAD_QUEUE);
        jsonObject.put("currentDRQ", jsonArray1);
        return jsonObject.toString();
    }

    public String pauseDownload(Context context) throws JSONException {
        DownloadUtils.storeDownloadState(context, DownloadService.DownloadState.PAUSE.getDownloadCode());
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(DownloadManager.getInstance().getDownloadRequestQueueItems()).getAsJsonArray();
        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.PAUSE_DOWNLOAD);
        jsonObject.put("currentDRQ", jsonArray1);
        return jsonObject.toString();
    }

    public String resumeDownload(Context context) throws JSONException {

        if ((DownloadUtils.getWifiSettingsState(context) && DownloadUtils.isWifiConnected(context)) || (!DownloadUtils.getWifiSettingsState(context) && DownloadUtils.isNetworkAvailable(context))) {
            Log.v("cordova", "==>DownloadState" + "ResumeDownload" + DownloadUtils.getCurrentDownloadState(context) + DownloadManager.getInstance().getDownloadRequestQueueItems().size());
            int currentDownloadState = DownloadUtils.getCurrentDownloadState(context);

            if (currentDownloadState != DownloadService.DownloadState.INPROGRESS.getDownloadCode() && _items.size() > 0) {
                Log.v("cordova", "init " + "resumedownload" + DownloadUtils.getCurrentDownloadState(context));
                initDownload(context);
            }
        }

        Gson gson = new GsonBuilder().create();
        Log.v("DownloadPlugin", "init " + "resumedownload" + DownloadManager.getInstance().getDownloadRequestQueueItems().size());
        JsonArray jsonArray = gson.toJsonTree(DownloadManager.getInstance().getDownloadRequestQueueItems()).getAsJsonArray();
        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.RESUME_DOWNLOAD);
        jsonObject.put("currentDRQ", jsonArray1);
        Log.v("DownloadPlugin", "===" + "storing resume download" + jsonObject.toString());
        return jsonObject.toString();
    }

    public String getMemoryInfo(Context context) throws JSONException {
        double availableInternalGb = getAvailableInternalMemorySize();
        double totalInternalGb = getTotalInternalMemorySize();
        double availbleExtGb = getAvailableExternalMemorySize();
        double totalExtGb = getTotalExternalMemorySize();
        String totalAppSize = getTotalAppSize(context);
        String totalVideosSize = getTotalVideosSize(context);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "getMemoryInfo");
        jsonObject.put("availableInternalMemory", availableInternalGb);
        jsonObject.put("totalInternalMemory", totalInternalGb);
        jsonObject.put("availableExternalMemory", availbleExtGb);
        jsonObject.put("totalExternalMemory", totalExtGb);
        jsonObject.put("totalVideosMemory", totalVideosSize);
        jsonObject.put("totalAppMemory", totalAppSize);
        return jsonObject.toString();
    }

    private String getTotalVideosSize(Context context) {
        long filesize = 0;
        ArrayList<String> videoIds = new ArrayList<String>();
        ArrayList<DownloadRequest> downloadRequests = DBHelper.getInstance(context).getDownloadedArraylist();
        if (downloadRequests != null && downloadRequests.size() > 0) {
            for (DownloadRequest downloadRequest : downloadRequests) {
                if (downloadRequest.getDownloadItemVOs() != null && downloadRequest.getDownloadItemVOs().size() > 0) {
                    for (DownloadItemVO downloadItemVO : downloadRequest.getDownloadItemVOs()) {
                        if (downloadItemVO.getFileType() != null && downloadItemVO.getFileType().equalsIgnoreCase(DownloadConstants.VIDEO_FILE_TYPE)) {
                            videoIds.add(downloadItemVO.getId() + ".app");
                        }
                    }

                }
            }
        }
        File file = new File(DownloadUtils.getContentPath(context));
        if (file.exists()) {
            String[] children = file.list();
            for (String childfile : children) {
                if (videoIds.contains(childfile)) {
                    File child = new File(DownloadUtils.getContentPath(context) + File.separator + childfile);
                    if (child.exists()) {
                        filesize = filesize + child.length();
                    }
                }

            }

        }
        return formatSizeWithString(filesize);
    }

    private String getTotalAppSize(Context context) {
        File file = context.getExternalFilesDir("NextBooks/data/content");
        if (file.exists()) {
            return formatSizeWithString(folderSize(file));
        }
        return "";
    }

    private long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    public String removeContent(ArrayList<DownloadRequest> downloadRequests, Context context) throws JSONException {
        ArrayList<DownloadItemVO> chapterItems = new ArrayList<DownloadItemVO>();
        for (DownloadRequest downloadRequest : downloadRequests) {
            chapterItems.addAll(downloadRequest.getDownloadItemVOs());
        }

        JSONObject jsonObject = new JSONObject();
        ArrayList<DownloadRequest> downloadRequestArrayList = DBHelper.getInstance(context).getDownloadedArraylist();
        if (downloadRequestArrayList != null && downloadRequestArrayList.size() > 0 && chapterItems.size() > 0) {
            for (DownloadRequest downloadRequest : downloadRequestArrayList) {
                Iterator<DownloadItemVO> downloadItemVOIterator = downloadRequest.getDownloadItemVOs().iterator();
                while (downloadItemVOIterator.hasNext()) {
                    DownloadItemVO downloadItemVO = downloadItemVOIterator.next();
                    if (chapterItems.contains(downloadItemVO)) {
                        removeFileFromStorage(context, downloadItemVO.getId());
                        downloadItemVOIterator.remove();
                    }

                }
            }
            String status = "incomplete";
            DBHelper.getInstance(context).updateDB(downloadRequestArrayList, status);
            jsonObject.put("action", "removeVideos");
            jsonObject.put("completedDRQ", downloadRequestArrayList);
        } else {
            downloadRequestArrayList = new ArrayList<DownloadRequest>();
            jsonObject.put("action", "removeVideos");
            jsonObject.put("completedDRQ", downloadRequestArrayList);
        }

        return jsonObject.toString();
    }

    public String getCurrentDownloadQueue() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "getCurrentDownloadQueue");
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(DownloadManager.getInstance().getCurrentDownloadQueue()).getAsJsonArray();
        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
        jsonObject.put("CompletedQueue", jsonArray1);
        DownloadManager.getInstance().getCompletedQueueItems().clear();
        return jsonObject.toString();
    }

    public void removeFileFromStorage(Context context, String id) {
        File file = new File(DownloadUtils.getContentPath(context) + File.separator + id + ".app");

        if (file.exists()) {
            file.delete();
        }
        file = new File(DownloadUtils.getQuestionAttachmentPath(context) + File.separator + id + ".app");
        if (file.exists()) {
            file.delete();
        }
    }

    public String setSettings(ArrayList<SettingsVO> downloadSettingsList, Context context) throws JSONException {
        Log.v("DownloadPlugin", "setsettings");
        SharedPreferences sharedPreferences = context.getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DownloadConstants.PREF_ACESSS, downloadSettingsList.get(0).getAccess());
        editor.putString(DownloadConstants.PREF_VERSION_TYPE, downloadSettingsList.get(0).getVersionType());
        editor.putBoolean(DownloadConstants.PREF_BACKGROUND_DOWNLOAD, downloadSettingsList.get(0).isBackgroundDownload());
        editor.putBoolean(DownloadConstants.PREF_REMEMBER_ALL_BOOKS, downloadSettingsList.get(0).isRememberForAllBooks());
        editor.putBoolean(DownloadConstants.PREF_WIFI, downloadSettingsList.get(0).isUseWifi());
        editor.commit();
        Log.v("DownloadPlugin", "" + downloadSettingsList.get(0).isUseWifi());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.GLOBAL_SETTINGS);
        jsonObject.put("stored", true);
        return jsonObject.toString();
    }

    public String removeAll(Context context) throws JSONException {
        DBHelper.getInstance(context).deleteAllDataFromDatabase();
        DownloadUtils.removeAllFilesInDir(context);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.REMOVETOTALCONTENT);
        return jsonObject.toString();
    }

    public String removeOnlyVideos(Context context) throws JSONException {
        ArrayList<DownloadRequest> drqs = DBHelper.getInstance(context).getDownloadedArraylist();
        if (drqs != null && drqs.size() > 0) {
            removeDownloadItemsList(context, drqs, types[1]);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.REMOVE_ONLY_VIDEOS);
        return jsonObject.toString();
    }

    public String removeFromDR(Context context, String nodeId, int type) throws JSONException {
        DownloadRequest downloadRequest = DBHelper.getInstance(context).getDownloadRequestFromNodeId(nodeId);
        if (downloadRequest != null) {
            ArrayList<DownloadRequest> downloadRequests = new ArrayList<DownloadRequest>();
            downloadRequests.add(downloadRequest);
            removeDownloadItemsList(context, downloadRequests, types[type]);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.REMOVE_FROM_DR);
        return jsonObject.toString();
    }

    public boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public double getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSizeGb(availableBlocks * blockSize);
    }

    public double getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return formatSizeGb(totalBlocks * blockSize);
    }

    public double getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSizeGb(availableBlocks * blockSize);
        } else {
            return 0;
        }
    }

    public double getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSizeGb(totalBlocks * blockSize);
        } else {
            return 0;
        }
    }

    public double formatSize(long size) {
        String suffix = null;
        double d = (double) size;
        if (d >= 1024) {
            suffix = "KB";
            d /= 1024;
            if (d >= 1024) {
                suffix = "MB";
                d /= 1024;
                if (d >= 1024) {
                    suffix = "GB";
                    d /= 1024;
                }
            }
        }
        double newKB = Math.round(d * 100.0) / 100.0;
        return newKB;
    }

    public double formatSizeGb(long size) {
        String suffix = null;
        double d = (double) size;
        d = d / (1024 * 1024 * 1024);
        double newKB = Math.round(d * 100.0) / 100.0;
        return newKB;
    }

    public String formatSizeWithString(long size) {
        if (size == 0) {
            return "0KB";
        } else {
            String suffix = " Bytes ";
            double d = (double) size;
            if (d >= 1024) {
                suffix = "KB";
                d /= 1024;
                if (d >= 1024) {
                    suffix = "MB";
                    d /= 1024;
                    if (d >= 1024) {
                        suffix = "GB";
                        d /= 1024;
                    }
                }
            }
            double newKB = Math.round(d * 100.0) / 100.0;
            return newKB + suffix;
        }

    }

    public ArrayList<DownloadRequest> getCompletedQueueItems() {
        return _currentQueue;
    }

    public ArrayList<DownloadRequest> getIncompleteQueue() {
        return _incompleteQueue;
    }

    public String createDatabaseOperation(ArrayList<DownloadRequest> dcwnloadRequests, Context context) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (DownloadRequest downloadRequest : dcwnloadRequests) {
            String json = new Gson().toJson(downloadRequest);
            if (!DBHelper.getInstance(context).isNodeIdExist(downloadRequest.getNodeId())) {
                Log.v("cordova", "===" + "insert");
                DBHelper.getInstance(context).insertDataIntoDatabase(downloadRequest.getNodeId(), json, downloadRequest.getNodeStatus());
            } else {
                Log.v("cordova", "===" + "update");
                DBHelper.getInstance(context).updateDownloadDRQ(downloadRequest.getNodeId(), json, downloadRequest.getNodeStatus());
            }
        }
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(dcwnloadRequests).getAsJsonArray();
        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
        jsonObject.put("action", "createDatabaseOperation");
        jsonObject.put("download", jsonArray1);
        return jsonObject.toString();
    }

    public String fetchDownloadHistory(ArrayList<DownloadRequest> downloadStatusVOs, Context context) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        ArrayList<DownloadRequest> downloadRequests = DBHelper.getInstance(context).getDownloadedArraylist();
        if (downloadRequests != null && downloadStatusVOs != null) {
            for (DownloadRequest downloadRequest : downloadRequests) {
                if (downloadStatusVOs.contains(downloadRequest)) {
                    int index = downloadStatusVOs.indexOf(downloadRequest);
                    downloadStatusVOs.set(index, downloadRequest);
                    //  downloadStatusVOs.get(index).setNodeStatus(downloadRequest.getNodeStatus());
                }

            }
        }
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(downloadStatusVOs).getAsJsonArray();
        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
        jsonObject.put("action", "fetchDownloadHistory");
        jsonObject.put("completedDRQ", jsonArray1);
        return jsonObject.toString();
    }

    public String fetchDownloadState(Context context) throws JSONException {

        int currentDownloadState = DownloadUtils.getCurrentDownloadState(context);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.GET_DOWNLOAD_STATE);
        jsonObject.put("state", currentDownloadState);
        return jsonObject.toString();
    }

    public String removeFileFromDB(ArrayList<DownloadRequest> dcwnloadRequests, Context context) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (DownloadRequest downloadRequest : dcwnloadRequests) {
            if (DBHelper.getInstance(context).isNodeIdExist(downloadRequest.getNodeId())) {
                Log.v("DownloadPlugin", "===" + "node updated");
                DBHelper.getInstance(context).deleteFromDatabase(downloadRequest.getNodeId());
            }

        }
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(dcwnloadRequests).getAsJsonArray();
        JSONArray jsonArray1 = new JSONArray(jsonArray.toString());
        jsonObject.put("action", "removeFileFromDB");
        jsonObject.put("download", jsonArray1);
        return jsonObject.toString();
    }

    public void removeDownloadedDR(Context context, String drId ) throws MalformedURLException {
        DownloadRequest downloadRequest = DBHelper.getInstance(context).getDownloadRequestFromNodeId(drId);
        if(downloadRequest == null) {
            return;
        }

        URL url = null;
        String path = null;
        File file = null;

        ArrayList<DownloadItemVO> downloadItemVOs = downloadRequest.getDownloadItemVOs();
        if(downloadItemVOs != null && downloadItemVOs.size() > 0) {
            for(DownloadItemVO itemVO : downloadItemVOs) {
                url = new URL(itemVO.getUrl());
                path = DownloadUtils.getAppDir(context) + url.getPath();
                file = new File(path);
                if(file.exists()) {
                    file.delete();
                }
            }
        }

        if (DBHelper.getInstance(context).isNodeIdExist(drId)) {
            DBHelper.getInstance(context).deleteFromDatabase(downloadRequest.getNodeId());
        }
    }

    public ArrayList<DownloadProgressListener> getProgressListeners() {
        return _progressListeners;
    }

    public String loadStoredFile(Context context) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", DownloadConstants.LOAD_STORED_FILE);
        String storedQueueJson = getDownloadRequestsFromAlreadyStoredFile(context);
        ArrayList<DownloadRequest> downloadRequests = new Gson().fromJson(storedQueueJson, new TypeToken<List<DownloadRequest>>() {
        }.getType());
        if (downloadRequests != null) {
            DownloadManager.getInstance().getDownloadRequestQueueItems().addAll(downloadRequests);
        }

        return jsonObject.toString();
    }

    public String getNetworkState(Context context) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "getNetworkState");
        jsonObject.put("networkstate", DownloadUtils.isWifiConnected(context));

        return jsonObject.toString();
    }

    private void storeDownloadRequestQueueIntoFile(Context context, ArrayList<DownloadRequest> downloadRequestItems) {
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(downloadRequestItems).getAsJsonArray();
        String json = jsonArray.toString();
        Log.v("DownloadPlugin", "===" + "storing json" + json);
        try {
//            String dirPath = DownloadUtils.getContentPath(context) + File.separator + "downloadqueue";        ** suresh **
            String dirPath = DownloadUtils.getAppDir(context) + File.separator + "downloadqueue";
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String newFile = dirPath + File.separator + "downloadqueue.txt";
            File file = new File(newFile);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(json);
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getDownloadRequestsFromAlreadyStoredFile(Context context) {
        StringBuffer buffer = new StringBuffer();
        try {
//            File dirPath = new File(DownloadUtils.getContentPath(context) + File.separator + "downloadqueue" + File.separator + "downloadqueue.txt");       ** suresh **
            File dirPath = new File(DownloadUtils.getAppDir(context) + File.separator + "downloadqueue" + File.separator + "downloadqueue.txt");

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

    private void removeDownloadItemsList(Context context, ArrayList<DownloadRequest> drqs, String type) {

        if (type.equalsIgnoreCase("All")) {
            DownloadRequest drq = drqs.get(0);
            for (DownloadItemVO downloadItem : drq.getDownloadItemVOs()) {
                DownloadManager.getInstance().removeFileFromStorage(context, downloadItem.getId());
            }

            DBHelper.getInstance(context).deleteFromDatabase(drq.getNodeId());
        } else {
            for (DownloadRequest downloadRequest : drqs) {
                int count = 0;
                if (downloadRequest.getDownloadItemVOs() != null && downloadRequest.getDownloadItemVOs().size() > 0) {
                    Iterator<DownloadItemVO> downloadItemIterator = downloadRequest.getDownloadItemVOs().iterator();
                    while (downloadItemIterator.hasNext()) {
                        DownloadItemVO downloadItemVO = downloadItemIterator.next();
                        if (downloadItemVO.getFileType().equalsIgnoreCase(type)) {
                            count++;
                            DownloadManager.getInstance().removeFileFromStorage(context, downloadItemVO.getId());
                            downloadItemIterator.remove();
                        }
                    }
                    if (count > 0) {
                        downloadRequest.setVersionType(1);
                        String json = new Gson().toJson(downloadRequest);
                        DBHelper.getInstance(context).updateDownloadDRQ(downloadRequest.getNodeId(), json, downloadRequest.getNodeStatus());
                    }
                }
            }
        }

    }


}
