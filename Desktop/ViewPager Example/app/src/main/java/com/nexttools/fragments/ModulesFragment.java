package com.nexttools.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nexttools.R;
import com.nexttools.adapter.SubjectsDataAdapter;
import com.nexttools.constants.NextToolConstants;
import com.nexttools.dao.Cards;
import com.nexttools.dao.Tabs;
import com.nexttools.interfaces.OnParseListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by next on 10/5/17.
 */
public class ModulesFragment extends Fragment implements OnParseListener {
    // Store instance variables
    private String mTitle;
    private int mPage;
    String mCurentOrientation;
    List<Cards> cardsArrayList;
    RecyclerView mRecyclerView;
    GridLayoutManager layoutManager;
    Drawable d;
    public static final String TAG = "ModulesFragment";



    // newInstance constructor for creating fragment with arguments
    public static  ModulesFragment newInstance(int page, String title) {
        ModulesFragment fragmentFirst = new ModulesFragment();
        Bundle args = new Bundle();
        args.putInt("someInt",page);
        args.putString("someTitle",title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }




    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage =  getArguments().getInt("someInt",0);
        mTitle = getArguments().getString("someTitle");

        // Taking the json parsed data from the constant i.e NextToolConstants.tabsArrayList class and iterated
        ArrayList<Tabs> tabsLIst = NextToolConstants.tabsArrayList;
        if(tabsLIst != null) {
            Iterator it = tabsLIst.iterator();
            while (it.hasNext()) {
                Tabs object = (Tabs) it.next();
                if (object.getTitle().equalsIgnoreCase(mTitle)) {
                    cardsArrayList = object.getCardInfo();
                }
            }
        }
    }

    // Inflate the view for the fragment based on layout XML
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modules_fragment,container,false);
        mRecyclerView= (RecyclerView) view.findViewById(R.id.recyclerView_Cards_Subjects);
        //Condition to check the orientation of device and set the layout according to it.
        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            layoutManager = new GridLayoutManager(getContext(),4
                    ,GridLayoutManager.VERTICAL,false);
        }else {
            layoutManager = new GridLayoutManager(getContext(), 2
                    , GridLayoutManager.VERTICAL, false);
        }
        mRecyclerView.setLayoutManager(layoutManager);
       /* LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(manager);*/
        //Seting the data to adpater

        SubjectsDataAdapter dataAdapter = new SubjectsDataAdapter(getContext(),cardsArrayList , this);
        mRecyclerView.setAdapter(dataAdapter);
        return  view;
    }


    @Override
    public void onSuccess(Object object) {
        Cards cardData=cardsArrayList.get((Integer) object);
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.string.app_name );


        final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialogue, null);
        dialog.setContentView(R.layout.dialogue);
        dialog.setTitle(cardData.getTitle());
        TextView popupTitle = (TextView) dialogView.findViewById(R.id.textTitle_Dialogue);
        TextView textData = (TextView) dialogView.findViewById(R.id.textData_Dialogue);
        ImageView imageView= (ImageView) dialogView.findViewById(R.id.imageData_dialogue);

        textData.setText(cardData.getInfo());
        popupTitle.setText(cardData.getTitle());
        try { // get input stream
            String[] split = cardData.getThumb().split("assets/");
            String imageName = split[1].replace("svg", "png");
            InputStream ims =getActivity().getAssets().open(imageName);
            // load image as
            d= Drawable.createFromStream(ims, null);
            // set image to ImageView
           imageView.setImageDrawable(d);
            ims .close();
        }
        catch(IOException ex) {
            Log.i(TAG, "onBindViewHolder: "+ex.getMessage());
            return;
        }
        imageView.setImageDrawable(d);
        dialog.setContentView(dialogView);
        //dialog.setContentView(dialogView, dialogParams);
        dialog.show();

        //Button in the dialog box of info of each card.
        Button dismiss= (Button) dialogView.findViewById(R.id.dismissButton);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        // Toast.makeText(mContext,"llll",Toast.LENGTH_SHORT).show();
    }





    @Override
    public void onException() {

    }
}
