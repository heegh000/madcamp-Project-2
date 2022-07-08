package com.example.fivepiratesgame.History;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fivepiratesgame.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private ArrayList<HistoryData> historyArrayList;
    private final Context context;

    public HistoryAdapter(Context context, ArrayList<HistoryData> historyArrayList) {
        this.context = context;
        this.historyArrayList = historyArrayList;
    }

    @NonNull
    @Override
    public HistoryAdapter.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        HistoryViewHolder holder = new HistoryViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.HistoryViewHolder holder, int position) {
        HistoryData hisData = historyArrayList.get(position);

        holder.tvDate.setText(hisData.getTime());
        holder.tvResult.setText(hisData.getResult());
    }

    @Override
    public int getItemCount() {
        return historyArrayList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        protected ImageView ivHisProfile;
        protected TextView  tvDate;
        protected TextView  tvResult;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ivHisProfile = (ImageView) itemView.findViewById(R.id.ivHisProfile);
            this.tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            this.tvResult = (TextView) itemView.findViewById(R.id.tvResult);
        }
    }
}
