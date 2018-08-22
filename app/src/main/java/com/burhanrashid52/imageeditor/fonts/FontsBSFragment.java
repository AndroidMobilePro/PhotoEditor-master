package com.burhanrashid52.imageeditor.fonts;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.burhanrashid52.imageeditor.FontsAdapter;
import com.burhanrashid52.imageeditor.R;
import com.burhanrashid52.imageeditor.constants.Contants;

public class FontsBSFragment extends BottomSheetDialogFragment {

    public FontsBSFragment() {
        // Required empty public constructor
    }

    private FontsListener mFontsListener;

    public void setFontListener(FontsListener fontListener) {
        mFontsListener = fontListener;
    }

    public interface FontsListener {
        void onFontClick(String fonts);
    }


    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_fonts_dialog, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        final TextView tvPreview = contentView.findViewById(R.id.tvPreview);

        RecyclerView rvEmoji = contentView.findViewById(R.id.rvEmoji);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 5);
        rvEmoji.setLayoutManager(gridLayoutManager);
        FontsAdapter fontsAdapter = new FontsAdapter(getActivity());
        //This listener will change the text fonts when clicked on any fonts from picker
        fontsAdapter.setFontsPickerListener(new FontsAdapter.OnTextPickerClickListener() {
            @Override
            public void onTextPickerClickListener(String fonts) {
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
                        Contants.folderFontPath + fonts);
                tvPreview.setTypeface(typeface);
                mFontsListener.onFontClick(fonts);
            }
        });
        rvEmoji.setAdapter(fontsAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}