package com.example.chatroomsmall.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatroomsmall.R;
import com.example.chatroomsmall.model.ChatModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatAdapter extends FirestoreRecyclerAdapter<ChatModel, ChatAdapter.ChatViewHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ChatAdapter(@NonNull FirestoreRecyclerOptions<ChatModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull ChatModel model) {

        Log.e("Text Message", (model.getMessage()));
        holder.tvMessage.setText(model.getMessage());
        Glide.with(holder.imageView).load(model.getUser_image_url()).into(holder.imageView);
        Log.e("CHAT_IMAGE", ""+ model.getChat_image());
        if (model.getChat_image() != null) {
            Glide.with(holder.chatImageView).load(model.getChat_image())
                    .into(holder.chatImageView);
            holder.chatImageView.setVisibility(View.VISIBLE);
        } else {
            holder.chatImageView.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMessage;
        private ImageView imageView;
        private ImageView chatImageView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_content_message);
            imageView = itemView.findViewById(R.id.img_user);
            chatImageView = itemView.findViewById(R.id.image_cam);
        }
    }
}
