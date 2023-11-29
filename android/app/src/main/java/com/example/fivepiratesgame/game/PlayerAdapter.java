package com.example.fivepiratesgame.game;

import static com.example.fivepiratesgame.MainActivity.mapAvatar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fivepiratesgame.R;

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

        String tmpOrder = Integer.toString(pData.getOrder());
        switch (tmpOrder){
            case "5": tmpOrder = "선장"; break;
            case "4": tmpOrder = "부선장"; break;
            case "3": tmpOrder = "고급선원"; break;
            case "2": tmpOrder = "일반선원"; break;
            case "1": tmpOrder = "잡일꾼"; break;
        }

        holder.tvOrder.setText(tmpOrder);
        holder.ivGameProfile.setImageResource(mapAvatar.get(pData.getAvatarID()));
        holder.tvNickname.setText(pData.getNickname());
        holder.setIDState(pData.getUserID(), pData.getState());

        if(pData.getGold() != -1) {
            holder.tvGold.setText(Integer.toString(pData.getGold()) + " GOLD");
        }

        if(holder.getState() == 0){
            holder.player_layout.setBackgroundResource(R.color.transparent);
            holder.ivGameProfile.setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.MULTIPLY);
        }

        if(holder.getHolderUID() == GameActivity.gameActivity.me.getUserID()){
            holder.player_layout.setBackgroundResource(R.drawable.game_player_me);
        }

        holder.ivMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int err_code = 0;

                if(holder.getHolderUID() == GameActivity.gameActivity.me.getUserID()){
                    err_code = 1;
                }
                if(holder.getState() == 0){
                    err_code = 2;

                }
//                if(GameActivity.gameActivity.me.getVote() != -1) {
//                    err_code = 3;
//                }
                else {
                    GameActivity.gameActivity.showSendDialog(holder, err_code);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public class PlayerViewHolder extends RecyclerView.ViewHolder{
        protected LinearLayout player_layout;
        protected ImageView ivGameProfile;
        protected TextView tvOrder;
        protected TextView  tvNickname;
        protected TextView  tvGold;
        protected ImageView ivMsg;

        private String holderUID;
        private int state;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.player_layout = (LinearLayout) itemView.findViewById(R.id.rvPlayer_layout);
            this.tvOrder = (TextView) itemView.findViewById(R.id.tvOrder);
            this.ivGameProfile = (ImageView) itemView.findViewById(R.id.ivGameProfile);
            this.tvNickname = (TextView) itemView.findViewById(R.id.tvNickname);
            this.tvGold = (TextView) itemView.findViewById(R.id.tvGold);
            this.ivMsg = (ImageView) itemView.findViewById(R.id.ivMsg);

        }

        public void setIDState(String holderUID, int state) {
            this.holderUID = holderUID;
            this.state = state;
        }

        public String getHolderUID() {
            return holderUID;
        }

        public int getState() {
            return state;
        }
    }
}
