package com.nexttools.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.nexttools.R;
import com.nexttools.activity.DownloadFile;
import com.nexttools.constants.NextToolConstants;
import com.nexttools.dao.Cards;
import com.nexttools.interfaces.DownloadListerner;
import com.nexttools.interfaces.OnParseListener;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by next on 8/5/17.
 */
public class SubjectsDataAdapter extends RecyclerView.Adapter<SubjectsDataAdapter.MyViewholder>  {
    Context mContext;
    private static final String TAG="adapter";
    List<Cards> cardsList = new ArrayList<>();
    Drawable d;
    Cards card;
    DownloadFile download=null;
    OnParseListener parseListener;
    MyViewholder mCurrentHolder;

    public SubjectsDataAdapter(Context mContext, List<Cards> cardsList, OnParseListener onParseListener) {
        this.mContext = mContext;
        this.cardsList = cardsList;
        parseListener = onParseListener;

    }

    @Override
    public SubjectsDataAdapter.MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout,parent,false);
        return new MyViewholder(view);
    }

    @Override
    public void onBindViewHolder(final SubjectsDataAdapter.MyViewholder holder, final int position) {

        card = cardsList.get(position);
        holder.textinfo.setImageResource(R.drawable.ic_info_outline_black_18dp);
        final String  data = card.getInfo();
        final String imageSet = card.getThumb();
        final String poppupcardTitle=card.getTitle();

        //taking module url
        final String moduleurl=card.getUrl();
        Log.i(TAG, "onBindViewHolder: "+moduleurl);
        holder.textinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                parseListener.onSuccess(position);

            }
        });
        //Sets the title to the cards in recyclerview
        holder.title.setText(card.getTitle());

        try { // get input stream
            String[] split = card.getThumb().split("assets/");
            String imageName = split[1].replace("svg", "png");
            InputStream ims =mContext.getAssets().open(imageName);
            // load image as
            d= Drawable.createFromStream(ims, null);
            // set image to ImageView
            holder.mImageView.setImageDrawable(d);
            ims .close();
        }
        catch(IOException ex) {
            Log.i(TAG, "onBindViewHolder: "+ex.getMessage());
            return;
        }
       //Onclick of the image on card move to next activity ie starts the downloading the content
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checking Internet connected or not is checked by calling the method of constants class
               boolean value = NextToolConstants.internetConnectvity(mContext);
                if (download == null) {
                    download = new DownloadFile(mContext);
                }
                    mCurrentHolder = holder;
                if(value ) {
                    mCurrentHolder.mCircularProgressBar.setVisibility(View.VISIBLE);

                    download.downloadModuleFiels(moduleurl, NextToolConstants.zipPath + moduleurl + ".zip", NextToolConstants.unZippath, new DownloadListerner() {
                        @Override
                        public void downloadCompletedListerner(boolean taskCompleted) {
                              mCurrentHolder.mCircularProgressBar.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void progressListerner(int progress) {
                            if (mCurrentHolder != null) {
                                mCurrentHolder.mCircularProgressBar.setProgress(progress);
                                mCurrentHolder.mCircularProgressBar.setColor(ContextCompat.getColor(mContext, R.color.dark_blue));
                                mCurrentHolder.mCircularProgressBar.setBackgroundColor(mContext.getResources().getColor(R.color.circleProgressCompleteEndGradient));
                                mCurrentHolder.mCircularProgressBar.setProgressBarWidth(mContext.getResources().getDimension(R.dimen.progressBarWidth_5dp));
                                mCurrentHolder.mCircularProgressBar.setBackgroundProgressBarWidth(mContext.getResources().getDimension(R.dimen.progressBarBackgroundWidth_10));
                                if (progress == 100) {
                                    mCurrentHolder.mCircularProgressBar.setVisibility(View.INVISIBLE);

                                    Log.i(TAG, "progressListerner: -------" + progress);
                                }
                            }
                        }
                    });
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return cardsList.size();

    }


    public class MyViewholder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView textinfo;
        ImageView mImageView;
        CircularProgressBar mCircularProgressBar;


        public MyViewholder(final View itemView) {
            super(itemView);

            textinfo= (ImageView) itemView.findViewById(R.id.textinfo);
            title= (TextView) itemView.findViewById(R.id.title_name);
            mImageView= (ImageView) itemView.findViewById(R.id.imageView);
            mCircularProgressBar= (CircularProgressBar) itemView.findViewById(R.id.new_circleprogress_bar);

        }
    }
}
