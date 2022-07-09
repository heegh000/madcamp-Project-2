package com.example.fivepiratesgame.ranking;

import static com.example.fivepiratesgame.MainActivity.mapAvatar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fivepiratesgame.R;
import com.example.fivepiratesgame.UserData;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private List<UserData> rankingList;
    private final Context context;

    public RankingAdapter(Context context, List<UserData> rankingList) {
        this.context = context;
        this.rankingList = rankingList;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ranking, parent, false);
        RankingViewHolder holder = new RankingViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        UserData uData = rankingList.get(position);

        holder.ivRankProfile.setImageResource(mapAvatar.get(uData.getAvatarID()));
        holder.tvRank.setText(Integer.toString(position+1));
        holder.tvNickname.setText(uData.getNickname());
        holder.tvGold.setText(uData.getGold());
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }

    public class RankingViewHolder extends RecyclerView.ViewHolder {
        protected ImageView ivRankProfile;
        protected TextView tvRank;
        protected TextView tvNickname;
        protected TextView tvGold;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ivRankProfile = (ImageView) itemView.findViewById(R.id.ivRankProfile);
            this.tvRank = (TextView) itemView.findViewById(R.id.tvRank);
            this.tvNickname = (TextView) itemView.findViewById(R.id.tvNickname);
            this.tvGold = (TextView) itemView.findViewById(R.id.tvGold);
        }
    }
}
