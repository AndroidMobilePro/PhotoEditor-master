package com.burhanrashid52.imageeditor;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.burhanrashid52.imageeditor.constants.Contants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed Adel on 5/8/17.
 */

public class FontsAdapter extends RecyclerView.Adapter<FontsAdapter.ViewHolder> {
    //    private static String sFonts[] = {"Amatic-Bold.ttf", "armalite_rifle.ttf", "black_jack.ttf", "Capture_it.ttf",
//            "CODE Bold.otf", "EraserRegular.ttf", "GrandHotel-Regular.otf", "impact2.otf", "KaushanScript-Regular.otf",
//            "LDFComicSans.ttf", "Lobster_1.3.otf", "LobsterTwo-Bold.otf", "Pacifico.ttf", "quick_end_jerk.ttf",
//            "Top_Secret.ttf", "TrashHand.ttf", "troika.otf"};
//    private static final ArrayList<String> sArrayString = new ArrayList<>();
    private static final ArrayList<String> fonts = new ArrayList<>();

    private Context context;
    private LayoutInflater inflater;
    private OnTextPickerClickListener onTextPickerClickListener;

    FontsAdapter(@NonNull Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
//        sArrayString.clear();
//        for (int i = 0; i < sFonts.length; i++) {
//            sArrayString.add(sFonts[i]);
//        }

        final AssetManager assetManager = context.getAssets();
        try {
            String[] filelistInSubfolder = assetManager.list(Contants.folderFont);
            for (int i = 0; i < filelistInSubfolder.length; i++) {
                // Get filename of file or directory
                String filename = filelistInSubfolder[i];
                Log.d("TAGGG", filename);
                fonts.add(filename);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fonts_picker_item_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.fontsPickerView.setTypeface(Typeface.createFromAsset(context.getAssets(),
                Contants.folderFontPath + fonts.get(position)));
        holder.fontsPickerView.setText("AA");
    }

    @Override
    public int getItemCount() {
        return fonts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView fontsPickerView;

        public ViewHolder(final View itemView) {
            super(itemView);
            fontsPickerView = itemView.findViewById(R.id.fonts_picker_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTextPickerClickListener != null) {
                        onTextPickerClickListener.onTextPickerClickListener(fonts.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public void setFontsPickerListener(OnTextPickerClickListener onTextPickerClickListener) {
        this.onTextPickerClickListener = onTextPickerClickListener;
    }

    public interface OnTextPickerClickListener {
        void onTextPickerClickListener(String textContent);
    }
}
