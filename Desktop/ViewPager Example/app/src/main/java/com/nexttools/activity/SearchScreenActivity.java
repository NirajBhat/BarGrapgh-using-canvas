package com.nexttools.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cardinalsolutions.progressindicator.*;
import com.nexteducation.downloadmanager.DownloadProgressListener;
import com.nexttools.R;

import com.nexttools.adapter.AdapterHandler;
import com.nexttools.constants.NextToolConstants;
import com.nexttools.dao.Cards;
import com.nexttools.dao.Tabs;
import com.nexttools.interfaces.DownloadListerner;
import com.nexttools.interfaces.OnParseListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchScreenActivity extends AppCompatActivity implements DownloadListerner {

    ProgressDialog mProg;
    AutoCompleteTextView mSearchEditText;

    //List where the data from NextToolConstants.tabsArrayList data is added to this list.
    List<Tabs> tabsList;

    //This list is created to add the title of Tabs ,data obtained from the NextToolConstants.tabsArrayList
    List<Object> objectsList = new ArrayList<>();

    //This list is created to add the items of card object which contains title,url,thumb.And this this list passed to ArrayAdapter
    List<String> filteredList = new ArrayList<String>();

    //This list is created to add the whole card object details.And passed in to the method addTextLister in onTextChanged & later passed to filteredList
    List<Cards> cardsList = new ArrayList<>();

    public static final String TAG = "SearchScreenActivity";

    String BASE_URL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
        ImageView mBackButton = (ImageView) findViewById(R.id.backButton);
        mSearchEditText = (AutoCompleteTextView) findViewById(R.id.searchInput);
        mSearchEditText.setThreshold(1);
          mProg = new ProgressDialog(SearchScreenActivity.this);

        BASE_URL = getResources().getString(R.string.Constant_url);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchScreenActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        //Implementing the onclick function on the selected item list in AutoCompleteText View
        mSearchEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Cards selectedCard = (Cards) adapterView.getItemAtPosition(i);
                final String url = selectedCard.getUrl();
                Log.i(TAG, "onItemClick: "+selectedCard.getUrl());
                File unZipFile = new File(NextToolConstants.unZippath+url);
                Log.i(TAG, "onItemClick----------> : "+unZipFile);
                if(unZipFile.exists()) {
                    //Moving to webloadActivity through intent
                    Intent intent = new Intent(SearchScreenActivity.this, WebViewActivity.class);
                    intent.putExtra("UrlModule",url);
                    startActivity(intent);

                }else {
                    final Dialog dialog = new Dialog(SearchScreenActivity.this);
                    dialog.setContentView(R.layout.dialogpop_up_to_download);
                    TextView text = (TextView) dialog.findViewById(R.id.textDialog);
                    text.setText("The content you searched is not available, Do you wish to download it...!");
                    dialog.show();

                    Button mOkay_button = (Button) dialog.findViewById(R.id.okay_button);
                    mOkay_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownloadFile downloadFile = new DownloadFile(getApplication());
                            downloadFile.downloadModuleFiels(url, NextToolConstants.zipPath, NextToolConstants.unZippath,SearchScreenActivity.this );
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        addTextListerner();
        setTitles();


    }

    //This method provides the card details which is iterated from the NextToolConstants.tabsArrayList
    private void setTitles()

    {
        tabsList = NextToolConstants.tabsArrayList;

        if (tabsList != null) {
            Iterator iterator = tabsList.iterator();
            while (iterator.hasNext()) {
                Tabs tabs = (Tabs) iterator.next();
                objectsList.add(tabs.getTitle());
                Iterator iterator1 = tabs.getCardInfo().iterator();
                while (iterator1.hasNext()) {
                    Cards card = (Cards) iterator1.next();
                    cardsList.add(card);

                }
            }
        }
    }

    // Implementing the search functionality .
    public void addTextListerner() {
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                AdapterHandler adapterHandler = new AdapterHandler(SearchScreenActivity.this,R.layout.cardview_layout,R.id.textTitle,cardsList);
                mSearchEditText.setAdapter(adapterHandler);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void downloadCompletedListerner(boolean taskCompleted) {

    }

    @Override
    public void progressListerner(int progress) {
        mProg.setMessage("Downloading the Content....");
        mProg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProg.setCancelable(false);
        mProg.setIndeterminate(false);
        mProg.setProgress(progress);
        mProg.show();

        if(progress == 100){
            mProg.dismiss();

        }

    }
}
