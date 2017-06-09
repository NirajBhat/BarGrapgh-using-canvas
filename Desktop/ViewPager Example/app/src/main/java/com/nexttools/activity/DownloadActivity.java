package com.nexttools.activity;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.nexteducation.downloadmanager.DownloadItemVO;
import com.nexteducation.downloadmanager.DownloadManager;
import com.nexteducation.downloadmanager.DownloadProgressListener;
import com.nexteducation.downloadmanager.DownloadRequest;
import com.nexttools.Controller;
import com.nexttools.R;
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

public class DownloadActivity extends AppCompatActivity implements DownloadProgressListener,OnParseListener {

    private  static  final  String TAG="Mainactivity";
     //private static final String  BASE_URL = "http://www.androidbegin.com/tutorial/jsonparsetutorial.txt";
    //base url to download
    private static final String  BASE_URL = "http://192.168.10.14/download/nexttools/weekly/demo/module_builds/";
    //private static final String URI = "http://www.androidbegin.com/tutorial/jsonparsetutorial.txt";
    String directoryForGettingFileLocation, dirtargetlocation;
    String urltodownload;
    File file;

    ArrayList<DownloadRequest> arrayList = new ArrayList<DownloadRequest>();//for DR
    ArrayList<DownloadItemVO> itemlist = new ArrayList<>();//arraylist for DI
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        long nodeIDFromShared=SharedpreferenceUtils.getInstance(this).getNodeId();
        nodeIDFromShared++;

      String valueofnodeId=  String.valueOf(nodeIDFromShared);

         //taking module url and position
        urltodownload= getIntent().getStringExtra("mModuleUrl");
        String position = getIntent().getStringExtra("position");

        ArrayList<String> urlList= new ArrayList<>();
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



        DownloadRequest request = new DownloadRequest();
       // request.setNodeId(position);

        request.setNodeId(valueofnodeId);


        DownloadItemVO downloadItem = new DownloadItemVO(valueofnodeId,"221MB.zip","new");
        SharedpreferenceUtils.getInstance(this).storeNodeId(nodeIDFromShared);
        //checking url
        if (urlList.contains(urltodownload)) {
            Log.i(TAG, "onCreate: start diownload zip file......" + urltodownload);
            Log.i(TAG, "onCreate: " + BASE_URL + urltodownload + ".zip");
            downloadItem.setUrl(BASE_URL + urltodownload + ".zip");
            String passURLtoGetContentLength = BASE_URL + urltodownload + ".zip";
            //downloadItem.setUrl(BASE_URL);
            // DownloadItemVO vo2 = new DownloadItemVO("2",".zip","runni");


            // arrayList.add(vo2);
            itemlist.add(downloadItem);
            request.setDownloadItemVOs(itemlist);
            arrayList.add(request);

            //path to download zip file
            //directoryForGettingFileLocation= String.valueOf(getExternalFilesDir(null));
            directoryForGettingFileLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            file = new File(directoryForGettingFileLocation, passURLtoGetContentLength);
            if (file.exists()) {
                Controller controller = new Controller(DownloadActivity.this, passURLtoGetContentLength, this);
                controller.execute();


            } else {
                callDownloadtask();
            }

        }
    }

    @Override
    public void onProgressUpdate(String drId, String diId, long downloadedDiLength, long totalDiLength)
    {
        Log.d(TAG, "onProgressUpdate() called with: " + "drId = [" + drId + "], diId = [" + diId + "], downloadedDiLength = [" + downloadedDiLength + "], totalDiLength = [" + totalDiLength + "]");
    }

    @Override
    public void onDownloadCompleted(String drId, String diId, long downloadedDiLength, long totalDiLength) {
        Log.d(TAG, "onDownloadCompleted() called with: " + "drId = [" + drId + "], diId = [" + diId + "], downloadedDiLength = [" + downloadedDiLength + "], totalDiLength = [" + totalDiLength + "]");
        try {
            //path to unzip file
            dirtargetlocation= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/Unzip/"+urltodownload;
            File file = new File(dirtargetlocation);
            if (!file.exists()) {
                file.mkdirs();
            }
            unzip(   directoryForGettingFileLocation +"/download/nexttools/weekly/demo/module_builds/"+urltodownload+".zip",dirtargetlocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   //An Interface methods to get the data (Content :Length from the controller class which is an Async Task extended)
    @Override
    public void onSuccess(Object object) {
        long fileSize = (long) object;
        if(file.length() == fileSize){
            Log.i(TAG, "onSuccess: " +fileSize);
        }else {
            callDownloadtask();
        }
    }

    @Override
    public void onException() {

    }

    //Method that holds the download task , i.e which calls the startDownload method from the Download Manager .
    public void callDownloadtask(){
        DownloadManager manager = DownloadManager.createInstance(this, directoryForGettingFileLocation, "http://192.168.10.14");
        // DownloadManager manager = DownloadManager.createInstance(this,directoryForGettingFileLocation,"http://www.androidbegin.com" );
        try {
            //adding into queue
            manager.addToQueue(this, arrayList, this);
            //download
            manager.startDownload();
            Log.i(TAG, "onCreate: " + "addedtoqueue");
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
            e.printStackTrace();
        }

    }



    @Override
    public void onDownloadFailed(String drId, String diId) {
        Log.d(TAG, "onDownloadFailed() called with: " + "drId = [" + drId + "], diId = [" + diId + "]");
    }


    //unzip method
    public  void unzip(String zipFile, String location) throws IOException {
        File file = new File(zipFile);

        if(file.exists()) {


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
                                }

                                zin.closeEntry();
                            } finally {
                                fout.flush();
                                fout.close();
                            }
                        }
                    }
                } finally {
                    zin.close();
                }

            } catch (Exception e) {
                Log.e(TAG, "Unzip exception", e);
            }
        }
    }
 }




