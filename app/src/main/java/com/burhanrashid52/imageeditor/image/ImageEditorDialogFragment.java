package com.burhanrashid52.imageeditor.image;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.burhanrashid52.imageeditor.R;
import com.burhanrashid52.imageeditor.edit.image.ThumbnailsAdapter;
import com.burhanrashid52.imageeditor.filters.FilterListener;
import com.burhanrashid52.imageeditor.filters.FilterViewAdapter;
import com.burhanrashid52.imageeditor.tools.ToolType;
import com.burhanrashid52.imageeditor.utils.BitmapUtils;
import com.burhanrashid52.imageeditor.utils.SpacesItemDecoration;
import com.burhanrashid52.imageeditor.views.PhotoFilter;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

public class ImageEditorDialogFragment extends Fragment implements EditingImageToolsAdapter.OnItemSelected, FilterListener, ThumbnailsAdapter.ThumbnailsAdapterListener, View.OnClickListener {
    // load native image filters library
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private static ImageEditorDialogFragment imageEditorDialogFragment;

    private OnToolImageSelectedListener onToolImageSelectedListener;

    private RecyclerView mRvTools;
    private EditingImageToolsAdapter mEditingToolsAdapter;

    private RecyclerView rvAdjustView;
    private RecyclerView rvOpacityView;
    private RecyclerView rvBlendingView;
    private RecyclerView rvFilterView;
    private RecyclerView rvFrameView;

    ThumbnailsAdapter mAdapter;

    List<ThumbnailItem> thumbnailItemList = new ArrayList<>();

    private ImageView imgRotation;
    private ImageView imgCrop;
    private ImageView imgDuplicate;
    private ImageView imgBringToFront;

    private CropImageDialogFragment cropImageDialogFragment;

    public static ImageEditorDialogFragment getInstance() {
        if (imageEditorDialogFragment == null) {
            imageEditorDialogFragment = new ImageEditorDialogFragment();
        }
        return imageEditorDialogFragment;
    }

    Bitmap currentBitmap;
    public void setBitmap(Bitmap bitmap) {
        currentBitmap = bitmap;
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

        imgCrop = (ImageView) rootView.findViewById(R.id.imgCrop);
        imgRotation = (ImageView) rootView.findViewById(R.id.imgRotation);
        imgDuplicate = (ImageView) rootView.findViewById(R.id.imgDuplicate);
        imgBringToFront = (ImageView) rootView.findViewById(R.id.imgBringToFront);

        FilterViewAdapter mAdjustViewAdapter = new FilterViewAdapter(this);
        LinearLayoutManager llmAdjust = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvAdjustView.setLayoutManager(llmAdjust);
        rvAdjustView.setAdapter(mAdjustViewAdapter);


        FilterViewAdapter mOpacityViewAdapter = new FilterViewAdapter(this);
        LinearLayoutManager llmOpacity = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvOpacityView.setLayoutManager(llmOpacity);
        rvOpacityView.setAdapter(mOpacityViewAdapter);

        FilterViewAdapter mBlendingViewAdapter = new FilterViewAdapter(this);
        LinearLayoutManager llmBlending = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvBlendingView.setLayoutManager(llmBlending);
        rvBlendingView.setAdapter(mBlendingViewAdapter);

        thumbnailItemList = new ArrayList<>();
        mAdapter = new ThumbnailsAdapter(getActivity(), thumbnailItemList, this);

//        FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
        LinearLayoutManager llmFilters = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvFilterView.setLayoutManager(llmFilters);
        rvFilterView.setItemAnimator(new DefaultItemAnimator());
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getResources().getDisplayMetrics());
        rvFilterView.addItemDecoration(new SpacesItemDecoration(space));
        rvFilterView.setAdapter(mAdapter);
        prepareThumbnail(null);

//        FilterViewAdapter mFrameViewAdapter = new FilterViewAdapter(this);
//        LinearLayoutManager llmFFrame = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        rvFrameView.setLayoutManager(llmFFrame);
//        rvFrameView.setAdapter(mFrameViewAdapter);

//        cropImageDialogFragment = new CropImageDialogFragment();
        imgCrop.setOnClickListener(this);
        imgRotation.setOnClickListener(this);
        imgDuplicate.setOnClickListener(this);
        imgBringToFront.setOnClickListener(this);
    }

    /**
     * Renders thumbnails in horizontal list
     * loads default image from Assets if passed param is null
     *
     * @param bitmap
     */
    public void prepareThumbnail(final Bitmap bitmap) {
        Runnable r = new Runnable() {
            public void run() {
                Bitmap thumbImage;

                if (bitmap == null) {
                    thumbImage = BitmapUtils.getBitmapFromAssets(getActivity(), "dog.jpg", 100, 100);
                } else {
                    thumbImage = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                }

                if (thumbImage == null)
                    return;

                ThumbnailsManager.clearThumbs();
                thumbnailItemList.clear();

                // add normal bitmap first
                ThumbnailItem thumbnailItem = new ThumbnailItem();
                thumbnailItem.image = thumbImage;
                thumbnailItem.filterName = getString(R.string.filter_normal);
                ThumbnailsManager.addThumb(thumbnailItem);

                List<Filter> filters = FilterPack.getFilterPack(getActivity());

                for (Filter filter : filters) {
                    ThumbnailItem tI = new ThumbnailItem();
                    tI.image = thumbImage;
                    tI.filter = filter;
                    tI.filterName = filter.getName();
                    ThumbnailsManager.addThumb(tI);
                }

                thumbnailItemList.addAll(ThumbnailsManager.processThumbs(getActivity()));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        };

        new Thread(r).start();
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

    @Override
    public void onFilterSelected(Filter filter) {
        onToolImageSelectedListener.onFilterClick(filter);
        Log.d("TAGGG", "filter");
    }
    private void setMainFragmentByPreset(CropDemoPreset demoPreset) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, MainCropFragment.newInstance(demoPreset))
                .commit();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgCrop:
                // start picker to get image for cropping and then use the image in cropping activity
//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(this);

//                setMainFragmentByPreset(CropDemoPreset.RECT);
                cropImageDialogFragment = CropImageDialogFragment.newInstance(CropDemoPreset.RECT);
                cropImageDialogFragment.setBitmap(currentBitmap);
                cropImageDialogFragment.setOnCropListener(new CropImageDialogFragment.OnCropListener() {
                    @Override
                    public void onCropSuccess(Bitmap bitmap) {
                        onToolImageSelectedListener.onCrop(bitmap);
                    }
                });
                cropImageDialogFragment.show(getActivity().getSupportFragmentManager(), cropImageDialogFragment.getTag());
                break;
            case R.id.imgRotation:
                break;
            case R.id.imgDuplicate:
                break;
            case R.id.imgBringToFront:
                break;
        }
    }

//    private void setMainFragmentByPreset(CropDemoPreset demoPreset) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager
//                .beginTransaction()
//                .replace(R.id.container, MainCropFragment.newInstance(demoPreset))
//                .commit();
//    }

    public interface OnToolImageSelectedListener {
        public void onAdjustClick();

        public void onOpacityClick();

        public void onBlendingClick();

        public void onFilterClick(Filter filter);

        public void onFrameClick();

        public void onCrop(Bitmap bitmap);
    }
}
