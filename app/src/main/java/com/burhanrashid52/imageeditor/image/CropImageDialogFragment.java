package com.burhanrashid52.imageeditor.image;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.burhanrashid52.imageeditor.ColorPickerAdapter;
import com.burhanrashid52.imageeditor.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CropImageDialogFragment extends BottomSheetDialogFragment implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnCropImageCompleteListener, View.OnClickListener {
    // region: Fields and Consts

    private CropDemoPreset mDemoPreset;

    private CropImageView mCropImageView;

    private ImageView rotation;
    private ImageView flipHorizontal;
    private ImageView flipVertical;
    private TextView crop;

    OnCropListener onCropListener;

    public CropImageDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static CropImageDialogFragment newInstance(CropDemoPreset demoPreset) {
        CropImageDialogFragment fragment = new CropImageDialogFragment();
        Bundle args = new Bundle();
        args.putString("DEMO_PRESET", demoPreset.name());
        fragment.setArguments(args);
        return fragment;
    }

    Bitmap currentBitmap;
    public void setBitmap(Bitmap bitmap) {
        currentBitmap = bitmap;
    }

    public void setOnCropListener(OnCropListener onCropListener) {
        this.onCropListener = onCropListener;
    }

    /**
     * Set the options of the crop image view to the given values.
     */
    public void setCropImageViewOptions(CropImageViewOptions options) {
        mCropImageView.setScaleType(options.scaleType);
        mCropImageView.setCropShape(options.cropShape);
        mCropImageView.setGuidelines(options.guidelines);
        mCropImageView.setAspectRatio(options.aspectRatio.first, options.aspectRatio.second);
        mCropImageView.setFixedAspectRatio(options.fixAspectRatio);
        mCropImageView.setMultiTouchEnabled(options.multitouch);
        mCropImageView.setShowCropOverlay(options.showCropOverlay);
        mCropImageView.setShowProgressBar(options.showProgressBar);
        mCropImageView.setAutoZoomEnabled(options.autoZoomEnabled);
        mCropImageView.setMaxZoom(options.maxZoomLevel);
        mCropImageView.setFlippedHorizontally(options.flipHorizontally);
        mCropImageView.setFlippedVertically(options.flipVertically);
    }


    /**
     * Set the initial rectangle to use.
     */
    public void setInitialCropRect() {
        mCropImageView.setCropRect(new Rect(100, 300, 500, 1200));
    }

    /**
     * Reset crop window to initial rectangle.
     */
    public void resetCropRect() {
        mCropImageView.resetCropRect();
    }

    public void updateCurrentCropViewOptions() {
        CropImageViewOptions options = new CropImageViewOptions();
        options.scaleType = mCropImageView.getScaleType();
        options.cropShape = mCropImageView.getCropShape();
        options.guidelines = mCropImageView.getGuidelines();
        options.aspectRatio = mCropImageView.getAspectRatio();
        options.fixAspectRatio = mCropImageView.isFixAspectRatio();
        options.showCropOverlay = mCropImageView.isShowCropOverlay();
        options.showProgressBar = mCropImageView.isShowProgressBar();
        options.autoZoomEnabled = mCropImageView.isAutoZoomEnabled();
        options.maxZoomLevel = mCropImageView.getMaxZoom();
        options.flipHorizontally = mCropImageView.isFlippedHorizontally();
        options.flipVertically = mCropImageView.isFlippedVertically();
//        ((MainCropActivity) getActivity()).setCurrentOptions(options);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_crop_image_dialog, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mCropImageView = view.findViewById(R.id.cropImageView);
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnCropImageCompleteListener(this);

        updateCurrentCropViewOptions();

        mCropImageView.setImageBitmap(currentBitmap);
//        if (savedInstanceState == null) {
//            if (mDemoPreset == CropDemoPreset.SCALE_CENTER_INSIDE) {
//                mCropImageView.setImageResource(R.drawable.cat_small);
//            } else {
//                mCropImageView.setImageResource(R.drawable.cat_small);
//            }
//        }
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(View rootView) {
        rotation = (ImageView) rootView.findViewById(R.id.rotation);
        flipHorizontal = (ImageView) rootView.findViewById(R.id.flipHorizontal);
        flipVertical = (ImageView) rootView.findViewById(R.id.flipVertical);
        crop = (TextView) rootView.findViewById(R.id.crop);

        rotation.setOnClickListener(this);
        flipHorizontal.setOnClickListener(this);
        flipVertical.setOnClickListener(this);
        crop.setOnClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mDemoPreset = CropDemoPreset.valueOf(getArguments().getString("DEMO_PRESET"));
//    ((MainCropActivity) activity).setCurrentFragment(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCropImageView != null) {
            mCropImageView.setOnSetImageUriCompleteListener(null);
            mCropImageView.setOnCropImageCompleteListener(null);
        }
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            Toast.makeText(getActivity(), "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("AIC", "Failed to load image by URI", error);
            Toast.makeText(getActivity(), "Image load failed: " + error.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        handleCropResult(result);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            handleCropResult(result);
        }
    }

    private void handleCropResult(CropImageView.CropResult result) {

        Bitmap bitmap = mCropImageView.getCropShape() == CropImageView.CropShape.OVAL
                ? CropImage.toOvalBitmap(result.getBitmap())
                : result.getBitmap();
//        Bitmap bitmap = result.getBitmap();
//        mCropImageView.setImageBitmap(bitmap);
        onCropListener.onCropSuccess(bitmap);
        dismiss();
//
//        if (result.getError() == null) {
//            Intent intent = new Intent(getActivity(), CropResultActivity.class);
//            intent.putExtra("SAMPLE_SIZE", result.getSampleSize());
//            if (result.getUri() != null) {
//                intent.putExtra("URI", result.getUri());
//            } else {
//                CropResultActivity.mImage =
//                        mCropImageView.getCropShape() == CropImageView.CropShape.OVAL
//                                ? CropImage.toOvalBitmap(result.getBitmap())
//                                : result.getBitmap();
//            }
//            startActivity(intent);
//        } else {
//            Log.e("AIC", "Failed to crop image", result.getError());
//            Toast.makeText(
//                    getActivity(),
//                    "Image crop failed: " + result.getError().getMessage(),
//                    Toast.LENGTH_LONG)
//                    .show();
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rotation:
                mCropImageView.rotateImage(90);
                break;
            case R.id.flipHorizontal:
                mCropImageView.flipImageHorizontally();
                break;
            case R.id.flipVertical:
                mCropImageView.flipImageVertically();
                break;
            case R.id.crop:
                mCropImageView.getCroppedImageAsync();
                break;
        }
    }

    public interface OnCropListener {
        void onCropSuccess(Bitmap bitmap);
    }
}