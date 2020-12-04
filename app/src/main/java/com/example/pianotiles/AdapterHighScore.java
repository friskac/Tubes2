package com.example.pianotiles;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import java.util.ArrayList;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class AdapterHighScore extends BaseAdapter {


    private List<Score> score;
    private Presenter presenter;
    private Context context;

    public AdapterHighScore(Context context, Presenter presenter) {
        this.context = context;
        this.presenter = presenter;
        this.score = new LinkedList<Score>();
        int [] arr = this.presenter.getPreference().getHighScores();
        for(int i = arr.length-1; i>=0; i--){
            this.score.add(new Score(arr[i]));
        }
    }

    @Override
    public int getCount() {
        return this.score.size();
    }

    @Override
    public Object getItem(int position) {
        return this.score.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.score_item, parent, false);
            viewHolder = new ViewHolder(convertView,this.presenter);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.updateView((Score) this.getItem(position), position);

        return convertView;
    }

    public void update(List<Score> getScore) {
        this.score.clear();
        this.score.addAll((getScore));
        this.notifyDataSetChanged();
    }

    private class ViewHolder {
        private int position;
        private TextView tvScore;

        public ViewHolder(View view,Presenter presenter) {
            this.tvScore = view.findViewById(R.id.num);
        }

        public void updateView(Score score, int position) {
            this.position = position;
            this.tvScore.setText(score.getScore()+"");


        }
    }

}
