package com.example.fivepiratesgame.game;

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

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<PlayerData> playerList;
    private final Context context;

    public PlayerAdapter(Context context, List<PlayerData> playerList) {
        this.context = context;
        this.playerList = playerList;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_player, parent, false);
        PlayerViewHolder holder = new PlayerViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        PlayerData pData = playerList.get(position);

        holder.tvOrder.setText(Integer.toString(pData.getOrder()));
        holder.ivGameProfile.setImageResource(mapAvatar.get(pData.getAvatarID()));
        holder.tvNickname.setText(pData.getNickname());

        if(pData.getGold() != -1) {
            holder.tvGold.setText(Integer.toString(pData.getGold()) + " GOLD");
        }
    }


    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        protected ImageView ivGameProfile;
        protected TextView tvOrder;
        protected TextView  tvNickname;
        protected TextView  tvGold;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvOrder = (TextView) itemView.findViewById(R.id.tvOrder);
            this.ivGameProfile = (ImageView) itemView.findViewById(R.id.ivGameProfile);
            this.tvNickname = (TextView) itemView.findViewById(R.id.tvNickname);
            this.tvGold = (TextView) itemView.findViewById(R.id.tvGold);
        }
    }
}
