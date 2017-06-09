package com.nexttools.threads;

import android.content.Context;
import android.os.AsyncTask;

import com.nexttools.constants.NextToolConstants;
import com.nexttools.dao.Cards;
import com.nexttools.dao.Guide;
import com.nexttools.dao.Tabs;
import com.nexttools.interfaces.OnParseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by next on 10/5/17.
 */
public class HomeAsyncTask extends AsyncTask<Void ,Void , ArrayList<Tabs>  >{
    Context mContext;
    OnParseListener onParseListener;
   /* ArrayList<String> firstIterate = new ArrayList<>();*/
    ArrayList<Tabs> tabsArrayList = new ArrayList<>();

    public HomeAsyncTask(Context mContext, OnParseListener onParseListener) {
        this.mContext = mContext;
        this.onParseListener=onParseListener;
    }

    @Override
    protected ArrayList<Tabs> doInBackground(Void... voids) {
        String json = null;
        try {
            InputStream inputStream = mContext.getAssets().open("jsonData.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray jsonArray_Tabs = jsonObj.getJSONArray("tabs");
            for (int i = 0; i < jsonArray_Tabs.length(); i++) {
                ArrayList<Cards> cardsArrayList = new ArrayList<>();
                Tabs tabData = new Tabs();

                JSONObject jsonObject = jsonArray_Tabs.getJSONObject(i);

                String strTitle1 = jsonObject.getString("title");
                String strInfo1 = jsonObject.getString("info");
                String strIcon = jsonObject.getString("icon");
                tabData.setTitle(strTitle1);
                tabData.setIcon(strIcon);
                tabData.setInfo(strInfo1);



                JSONArray jsonArray_Cards = jsonObject.getJSONArray("cards");
                for (int j = 0; j < jsonArray_Cards.length(); j++) {
                    ArrayList<Guide> guideArrayList = new ArrayList<>();
                    Cards cardData = new Cards();

                    JSONObject jsonObject1 = jsonArray_Cards.getJSONObject(j);
                    String strTitle2 = jsonObject1.getString("title");
                    String strThumb1 = jsonObject1.getString("thumb");
                    String strInfo2 = jsonObject1.getString("info");
                    String strURL = jsonObject1.getString("url");
                    cardData.setTitle(strTitle2);
                    cardData.setThumb(strThumb1);
                    cardData.setInfo(strInfo2);
                    cardData.setUrl(strURL);


                    JSONArray jsonArray_Guide = jsonObject1.getJSONArray("guide");
                    for (int k = 0; k < jsonArray_Guide.length(); k++)
                    {

                        Guide guide = new Guide();
                        JSONObject jsonObject2 = jsonArray_Guide.getJSONObject(k);
                        String strThumb2 = jsonObject2.getString("thumb");
                        String strTitle3 = jsonObject2.getString("title");
                        String strColor = jsonObject2.getString("color");
                        String strDescription = jsonObject2.getString("description");
                        guide.setThumb(strThumb2);
                        guide.setTitle(strTitle3);
                        guide.setColour(strColor);
                        guide.setDescription(strDescription);
                        guideArrayList.add(guide);

                        /*Guide guide = new Guide(getApplicationContext());
                        guide.parse(jsonObject2);*/

                    }
                    cardData.setGuideInfo(guideArrayList);
                    cardsArrayList.add(cardData);
                }
                tabData.setCardInfo(cardsArrayList);
                tabsArrayList.add(tabData);
            }




        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return tabsArrayList;
    }

    //sending the data obtained in tabArrayList(Json Parsed data)
    @Override
    protected void onPostExecute(ArrayList<Tabs> tabses) {
        super.onPostExecute(tabses);
        NextToolConstants.tabsArrayList = tabsArrayList;
        onParseListener.onSuccess(tabsArrayList);
    }


}


