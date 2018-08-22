package com.burhanrashid52.imageeditor;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.burhanrashid52.imageeditor.filters.FilterListener;
import com.burhanrashid52.imageeditor.views.PhotoFilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.2
 * @since 5/23/2018
 */
public class EditViewAdapter extends RecyclerView.Adapter<EditViewAdapter.ViewHolder> {

    private Context context;
    private List<String> list = new ArrayList<>();
    private EditViewListener mEditViewListener;

    public EditViewAdapter(Context context) {
        this.context = context;
        list.add("FORMAT");
        list.add("FONT");
        list.add("TEXT SIZE");
        list.add("COLOR");
        list.add("SHADOW");
        list.add("STROKE");
        list.add("HIGHLIGHT");
        list.add("SPACING");
        list.add("BLUR");
    }

    public EditViewAdapter(Context context, EditViewListener editViewListener) {
        this(context);
        this.mEditViewListener = editViewListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_edit_text_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String textContent = list.get(position);
        holder.mTxtName.setText(textContent);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (textContent) {
                    case "FORMAT":
                        mEditViewListener.onFormatListener();
                        break;
                    case "FONT":
                        mEditViewListener.onFontListener();
                        break;
                    case "TEXT SIZE":
                        mEditViewListener.onTextSizeListener();
                        break;
                    case "COLOR":
                        mEditViewListener.onColorListener();
                        break;
                    case "SHADOW":
                        mEditViewListener.onShadowListener();
                        break;
                    case "STROKE":
                        mEditViewListener.onStrokeListener();
                        break;
                    case "HIGHLIGHT":
                        mEditViewListener.onHighLightListener();
                        break;
                    case "SPACING":
                        mEditViewListener.onSpacingListener();
                        break;
                    case "BLUR":
                        mEditViewListener.onBlurListener();
                        break;
                    default:
                        mEditViewListener.onFormatListener();
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTxtName;
        View mView;

        ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imgView);
            mTxtName = itemView.findViewById(R.id.txtName);
            mView = itemView.findViewById(R.id.viewParent);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
        }
    }


    public interface EditViewListener {
        void onFormatListener();

        void onFontListener();

        void onTextSizeListener();

        void onColorListener();

        void onShadowListener();

        void onStrokeListener();

        void onHighLightListener();

        void onSpacingListener();

        void onBlurListener();

    }
}
