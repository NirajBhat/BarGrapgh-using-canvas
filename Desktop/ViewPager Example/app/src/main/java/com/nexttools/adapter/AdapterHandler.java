package com.nexttools.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.nexttools.R;
import com.nexttools.dao.Cards;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by next on 17/5/17.
 */
public class AdapterHandler extends ArrayAdapter<Cards> {

    List<Cards> cardsList,tempItems,suggestions;
    Context mContext;
    int resource, textViewResourceId;

    public AdapterHandler(Context context, int resource, int textViewResourceId, List<Cards> objects)
    {
        super(context, resource, textViewResourceId, objects);
        this.mContext = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;


        cardsList = objects;
        tempItems = new ArrayList<Cards>(objects); // this makes the difference.
        suggestions = new ArrayList<Cards>();
    }

    @Override
    public Cards getItem(int position) {
        return cardsList.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

            view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            Cards card = cardsList.get(position);
            if(card != null){
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(card.getTitle());
            }return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter(){
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Cards) resultValue).getTitle();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                suggestions.clear();
                for (Cards card : tempItems) {
                    if (card.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        suggestions.add(card);

                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }

        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            List<Cards> cardData = (List<Cards>) filterResults.values;
            if(filterResults != null && filterResults.count >0){
                clear();
                for(Cards cards : cardData){
                    add(cards);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
