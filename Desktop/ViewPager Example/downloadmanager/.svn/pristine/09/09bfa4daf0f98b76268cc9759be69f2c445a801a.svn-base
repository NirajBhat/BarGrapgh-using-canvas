package com.nexteducation.downloadmanager;

/*import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;*/

/**
 * Created by suresh on 2/12/2016.
 */
public class DownloadPlugin //extends CordovaPlugin
{
 /*   private Context _context;
    private Class[] noParams = {};
    private String TAG = "DownloadPlugin";
    private Activity _activity;


    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException
    {
        _context = this.cordova.getActivity().getApplicationContext();
        Log.v("cordova", "method"+action);
        Log.v(TAG, "downloadRequestQueue" + args.toString());
        _activity = cordova.getActivity();
        cordova.getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                try
                {
                    Class classname = Class.forName("com.sample.sampledownload.DownloadManager");
                    Method method = classname.getMethod("getInstance", new Class[0]);
                    Object object = method.invoke(classname, new Object[0]);


                    if (action.equalsIgnoreCase(DownloadConstants.ADDTO_QUEUE))
                    {
                        ArrayList<DownloadRequest> downloadRequests = new Gson().fromJson(args.toString(), new TypeToken<List<DownloadRequest>>()
                        {
                        }.getType());
                        Log.v("DownloadPlugin", downloadRequests.toString());
                        Log.v("DownloadPlugin", "addtoqueue");
                        Method methodWithArrayList = classname.getDeclaredMethod(DownloadConstants.ADDTO_QUEUE, ArrayList.class, Context.class);
                        String response = (String) methodWithArrayList.invoke(object, downloadRequests, _context);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.REMOVEALL_FROM_QUEUE))
                    {
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.REMOVEALL_FROM_QUEUE, Context.class);
                        String response = (String) methodWithParams.invoke(object, _context);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.REMOVE_FROM_QUEUE))
                    {
                        ArrayList<DownloadRequest> downloadRequests = new Gson().fromJson(args.toString(), new TypeToken<List<DownloadRequest>>()
                        {
                        }.getType());
                        Log.v("DownloadPlugin", downloadRequests.toString());
                        Method methodWithArrayList = classname.getDeclaredMethod(DownloadConstants.REMOVE_FROM_QUEUE, Context.class, ArrayList.class);
                        String response = (String) methodWithArrayList.invoke(object, _context, downloadRequests);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.FETCH_DRQ))
                    {
                        Method methodWithContext = classname.getDeclaredMethod(DownloadConstants.FETCH_DRQ, Context.class);
                        String response = (String) methodWithContext.invoke(object, _context);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase("getCompletedQueue"))
                    {
                        Method methodWithContext = classname.getDeclaredMethod("getCompletedQueue", Context.class);
                        String response = (String) methodWithContext.invoke(object, _context);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.FETCH_STATUS))
                    {
                        Method methodWithContext = classname.getDeclaredMethod(DownloadConstants.FETCH_STATUS, Context.class);
                        String response = (String) methodWithContext.invoke(object, _context);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.EDIT_DOWNLOAD_QUEUE))
                    {
                        ArrayList<DownloadRequest> downloadRequests = new Gson().fromJson(args.toString(), new TypeToken<List<DownloadRequest>>()
                        {
                        }.getType());
                        Log.v("DownloadPlugin", downloadRequests.toString());
                        Method methodWithArrayList = classname.getDeclaredMethod(DownloadConstants.EDIT_DOWNLOAD_QUEUE, Context.class, ArrayList.class);
                        String response = (String) methodWithArrayList.invoke(object, _context, downloadRequests);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.PAUSE_DOWNLOAD))
                    {
                        Method methodWithContext = classname.getDeclaredMethod(DownloadConstants.PAUSE_DOWNLOAD, Context.class);
                        String response = (String) methodWithContext.invoke(object, _context);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.RESUME_DOWNLOAD))
                    {
                        Method methodWithContext = classname.getDeclaredMethod(DownloadConstants.RESUME_DOWNLOAD, Context.class);
                        String response = (String) methodWithContext.invoke(object, _context);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.MEMORY_DETAILS))
                    {
                        Method methodWithNoParams = classname.getDeclaredMethod(DownloadConstants.MEMORY_DETAILS, Context.class);
                        String response = (String) methodWithNoParams.invoke(object, _context);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.REMOVE_CONTENT))
                    {
                        ArrayList<DownloadRequest> downloadRequests = new Gson().fromJson(args.toString(), new TypeToken<List<DownloadRequest>>()
                        {
                        }.getType());
                        Log.v("DownloadPlugin", downloadRequests.toString());
                        Method methodWithArrayList = classname.getDeclaredMethod(DownloadConstants.REMOVE_CONTENT, ArrayList.class, Context.class);
                        String response = (String) methodWithArrayList.invoke(object, downloadRequests);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.GET_CURRENT_DOWNLOADQUEUE))
                    {
                        Method methodWithNoParams = classname.getDeclaredMethod(DownloadConstants.GET_CURRENT_DOWNLOADQUEUE, noParams);
                        String response = (String) methodWithNoParams.invoke(object, null);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.GLOBAL_SETTINGS))
                    {
                        ArrayList<SettingsVO> downloadSettings = new Gson().fromJson(args.toString(), new TypeToken<List<SettingsVO>>()
                        {
                        }.getType());
                        Log.v("DownloadPlugin", downloadSettings.toString());
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.GLOBAL_SETTINGS, ArrayList.class, Context.class);
                        String response = (String) methodWithParams.invoke(object, downloadSettings, _context);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.DATABASE_OPERATIONS))
                    {
                        ArrayList<DownloadRequest> downloadRequests = new Gson().fromJson(args.toString(), new TypeToken<List<DownloadRequest>>()
                        {
                        }.getType());
                        Log.v("DownloadPlugin", downloadRequests.toString());
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.DATABASE_OPERATIONS, ArrayList.class, Context.class);
                        String response = (String) methodWithParams.invoke(object, downloadRequests, _context);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.FETCH_DOWNLOAD_HISTORY))
                    {
                        ArrayList<DownloadRequest> downloadRequests = new Gson().fromJson(args.toString(), new TypeToken<List<DownloadRequest>>()
                        {
                        }.getType());
                        Log.v("DownloadPlugin", downloadRequests.toString());
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.FETCH_DOWNLOAD_HISTORY, ArrayList.class, Context.class);
                        String response = (String) methodWithParams.invoke(object, downloadRequests, _context);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.GET_DOWNLOAD_STATE))
                    {
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.GET_DOWNLOAD_STATE, Context.class);
                        String response = (String) methodWithParams.invoke(object, _context);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.DELETE_FROM_DB))
                    {
                        ArrayList<DownloadRequest> downloadRequests = new Gson().fromJson(args.toString(), new TypeToken<List<DownloadRequest>>()
                        {
                        }.getType());
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.DELETE_FROM_DB, ArrayList.class, Context.class);
                        String response = (String) methodWithParams.invoke(object, downloadRequests, _context);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.LOAD_STORED_FILE))
                    {
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.LOAD_STORED_FILE, Context.class);
                        String response = (String) methodWithParams.invoke(object, _context);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.GET_NETWORK_STATE))
                    {
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.GET_NETWORK_STATE, Context.class);
                        String response = (String) methodWithParams.invoke(object, _context);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.SET_SESSION_ID))
                    {
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.SET_SESSION_ID, Context.class, String.class);
                        JSONArray jsonArray1 = args;
                        JSONObject jsonObject = jsonArray1.getJSONObject(0);
                        String sessionid = jsonObject.optString("sessionId");
                        String response = (String) methodWithParams.invoke(object, _context, sessionid);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.SET_ORIENTATION))
                    {
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.SET_ORIENTATION, Activity.class, Boolean.class);
                        JSONArray jsonArray1 = args;
                        JSONObject jsonObject = jsonArray1.getJSONObject(0);
                        boolean flag=jsonObject.optBoolean("lock");
                        String response = (String) methodWithParams.invoke(object, _context, flag);
                        callbackContext.success(response);
                    }
                    else if(action.equalsIgnoreCase(DownloadConstants.REMOVETOTALCONTENT))
                    {
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.REMOVETOTALCONTENT, Context.class);
                        String response = (String) methodWithParams.invoke(object, _context);
                        callbackContext.success(response);
                    }
                    else if(action.equalsIgnoreCase(DownloadConstants.REMOVE_ONLY_VIDEOS))
                    {
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.REMOVE_ONLY_VIDEOS, Context.class);
                        String response = (String) methodWithParams.invoke(object, _context);
                        callbackContext.success(response);
                    }
                    else if(action.equalsIgnoreCase(DownloadConstants.REMOVE_FROM_DR))
                    {
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.REMOVE_FROM_DR, Context.class,String.class,int.class);
                        JSONArray jsonArray1 = args;
                        JSONObject jsonObject = jsonArray1.getJSONObject(0);
                        int type=jsonObject.optInt("type");
                        String nodeId=jsonObject.optString("nodeId");
                        String response = (String) methodWithParams.invoke(object, _context,nodeId,type);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.STORE_DATA))
                    {
                       Log.v("cordova1", "method"+action);
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.STORE_DATA, Context.class, String.class,String.class);
                        JSONArray jsonArray1 = args;
                        JSONObject jsonObject = jsonArray1.getJSONObject(0);
                        String key = jsonObject.optString("key");
                        String value =jsonObject.optString("value");
                         Log.v("cordova1", "storedata"+key+"======"+value);
                        String response = (String) methodWithParams.invoke(object, _context, key,value);
                        callbackContext.success(response);
                    }
                    else if (action.equalsIgnoreCase(DownloadConstants.RETRIEVE_DATA))
                    {
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.RETRIEVE_DATA, Context.class, String.class);
                        JSONArray jsonArray1 = args;
                        JSONObject jsonObject = jsonArray1.getJSONObject(0);
                        String key = jsonObject.optString("key");
                       Log.v("cordova1", "retrieve"+key);
                        String response = (String) methodWithParams.invoke(object, _context, key);
                        callbackContext.success(response);
                    }
					else if (action.equalsIgnoreCase(DownloadConstants.RETRIEVE_DATA_WITH_FILE_EXISTENCE))
                    {
                        Method methodWithParams = classname.getDeclaredMethod(DownloadConstants.RETRIEVE_DATA_WITH_FILE_EXISTENCE, Context.class, String.class, String.class);
                        JSONArray jsonArray1 = args;
                        JSONObject jsonObject = jsonArray1.getJSONObject(0);
                        String key = jsonObject.optString("key");
						 String uri = jsonObject.optString("uri");
                       Log.v("cordova1", "retrieve"+key);
                        String response = (String) methodWithParams.invoke(object, _context, key, uri);
                        callbackContext.success(response);
                    }
                    else
                    {
                        callbackContext.success(action + "not matched");
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });


        return true;
    }

*/
}
