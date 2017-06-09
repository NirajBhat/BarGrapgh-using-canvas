package com.nexteducation.downloadmanager;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by suresh on 2/10/2016.
 */
public class DownloadService extends Service {
    private static DownloadService _instance;
    //private final String BASE_URL="http://182.72.188.40/download/";
    //private final String HOST_URL = "http://182.72.188.40/mobile/content/";
    private SharedPreferences _sharedPreferences;
    private int _itemCount = 0;
    private int _connectionRetries = 0;
    private NotificationManager _notificationManager;
    private Notification.Builder _builder;
    private Notification notification;
    private int notificationId = 11;
    private int percentage = 0;
    private PendingIntent contentIntent;


    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Inside onCreate, " + DownloadService.class.getName());
        _instance = this;
        _sharedPreferences = getSharedPreferences(DownloadConstants.PREFERENCE_DOWNLOAD, Context.MODE_PRIVATE);
        Intent brodcast = new Intent(DownloadConstants.SERVICE_STARTED);
        sendBroadcast(brodcast);
//        buildNotification();

    }

    private void buildNotification() {
        _notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        _builder = new Notification.Builder(DownloadService.this);
        Intent notificationIntent = new Intent(this, getClassName());
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("completeNotification", true);
        contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Inside onStartCommand, " + DownloadService.class.getName());
//        startDownload();
        showFakeNotification();
        return START_STICKY;
    }

    private void showFakeNotification() {
        _builder = new Notification.Builder(DownloadService.this);
        notification = _builder.build();
        startForeground(0, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("===========>"+"ondestroy");
        _instance = null;

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        System.out.println("inside onTaskRemoved, " + DownloadService.class.getName());

        Log.v("cordova", "app killed" + DownloadUtils.getCurrentDownloadState(DownloadService.this) + DownloadManager.getInstance().getDownloadRequestQueueItems().size());
        DownloadUtils.storeApplicationState(DownloadService.this, false);
        storeDownloadRequestQueueIntoFile(DownloadManager.getInstance().getDownloadRequestQueueItems());
        DownloadUtils.storeTotalQueueInPreference(DownloadService.this);
//        _notificationManager.cancel(notificationId);
        DBHelper.getInstance(this).close();
        super.onTaskRemoved(rootIntent);
    }


    public static DownloadService getInstance() {
        return _instance;
    }


    public void startDownload() {
        Log.v("cordova", "service calling");
//        createProgressNotification();
        startDownloadRequestQueue();

    }

    private void startDownloadRequestQueue() {
        int currentDownloadState = DownloadUtils.getCurrentDownloadState(DownloadService.this);
        Log.v("cordova", "inside downloadservice");
        if ((currentDownloadState == DownloadState.INPROGRESS.getDownloadCode())) {

            ArrayList<DownloadRequest> drList = DownloadManager.getInstance().getDownloadRequestQueueItems();
            DownloadRequest dr = drList.get(0);
            ArrayList<DownloadItemVO> diList = dr.getDownloadItemVOs();

            if (diList.size() > _itemCount) {
                Log.v("cordova", "inside downloadtask");
                new DownloadTask(dr, diList.get(_itemCount)).execute();
            }
        } else {
            storeDownloadRequestQueueIntoFile(DownloadManager.getInstance().getDownloadRequestQueueItems());
        }
    }

    private class DownloadTask extends AsyncTask<Void, Integer, Integer> {
        private DownloadRequest _downloadRequest;
        private DownloadItemVO _downloadItem;

        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        HttpURLConnection httpURLConnection = null;

        int downloaded = 0;
        int lengthOfFile = 0;

        private static final String UNCONFIRMED = ".unconfirmed.download";

        public DownloadTask(DownloadRequest downloadRequest, DownloadItemVO downloadItemVO) {
            _downloadRequest = downloadRequest;
            _downloadItem = downloadItemVO;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("cordova", "inside preexecute");
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int status = 0;

            // get AppBasePath, e.g., /storage/emulated/0/Android/data/com.next.product/NextEducation/
            String appBasePath = DownloadUtils.getAppDir(DownloadService.this);

            // create appBaseDir.
            File appBaseDir = new File(appBasePath);
            appBaseDir.mkdirs();

            // Form the URL to download
            String downloadUrl_String = _downloadItem.getUrl();
            // Append sessionId if exists, (this acts as compatible code for cordova NextBooks project)
            final String sessionId = DownloadUtils.getSessionId(DownloadService.this);
            if(sessionId != null && !sessionId.isEmpty()) {
                downloadUrl_String = downloadUrl_String + "?ICONCEPTSESSIONID=" + sessionId;
            }

            File storageFile = null;
            URL downloadUrl = null;
            String fileStoragePath = null;

            try {
                downloadUrl = new URL(downloadUrl_String);
                fileStoragePath = appBasePath + downloadUrl.getPath();
                storageFile = new File(fileStoragePath);
                System.out.println("inside doInBackground, file storage path = " + fileStoragePath);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                // If URL is not proper, store the file in appBasePath with id as name
                fileStoragePath = appBasePath;
                storageFile = new File(fileStoragePath + File.separator + _downloadItem.getId() + ".app");
                System.out.println("inside doInBackground, URL Malformed, hence file storage path = " + fileStoragePath);
            }
            // create missed directories.
            storageFile.getParentFile().mkdirs();

            // create partial download path to handle range request
            StringBuilder partialDownloadedFilePath = new StringBuilder(storageFile.getParent())
                    .append("/")
                    .append(storageFile.getName())
                    .append(UNCONFIRMED);

            File partialDownloadedFile = new File(partialDownloadedFilePath.toString());

            System.out.println("inside doInBackground, storageFile = " + storageFile.getAbsolutePath());

            try {
                System.out.println("inside doInBackground, Requesting the file with URL = " + downloadUrl_String);
                downloadUrl = new URL(downloadUrl_String);
                httpURLConnection = (HttpURLConnection) downloadUrl.openConnection();
                httpURLConnection.setConnectTimeout(1000000000);
                httpURLConnection.setReadTimeout(1000000000);

                long existingFileLength = 0;
                if(partialDownloadedFile.exists()) {
                    // get the downloaded file length
                    existingFileLength = partialDownloadedFile.length();
                    httpURLConnection.setRequestProperty("Range", "bytes=" + existingFileLength + "-");
                }

                httpURLConnection.connect();

                // calculate total length of file by getting contentLength and add that contentLength
                // to already downloaded file length.
                lengthOfFile = (int) (existingFileLength + httpURLConnection.getContentLength());

               // _downloadItem.totalSize = lengthOfFile;
                if (storageFile.exists() && httpURLConnection.getContentLength() == storageFile.length() && !_downloadItem.isSignMismatch()) {
                    status = 200;
                    downloaded = lengthOfFile;
                    sendProgress(DownloadState.COMPLETED, _downloadRequest, _downloadItem, downloaded, lengthOfFile);
                    /*broadcastMessage(DownloadManager.ACTION_ITEM_COMPLETED);
                    broadcastMessage(DownloadManager.ACTION_);
                    broadcastMessage(DownloadManager.ACTION_ITEM_DOWNLOADING);*/
                    closeConnection();
                    Log.v("cordova", "inside cordova status");
                }

                else {
                    if (storageFile.exists()) {
                        DownloadManager.getInstance().removeFileFromStorage(DownloadService.this, _downloadItem.getId());
                        storageFile.createNewFile();
                        Log.v("cordova", "inside file remove");
                    }
                    status = httpURLConnection.getResponseCode();
                    inputStream = httpURLConnection.getInputStream();

                    // if the status is 200 and partial download file exists, then delete the partial download file
                    if(status == 200 && partialDownloadedFile.exists()) {
                        deleteFileFromStorage(partialDownloadedFile);
                    }

                    fileOutputStream = new FileOutputStream(partialDownloadedFile, true);

                    byte[] buffer = new byte[1024 * 10];
                    int bufferLength = 0;
                    int n = 5;
                    int prev=0;
//                    publishProgress(percentage);
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        boolean wifiSetting = DownloadUtils.getWifiSettingsState(DownloadService.this);
                        //Log.v("cordova", "wifisetting"+wifiSetting);
                        if (!((DownloadUtils.isNetworkAvailable(DownloadService.this) && !wifiSetting) || (wifiSetting && DownloadUtils.isWifiConnected(DownloadService.this)))) {
                            DownloadUtils.storeDownloadState(DownloadService.this, DownloadState.NETWORKPAUSE.getDownloadCode());
                        }

                        int currentDownloadState = DownloadUtils.getCurrentDownloadState(DownloadService.this);
                        if (currentDownloadState == DownloadState.NETWORKPAUSE.getDownloadCode() || currentDownloadState == DownloadState.PAUSE.getDownloadCode()) {
                            status = 100;
                            closeConnection();
                            break;

                        } else {
                            fileOutputStream.write(buffer, 0, bufferLength);
                            downloaded = downloaded + bufferLength;

                            // publishing msg for every 1%
                            int percentage=(downloaded*100)/lengthOfFile;
                            if(percentage % 1 == 0 && percentage != prev) {
                                prev = percentage;
                                publishProgress(DownloadState.INPROGRESS.getDownloadCode());
                                System.out.println("percentage"+percentage+"======="+downloaded);
                            }
                        }
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    closeConnection();

                    if(bufferLength < 0) {
                        _downloadItem.downloadedSize = downloaded;
                        createOriginalFile(storageFile, partialDownloadedFile);
                        deleteFileFromStorage(partialDownloadedFile);
                        percentage += getDownloadedPercentage(_downloadRequest);
                        publishProgress(DownloadState.INPROGRESS.getDownloadCode());
                    }

                    Log.v("cordova", "inside do in background, " + storageFile.getAbsolutePath() + " Downloaded");
                }
            } catch (IOException e) {
                if (!DownloadUtils.isNetworkAvailable(DownloadService.this) && DownloadUtils.getCurrentDownloadState(DownloadService.this) == DownloadState.INPROGRESS.getDownloadCode()) {
                    DownloadUtils.storeDownloadState(DownloadService.this, DownloadState.NETWORKPAUSE.getDownloadCode());

                }
                deleteFileFromStorage(storageFile);
                publishProgress(DownloadState.INPROGRESS.getDownloadCode());
                e.printStackTrace();
                Log.v("cordova", "inside do in background exception" + "responsecode" + status + e.getMessage());
            } finally {
                closeConnection();
            }

            return status;
        }


        private void sendProgress(final DownloadState state, final DownloadRequest downloadRequest, final DownloadItemVO downloadItem, final int downloaded,final int totalLength) {
            DownloadManager dm = DownloadManager.getInstance();
            ArrayList<DownloadProgressListener> progressListeners = null;
            if(dm != null) {
                progressListeners = dm.getProgressListeners();
            }

            if(progressListeners != null && progressListeners.size() > 0) {
                for(DownloadProgressListener listener : progressListeners) {
                    switch (state) {
                        case COMPLETED :
                            listener.onDownloadCompleted(downloadRequest.getNodeId(), downloadItem.getId(), downloaded, totalLength);
                            break;
                        case INPROGRESS :
                            listener.onProgressUpdate(downloadRequest.getNodeId(), downloadItem.getId(), downloaded, totalLength);
                            break;
                        case FAILED :
                            listener.onDownloadFailed(downloadRequest.getNodeId(), downloadItem.getId());
                            break;
                    }
                }
            }

            Intent brodcast = new Intent(DownloadConstants.DOWNLOAD_STATUS);
            brodcast.putExtra(DownloadConstants.DOWNLOAD_STATUS_TYPE, state.name());
            brodcast.putExtra("downloadRequestId", downloadRequest.getNodeId());
            brodcast.putExtra("downloadItemId", downloadItem.getId());
            brodcast.putExtra("downloaded", downloaded);
            brodcast.putExtra("totalLength", totalLength);
            sendBroadcast(brodcast);
        }

        private void closeConnection() {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.out.println("inside finally block of DownloadTask, error msg = " + e.getMessage());
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Update progress

            int index = DownloadManager.getInstance().getTotalQueue().indexOf(_downloadRequest) + 1;
            int value = values[0];
            sendProgress(DownloadState.getEnumObj(value), _downloadRequest, _downloadItem, downloaded, lengthOfFile);
            /*String progressText = " Downloading  " + index + " out of " + DownloadManager.getInstance().getTotalQueue().size() + " " + "chapters";
            *//*percentage = percentage + 5;
            Log.v("cordova", percentage + "======" + values[0] + "=======" + _downloadRequest.getDownloadItemVOs().size());
            _builder.setProgress(100, percentage / _downloadRequest.getDownloadItemVOs().size(), false);*//*
            _builder.setProgress(100, values[0], false);
            _builder.setContentText(progressText);
            _notificationManager.notify(notificationId, _builder.build());
            super.onProgressUpdate(values);*/
        }

        @Override
        protected void onPostExecute(Integer status) {
            super.onPostExecute(status);

            if (DownloadUtils.getCurrentDownloadState(DownloadService.this) == DownloadState.PAUSE.getDownloadCode() || DownloadUtils.getCurrentDownloadState(DownloadService.this) == DownloadState.NETWORKPAUSE.getDownloadCode()) {
                storeDownloadRequestQueueIntoFile(DownloadManager.getInstance().getDownloadRequestQueueItems());
//                createPauseNotification();
                percentage = 0;
                return;
            }
            if (status != 200 && status != 100) {
                ++_connectionRetries;
                if (DownloadManager.getInstance().getDownloadRequestQueueItems().size() > 0 && _connectionRetries < 3) {
                    Log.v("cordova", "inside retry");
                    new DownloadTask(_downloadRequest, _downloadItem).execute();
                    return;
                } else {
                    Log.v("cordova", "inside retry completed");
                    _connectionRetries = 0;
                }

            }

            Log.v("cordova", "inside postexecute" + DownloadUtils.getCurrentDownloadState(DownloadService.this));

            boolean wifiSetting = DownloadUtils.getWifiSettingsState(DownloadService.this);

            int downloadItemIndex = _downloadRequest.getDownloadItemVOs().indexOf(_downloadItem);

            if (status == 200) {
                _downloadItem.setStatus("completed");
                _downloadItem.setSignMismatch(false);
            } else {
                _downloadItem.setStatus("pending");
            }

            if (_downloadRequest.getDownloadItemVOs().size() == (downloadItemIndex + 1)) {
                percentage = 0;
                _itemCount = 0;
                _downloadRequest.setCompletedLength(_downloadRequest.getCompletedLength() + 1);
                String statusOfDownload = getDownloadRequestStatus(_downloadRequest);
                _downloadRequest.setNodeStatus(statusOfDownload);
                String json = new Gson().toJson(_downloadRequest);
                if (!DBHelper.getInstance(DownloadService.this).isNodeIdExist(_downloadRequest.getNodeId())) {
                    Log.v("cordova", "insert");
                    DBHelper.getInstance(DownloadService.this).insertDataIntoDatabase(_downloadRequest.getNodeId(), json, statusOfDownload);
                } else {
                    Log.v("cordova", "update");
                    DBHelper.getInstance(DownloadService.this).updateDownloadDRQ(_downloadRequest.getNodeId(), json, statusOfDownload);
                }
                if (_downloadRequest.getNodeStatus().equalsIgnoreCase(DownloadConstants.COMPLETED)) {
                    DownloadManager.getInstance().getCompletedQueueItems().add(_downloadRequest);
                } else {
                    DownloadManager.getInstance().getIncompleteQueue().add(_downloadRequest);
                }

                DownloadManager.getInstance().getDownloadRequestQueueItems().remove(_downloadRequest);

                if (DownloadManager.getInstance().getDownloadRequestQueueItems().size() > 0) {
                    Log.v("cordova", "inside itemssize" + DownloadManager.getInstance().getDownloadRequestQueueItems().size());
                    if ((DownloadUtils.isNetworkAvailable(DownloadService.this) && !wifiSetting) || (wifiSetting && DownloadUtils.isWifiConnected(DownloadService.this))) {
                        if (DownloadUtils.getCurrentDownloadState(DownloadService.this) == DownloadState.INPROGRESS.getDownloadCode()) {
                            Log.v("cordova", "inside startdownload" + DownloadManager.getInstance().getDownloadRequestQueueItems().size());
                            startDownloadRequestQueue();
                        }

                    } else {
                        Log.v("cordova", "networkpause" + DownloadManager.getInstance().getDownloadRequestQueueItems().size());
                        DownloadUtils.storeDownloadState(DownloadService.this, DownloadState.NETWORKPAUSE.getDownloadCode());
                    }
                } else {
                    Log.v("cordova", "inside no queue" + DownloadManager.getInstance().getDownloadRequestQueueItems().size());
                    DownloadUtils.storeDownloadState(DownloadService.this, DownloadState.COMPLETED.getDownloadCode());
//                    File file = new File(DownloadUtils.getContentPath(DownloadService.this) + File.separator + "downloadqueue");      ** suresh **
                    File file = new File(DownloadUtils.getAppDir(DownloadService.this) + File.separator + "downloadqueue");
                    deleteRecursive(file);
                    stopService();

                }

            } else {
                Log.v("cordova", "inside itemssize" + DownloadManager.getInstance().getDownloadRequestQueueItems().size());
                int currentDownloadState = DownloadUtils.getCurrentDownloadState(DownloadService.this);
                if (currentDownloadState == DownloadState.INPROGRESS.getDownloadCode()) {
                    _downloadRequest.setCompletedLength(_downloadRequest.getCompletedLength() + 1);
                    if (DownloadManager.getInstance().getDownloadRequestQueueItems().get(0).getDownloadItemVOs().size() > _itemCount) {
                        if ((DownloadUtils.isNetworkAvailable(DownloadService.this) && !wifiSetting) || (wifiSetting && DownloadUtils.isWifiConnected(DownloadService.this))) {
                            ++_itemCount;
                            new DownloadTask(_downloadRequest, DownloadManager.getInstance().getDownloadRequestQueueItems().get(0).getDownloadItemVOs().get(_itemCount)).execute();
                        } else {
                            _itemCount = 0;
                            Log.v("cordova", "networkpause" + DownloadManager.getInstance().getDownloadRequestQueueItems().size());
                            DownloadUtils.storeDownloadState(DownloadService.this, DownloadState.NETWORKPAUSE.getDownloadCode());
//                            createPauseNotification();
                        }

                    }

                }
            }
            storeDownloadRequestQueueIntoFile(DownloadManager.getInstance().getDownloadRequestQueueItems());



            ArrayList<DownloadProgressListener> listprogrees= DownloadManager.getInstance().getProgressListeners();
            DownloadProgressListener downloadProgressListener = null;
            for (int i = 0; i <listprogrees.size() ; i++) {
                downloadProgressListener=listprogrees.get(i);
            }
           // downloadProgressListener.onDownloadCompleted("","",0,0);

        }

    }

    private int getDownloadedPercentage(DownloadRequest downloadRequest) {
        int percent = 0;
        int totalQueueSize = DownloadManager.getInstance().getTotalQueue().size();
        int eachDr = 100 / totalQueueSize;

        int diList = downloadRequest.getDownloadItemVOs().size();
        percent = eachDr / diList;
        return percent;
    }

    private void createOriginalFile(File storageFile, File partialDownloadedFile) throws IOException {

        if(!partialDownloadedFile.exists()) {
            return;
        }
        FileInputStream fis = new FileInputStream(partialDownloadedFile);
        FileOutputStream fos = new FileOutputStream(storageFile);

        byte[] buffer = new byte[1024];
        int length = 0;
        while((length = fis.read(buffer)) > 0 ) {
            fos.write(buffer, 0, length);
        }
        fos.flush();
        fos.close();
        fis.close();
    }


    private void deleteFileFromStorage(File storageFile) {
        if (storageFile.exists()) {
            storageFile.delete();
        }
    }


    private String getDownloadRequestStatus(DownloadRequest downloadRequest) {
        String status;
        int count = 0;
        for (DownloadItemVO downloadItemVO : downloadRequest.getDownloadItemVOs()) {
            if (downloadItemVO.getStatus().equalsIgnoreCase(DownloadConstants.COMPLETED)) {
                count++;
            }
        }
        /*if (downloadRequest.getDownloadItemVOs().size() == count && downloadRequest.getDownloadSettings().isVideosRequired())
        {
            status = "completed";
        }
        else
        {
            status = "incomplete";
        }*/
        if (downloadRequest.getDownloadItemVOs().size() == count) {
            status = DownloadConstants.COMPLETED;
        } else {
            status = DownloadConstants.INCOMPLETED;
            DownloadManager.getInstance().getTotalQueue().remove(downloadRequest);
            DownloadUtils.storeTotalQueueInPreference(DownloadService.this);
        }


        return status;
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

    private void stopService() {
        Log.v("cordova", "inside stopservice");
//        _notificationManager.cancel(notificationId);
//        createCompleteNotification();
        stopSelf();
    }


    private void storeDownloadRequestQueueIntoFile(ArrayList<DownloadRequest> downloadRequestItems) {
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(downloadRequestItems).getAsJsonArray();
        String json = jsonArray.toString();
        try {
//            String dirPath = DownloadUtils.getContentPath(DownloadService.this) + File.separator + "downloadqueue";       ** suresh **
            String dirPath = DownloadUtils.getAppDir(DownloadService.this) + File.separator + "downloadqueue";
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


    public enum DownloadState {
        INPROGRESS(0, "INPROGRESS"), PAUSE(1, "PAUSE"), COMPLETED(2, "COMPLETED"), NETWORKPAUSE(3, "NETWORKPAUSE"), FAILED(4, "FAILED");

        private final int downloadCode;
        private final String downloadStatus;

        DownloadState(int code, String status) {
            downloadCode = code;
            downloadStatus = status;
        }

        public int getDownloadCode() {
            return downloadCode;
        }

        public static DownloadState getEnumObj(int code)
        {
            if(code == INPROGRESS.downloadCode)
            {
                return INPROGRESS;
            } else if(code == PAUSE.downloadCode) {
                return PAUSE;
            } else if(code == COMPLETED.downloadCode) {
                return COMPLETED;
            } else if(code == NETWORKPAUSE.downloadCode) {
                return NETWORKPAUSE;
            } else if(code == FAILED.downloadCode) {
                return FAILED;
            }
            return FAILED;
        }

        public String getDownloadStatus() {
            return downloadStatus;
        }
    }

    private Class getClassName() {
        /*String StringClassname = getPackageName() + ".MainActivity";

        Class<?> mainactivity = null;

        if (StringClassname != null) {
            try {
                mainactivity = Class.forName(StringClassname);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return mainactivity;*/

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        String className = launchIntent.getComponent().getClassName();
        Class<?> aClass = null;
        try {
            aClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return aClass;
    }

    private void createProgressNotification() {
        int iconId = this.getResources().getIdentifier("icon", "drawable", getPackageName());
        iconId = iconId == 0 ? this.getResources().getIdentifier("ic_launcher", "mipmap", getPackageName()) : iconId;
        int downloadiconId = this.getResources().getIdentifier("download", "drawable", getPackageName());

        Bitmap icon = BitmapFactory.decodeResource(getResources(), iconId);
        _builder = new Notification.Builder(DownloadService.this);
        _builder.setContentTitle(getAppName())
                .setContentText("Download in progress")
                .setLargeIcon(icon)
                .setSmallIcon(downloadiconId)
                .setContentIntent(contentIntent)
                .setOngoing(true);
        notification = _builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        _notificationManager.notify(notificationId, notification);
        startForeground(notificationId, notification);
    }

    private String getAppName() {
        int labelRes = getApplicationInfo().labelRes;
        return labelRes == 0 ? getApplicationInfo().nonLocalizedLabel.toString() : getString(labelRes);
    }

    private void createCompleteNotification() {
        int iconId = this.getResources().getIdentifier("icon", "drawable", getPackageName());
        iconId = iconId == 0 ? this.getResources().getIdentifier("ic_launcher", "mipmap", getPackageName()) : iconId;
        int downloadiconId = this.getResources().getIdentifier("download", "drawable", getPackageName());

        Bitmap icon = BitmapFactory.decodeResource(getResources(), iconId);
        _builder = new Notification.Builder(DownloadService.this);
        _builder.setContentTitle(getAppName())
                .setContentText("Download of " + DownloadManager.getInstance().getTotalQueue().size() + " Chapters completed")
                .setLargeIcon(icon)
                .setSmallIcon(downloadiconId)
                .setContentIntent(contentIntent);
        DownloadManager.getInstance().getTotalQueue().clear();
        DownloadUtils.storeTotalQueueInPreference(DownloadService.this);
        notification = _builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        _notificationManager.notify(notificationId, notification);
    }

    private void createPauseNotification() {
        int iconId = this.getResources().getIdentifier("icon", "drawable", getPackageName());
        iconId = iconId == 0 ? this.getResources().getIdentifier("ic_launcher", "mipmap", getPackageName()) : iconId;
        int downloadiconId = this.getResources().getIdentifier("download", "drawable", getPackageName());

        Bitmap icon = BitmapFactory.decodeResource(getResources(), iconId);
        _builder = new Notification.Builder(DownloadService.this);
        _builder.setContentTitle(getAppName())
                .setContentText("Download Paused")
                .setLargeIcon(icon)
                .setSmallIcon(downloadiconId)
                .setContentIntent(contentIntent);
        notification = _builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        _notificationManager.notify(notificationId, notification);
    }

}
