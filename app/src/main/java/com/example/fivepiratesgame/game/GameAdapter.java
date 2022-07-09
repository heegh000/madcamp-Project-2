package com.example.fivepiratesgame.game;

import static com.example.fivepiratesgame.MainActivity.mapAvatar;

import android.content.Context;
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

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private List<UserData> playerList;
    private final Context context;

    public GameAdapter(Context context, List<UserData> playerList) {
        this.context = context;
        this.playerList = playerList;
    }

    @NonNull
    @Override
    public GameAdapter.GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_player, parent, false);
        GameViewHolder holder = new GameViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        UserData pData = playerList.get(position);

        holder.ivGameProfile.setImageResource(mapAvatar.get(pData.getAvatarID()));
        holder.tvNickname.setText(pData.getNickname());
        holder.tvGold.setText(pData.getGold() + "GOLD");
    }


    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public class GameViewHolder extends RecyclerView.ViewHolder {
        protected ImageView ivGameProfile;
        protected TextView  tvNickname;
        protected TextView  tvGold;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ivGameProfile = (ImageView) itemView.findViewById(R.id.ivHisProfile);
            this.tvNickname = (TextView) itemView.findViewById(R.id.tvDate);
            this.tvGold = (TextView) itemView.findViewById(R.id.tvResult);
        }
    }
}
