package com.nexttools.activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nexteducation.downloadmanager.DownloadItemVO;
import com.nexteducation.downloadmanager.DownloadManager;
import com.nexteducation.downloadmanager.DownloadProgressListener;
import com.nexteducation.downloadmanager.DownloadRequest;
import com.nexttools.Controller;
import com.nexttools.R;
import com.nexttools.constants.NextToolConstants;
import com.nexttools.interfaces.DownloadListerner;
import com.nexttools.interfaces.OnParseListener;
import com.nexttools.storing.SharedpreferenceUtils;


import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.chromium.base.ThreadUtils.runOnUiThread;

/**
 * Created by next on 20/5/17.
 */
public class DownloadFile implements DownloadProgressListener, OnParseListener {
    Context mContext;
    String mModuleURL, zipPath, unzipPath;

   /* private static final String BASE_URL = "http://192.168.10.14/download/nexttools/mobile/NextToolsModuleBuilds/";*/
    public static final String TAG = "DownloadFile";
    String dirforZiptargetlocation;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    long nodeIDFromShared;
    ArrayList<DownloadRequest> arrayList = new ArrayList<DownloadRequest>();//for DR
    ArrayList<DownloadItemVO> itemlist = new ArrayList<>();//arraylist for DI
    File mFile;
    boolean isOnce = false;
    DownloadListerner downloadCompleted;



    public DownloadFile(Context mContext) {
        this.mContext = mContext;

      //  this.downloadCompletedListerner =downloadCompletedListerner;
    }



        public void downloadModuleFiels(String urltodownload1, String zippathloc, String unZipPathloc,DownloadListerner downloadCompleted) {
        this.downloadCompleted=downloadCompleted;
        mModuleURL = urltodownload1;
        zipPath = zippathloc;
        unzipPath = unZipPathloc;

        ArrayList<String> urlList = new ArrayList<>();
        urlList.add("abacus");
        urlList.add("howtodraw");
        urlList.add("shape_sorter");
        urlList.add("chemicalequation");
        urlList.add("logarithms");
        urlList.add("solarsystem");
        urlList.add("clock");
        urlList.add("multiplication_table");
        urlList.add("unit_converter");
        urlList.add("dictionary");
        urlList.add("periodic_table");
        urlList.add("worldtime");
        urlList.add("area_explorer");
        urlList.add("imaps");
        urlList.add("nextmaps");


        DownloadRequest request = new DownloadRequest();
        // request.setNodeId(position);
        nodeIDFromShared = SharedpreferenceUtils.getInstance(mContext).getNodeId();
        nodeIDFromShared++;
        Log.i("id", "downloadModuleFiels: " + nodeIDFromShared);
        String valueofnodeId = String.valueOf(nodeIDFromShared);
        request.setNodeId(valueofnodeId);


        DownloadItemVO downloadItem = new DownloadItemVO(valueofnodeId, "221MB.zip", "new");
        SharedpreferenceUtils.getInstance(mContext).storeNodeId(nodeIDFromShared);
        //checking url
        if (urlList.contains(mModuleURL)) {
            Log.i("DownloadModule", "onCreate: start diownload zip file......" + mModuleURL);
            Log.i("DownloadModule", "onCreate: " + NextToolConstants.BASE_URL+ mModuleURL + ".zip");
            downloadItem.setUrl(NextToolConstants.BASE_URL + mModuleURL + ".zip");
            String passURLtoGetContentLength = NextToolConstants.BASE_URL + mModuleURL + ".zip";
            //downloadItem.setUrl(BASE_URL);
            // DownloadItemVO vo2 = new DownloadItemVO("2",".zip","runni");


            itemlist.add(downloadItem);

            request.setDownloadItemVOs(itemlist);
            arrayList.add(request);



            dirforZiptargetlocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            //getting the file path of zipfile which is downloaded already ,were it is taken for only to check the content length of file i.e downloaded one.
            mFile = new File(zipPath);
            //getting the file path of unzip path of downloaded one,checks for the existence.
            File mUnZipFile = new File(unzipPath+mModuleURL);
            if (mUnZipFile.exists()) {
                Controller controller = new Controller(mContext, passURLtoGetContentLength, this);
                controller.execute();

            } else {
                DownloadManager manager = DownloadManager.createInstance(mContext, dirforZiptargetlocation, "http://192.168.10.14");
                // DownloadManager manager = DownloadManager.createInstance(this,dir,"http://www.androidbegin.com" );
                try {
                    //adding into queue
                    manager.addToQueue(this, arrayList, mContext);
                    //download
                    manager.startDownload();
                    Log.i(TAG, "downloadModuleFiels: downloading method is called");
                    mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new NotificationCompat.Builder(mContext);
                    mBuilder.setContentText("Downloading...")
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.download);


                    Log.i("DownloadModule", "onCreate: " + "addedtoqueue");
                } catch (JSONException e) {
                    Log.e("DownloadModule", "onCreate: " + e.getMessage());
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onProgressUpdate(String drId, String diId, long downloadedDiLength, long totalDiLength) {
        Log.d("DownloadModule", "onProgressUpdate() called with: " + "drId = [" + drId + "], diId = [" + diId + "], downloadedDiLength = [" + downloadedDiLength + "], totalDiLength = [" + totalDiLength + "]");
        //   double percentageValue = (downloadedDiLength/totalDiLength)*100;
       try {
           int per = (int) ((downloadedDiLength * 100) / totalDiLength);
           int totalfileLenght = (int) totalDiLength;
           int downLoadDi = (int) downloadedDiLength;
           int total = (int) (long) totalDiLength;

           Log.i(TAG, "onProgressUpdate: " + total);


           Log.i("percentagediaplay", "onProgressUpdate: " + per + "%");
           Log.i(TAG, "onProgressUpdate: Calling progress Upadate");
           // mBuilder.setContentText("Downloading......"+downLoadDi+"%" +"of toatal "+totalfileLenght+"%......"+per);
           mBuilder.setContentText("Downloading......" + per + "%");



           mNotifyManager.notify((int) (nodeIDFromShared), mBuilder.build());
           // mProgressBar.setProgress((int) percentageValue);
           /// Log.i(TAG, "onProgressUpdate: "+percentageValue);
           if (!isOnce) {
             //  mProgressBar.setMax((int) (totalfileLenght));

               isOnce = true;
           }


           downloadCompleted.progressListerner(per);

           if (per == 100) {
               downloadCompleted.downloadCompletedListerner(true);
               mBuilder.setContentText("Downloaded Successfully....");
               mNotifyManager.notify((int) (nodeIDFromShared), mBuilder.build());
               Log.i(TAG, "onProgressUpdate: " + "uuuuuunzip");
           }

           if(per==100){
               try {
                   unzip(zipPath, unzipPath);
                   File f = new File(zipPath);
                   if (f.exists()) {
                       f.delete();
                   }
                   callWebView();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }catch (ArithmeticException ex){
        ex.printStackTrace();
    }


    }


    @Override
    public void onDownloadCompleted(String drId, String diId, long downloadedDiLength, long totalDiLength)
    {
        Log.d("DownloadModule", "onDownloadCompleted() called with: " + "drId = [" + drId + "], diId = [" + diId + "], " +
                "downloadedDiLength = [" + downloadedDiLength + "], totalDiLength = [" + totalDiLength + "]");

    }

    @Override
    public void onDownloadFailed(String drId, String diId)
    {
        Log.d("DownloadModule", "onDownloadFailed() called with: " + "drId = [" + drId + "], diId = [" + diId + "]");
    }
    //unzip method
    public void unzip(String zipFile, String location) throws IOException {
        Log.i(TAG, "unzip: " + "unzipcalld");
        File file = new File(zipFile);
        if (file.exists())
        {

            int size;
            int BUFFER_SIZE = 1024 * 1024;
            byte[] buffer = new byte[BUFFER_SIZE];

            try {
                if (!location.endsWith("/")) {
                    location += "/";
                }
                File f = new File(location);
                if (!f.isDirectory()) {
                    f.mkdirs();
                }
                ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
                try {
                    ZipEntry ze = null;
                    while ((ze = zin.getNextEntry()) != null) {
                        String path = location + ze.getName();
                        File unzipFile = new File(path);

                        if (ze.isDirectory()) {
                            if (!unzipFile.isDirectory()) {
                                unzipFile.mkdirs();
                            }
                        } else {
                            // check for and create parent directories if they don't exist
                            File parentDir = unzipFile.getParentFile();
                            if (null != parentDir) {
                                if (!parentDir.isDirectory()) {
                                    parentDir.mkdirs();
                                }
                            }

                            // unzip the file
                            FileOutputStream out = new FileOutputStream(unzipFile, false);
                            BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                            try {
                                while ((size = zin.read(buffer, 0, BUFFER_SIZE)) != -1) {
                                    fout.write(buffer, 0, size);
                                    if (path.contains("index.html")) {

                                        SharedpreferenceUtils.getInstance(mContext).storingModuleLocation(mModuleURL, path);
                                    }
                                }

                                zin.closeEntry();
                            } finally {
                                fout.flush();
                                fout.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("DownloadModule", "Unzip exception", e);
                } finally {
                    zin.close();
                }

            } catch (Exception e) {
                Log.e("DownloadModule", "Unzip exception", e);
            }
        }
    }

  //Checking the length of the zip file with the content length of the server file , which decides that weather to download or not
    @Override
    public void onSuccess(Object object) {
        long fileSize = (long) object;
        if (mFile.length() == fileSize) {
            //Call WebView
            callWebView();
            Log.i(TAG, "onSuccess: -------------------->" + fileSize + mFile.length());
            System.out.print("appppppppp" + fileSize + mFile.length());
        } else {

            downloadModuleFiels(mModuleURL, zipPath, unzipPath,downloadCompleted);

        }
    }


      public void callWebView() {
        Intent in = new Intent(mContext, WebViewActivity.class);
        in.putExtra("UrlModule", mModuleURL);
        mContext.startActivity(in);
    }


    @Override
    public void onException() {

        
    }

}

