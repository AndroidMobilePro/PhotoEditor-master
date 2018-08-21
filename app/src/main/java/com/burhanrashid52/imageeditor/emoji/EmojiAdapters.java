package com.burhanrashid52.imageeditor.emoji;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.burhanrashid52.imageeditor.EmojiBSFragment;
import com.burhanrashid52.imageeditor.R;
import com.burhanrashid52.imageeditor.filters.FilterListener;
import com.burhanrashid52.imageeditor.views.PhotoEditor;

import java.util.ArrayList;

public class EmojiAdapters extends RecyclerView.Adapter<EmojiAdapters.ViewHolder> {


    ArrayList<String> emojisList = new ArrayList<>();
    private EmojiBSFragment.EmojiListener mEmojiListener;

    public EmojiAdapters(Context context, EmojiBSFragment.EmojiListener mEmojiListener) {
        emojisList = PhotoEditor.getEmojis(context);
        this.mEmojiListener = mEmojiListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_emoji_s, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtEmoji.setText(emojisList.get(position));
    }

    @Override
    public int getItemCount() {
        return emojisList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtEmoji;

        ViewHolder(View itemView) {
            super(itemView);
            txtEmoji = itemView.findViewById(R.id.txtEmoji);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEmojiListener != null) {
                        mEmojiListener.onEmojiClick(emojisList.get(getLayoutPosition()));
                    }
//                        dismiss();
                }
            });
        }
    }
}