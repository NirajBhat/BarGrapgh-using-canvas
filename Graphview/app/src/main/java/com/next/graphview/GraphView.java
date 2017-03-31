package com.next.graphview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class GraphView extends AppCompatActivity {

    BarView barView;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);
        barView = (BarView)findViewById(R.id.bar_view);
        button = (Button)findViewById(R.id.bar_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomSet(barView);
            }
        });
        randomSet(barView);
    }
    private void randomSet(BarView barView){
        int random = (int)(Math.random()*20)+6;
        ArrayList<String> test = new ArrayList<String>();
        for (int i=0; i<random; i++){
            test.add("test");
            test.add("pqg");
//            test.add(String.valueOf(i+1));
        }
        barView.setBottomTextList(test);

        ArrayList<Integer> barDataList = new ArrayList<Integer>();
        for(int i=0; i<random*2; i++){
            barDataList.add((int)(Math.random() * 100));
        }
        barView.setDataList(barDataList,100);
    }
}
