package com.burhanrashid52.imageeditor.image;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;

import com.burhanrashid52.imageeditor.R;
import com.burhanrashid52.imageeditor.filters.FilterListener;
import com.burhanrashid52.imageeditor.filters.FilterViewAdapter;
import com.burhanrashid52.imageeditor.tools.ToolType;
import com.burhanrashid52.imageeditor.views.PhotoFilter;

public class ImageEditorDialogFragment extends Fragment implements EditingImageToolsAdapter.OnItemSelected, FilterListener {

    private static ImageEditorDialogFragment imageEditorDialogFragment;

    private OnToolImageSelectedListener onToolImageSelectedListener;

    private RecyclerView mRvTools;
    private EditingImageToolsAdapter mEditingToolsAdapter;

    private RecyclerView rvAdjustView;
    private RecyclerView rvOpacityView;
    private RecyclerView rvBlendingView;
    private RecyclerView rvFilterView;
    private RecyclerView rvFrameView;

    public static ImageEditorDialogFragment getInstance() {
        if (imageEditorDialogFragment == null) {
            imageEditorDialogFragment = new ImageEditorDialogFragment();
        }
        return imageEditorDialogFragment;
    }

    public void setOnToolImageSelectedListener(OnToolImageSelectedListener onToolImageSelectedListener) {
        this.onToolImageSelectedListener = onToolImageSelectedListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_image_dialog_test, container, false);
        initView(view);
        return view;
    }

    private void initView(View rootView) {
        mRootView = rootView.findViewById(R.id.rootView);

        mEditingToolsAdapter = new EditingImageToolsAdapter(this);
        mRvTools = rootView.findViewById(R.id.rvConstraintTools);
        LinearLayoutManager llmTools = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        rvAdjustView = (RecyclerView) rootView.findViewById(R.id.rvAdjustView);
        rvOpacityView = (RecyclerView) rootView.findViewById(R.id.rvOpacityView);
        rvBlendingView = (RecyclerView) rootView.findViewById(R.id.rvBlendingView);
        rvFilterView = (RecyclerView) rootView.findViewById(R.id.rvFilterView);
        rvFrameView = (RecyclerView) rootView.findViewById(R.id.rvFrameView);

        FilterViewAdapter mAdjustViewAdapter = new FilterViewAdapter(this);
        LinearLayoutManager llmAdjust = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvFilterView.setLayoutManager(llmAdjust);
        rvFilterView.setAdapter(mAdjustViewAdapter);


        FilterViewAdapter mOpacityViewAdapter = new FilterViewAdapter(this);
        LinearLayoutManager llmOpacity = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvFilterView.setLayoutManager(llmOpacity);
        rvFilterView.setAdapter(mOpacityViewAdapter);

        FilterViewAdapter mBlendingViewAdapter = new FilterViewAdapter(this);
        LinearLayoutManager llmBlending = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvFilterView.setLayoutManager(llmBlending);
        rvFilterView.setAdapter(mBlendingViewAdapter);

        FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
        LinearLayoutManager llmFilters = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvFilterView.setLayoutManager(llmFilters);
        rvFilterView.setAdapter(mFilterViewAdapter);

        FilterViewAdapter mFrameViewAdapter = new FilterViewAdapter(this);
        LinearLayoutManager llmFFrame = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvFilterView.setLayoutManager(llmFFrame);
        rvFilterView.setAdapter(mFrameViewAdapter);


    }

    boolean mIsFilterVisible;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private ConstraintLayout mRootView;

    void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(rvFilterView.getId(), ConstraintSet.START);
            mConstraintSet.connect(rvFilterView.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(rvFilterView.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(rvFilterView.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(rvFilterView.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    //    ADJUST,
//    OPACITY,
//    BLENDING,
//    FILTER,
//    FRAME
    @Override
    public void onToolSelected(ToolImageType toolType) {
        switch (toolType) {
            case ADJUST:
                showFilter(true);
//                onToolImageSelectedListener.onAdjustClick();
                break;
            case OPACITY:
                showFilter(true);
//                onToolImageSelectedListener.onOpacityClick();
                break;
            case BLENDING:
                showFilter(true);
//                onToolImageSelectedListener.onBlendingClick();
                break;
            case FILTER:
                showFilter(true);
//                onToolImageSelectedListener.onFilterClick();
                break;
            case FRAME:
                showFilter(true);
//                onToolImageSelectedListener.onFrameClick();
                break;
        }
    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {

    }

    public interface OnToolImageSelectedListener {
        public void onAdjustClick();

        public void onOpacityClick();

        public void onBlendingClick();

        public void onFilterClick();

        public void onFrameClick();
    }
}
