package com.burhanrashid52.imageeditor;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.burhanrashid52.imageeditor.color.ColorPickerDialogFragment;
import com.burhanrashid52.imageeditor.color.palettes.ArrayPalette;
import com.burhanrashid52.imageeditor.color.palettes.ColorFactory;
import com.burhanrashid52.imageeditor.color.palettes.ColorShadeFactory;
import com.burhanrashid52.imageeditor.color.palettes.CombinedColorFactory;
import com.burhanrashid52.imageeditor.color.palettes.FactoryPalette;
import com.burhanrashid52.imageeditor.color.palettes.Palette;
import com.burhanrashid52.imageeditor.color.palettes.RainbowColorFactory;
import com.burhanrashid52.imageeditor.color.palettes.RandomPalette;
import com.burhanrashid52.imageeditor.constants.Contants;
import com.burhanrashid52.imageeditor.outline.CompatOutlinedTextView;

import org.dmfs.android.retentionmagic.annotations.Retain;

import java.util.ArrayList;

/**
 * Created by Burhanuddin Rashid on 1/16/2018.
 */

public class TextEditorDialogFragment extends DialogFragment implements EditViewAdapter.EditViewListener, SeekBar.OnSeekBarChangeListener, ColorPickerDialogFragment.ColorDialogResultListener {

    public static final String TAG = TextEditorDialogFragment.class.getSimpleName();
    public static final String EXTRA_INPUT_TEXT = "extra_input_text";
    public static final String EXTRA_COLOR_CODE = "extra_color_code";
    public static final String EXTRA_SIZE_CODE = "extra_size_code";
    private EditText mAddTextEditText;
    private TextView mAddTextDoneTextView;
    private TextView tvPhotoEditorTextPreview;

    private InputMethodManager mInputMethodManager;
    private int mColorCode;
    private int mSizeCode = 40;
    private TextEditor mTextEditor;
    private static Typeface mTypeface;
    private EditViewAdapter mEditViewAdapter;

    RecyclerView addTextColorPickerRecyclerView;
    RecyclerView addRecyclerView;
    RecyclerView addTextFontsRecyclerView;
    RecyclerView shadowRecyclerView;
    RecyclerView strokeRecyclerView;

    private SeekBar mSeekBarShadow;
    private SeekBar sbBlurSize;
    private SeekBar sbLetterSpacing;
    private SeekBar sbLineSpacing;

    LinearLayout shadowContainer;
    LinearLayout containerTextSize;
    LinearLayout containerSpacing;


    SeekBar sbBrushSize;

    ImageView imgClose;
    ImageView imgMore;

    RadioGroup rdGroupBlur;
    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet = new ConstraintSet();

    private boolean mIsColor;
    private boolean mIsFonts;
    private boolean mIsSizes;
    private boolean mIsShadow;
    private boolean mIsStroke;
    private boolean mIsBlur;
    private boolean mIsSpacing;

    private int currentSeekBar = 30;
    private int currentColorShadow;
    private int currentSeekBarBlur = 25;
    private BlurMaskFilter.Blur currentBlur = BlurMaskFilter.Blur.NORMAL;

    private final static int[] COLORS = new int[]{
            0xff000000, 0xff0000ff, 0xff00ff00, 0xffff0000, 0xffffff00, 0xff00ffff, 0xffff00ff, 0xff404040,
            0xff808080, 0xff8080ff, 0xff80ff80, 0xffff8080, 0xffffff80, 0xff80ffff, 0xffff80ff, 0xffffffff};

    private final static int[] MATERIAL_COLORS_PRIMARY = {
            0xffe91e63, 0xfff44336, 0xffff5722, 0xffff9800, 0xffffc107, 0xffffeb3b, 0xffcddc39, 0xff8bc34a,
            0xff4caf50, 0xff009688, 0xff00bcd4, 0xff03a9f4, 0xff2196f3, 0xff3f51b5, 0xff673ab7, 0xff9c27b0};

    private static final int MATERIAL_COLORS_SECONDARY[] = {
            0xffad1457, 0xffc62828, 0xffd84315, 0xffef6c00, 0xffff8f00, 0xfff9a825, 0xff9e9d24, 0xff558b2f,
            0xff2e7d32, 0xff00695c, 0xff00838f, 0xff0277bd, 0xff1565c0, 0xff283593, 0xff4527a0, 0xff6a1b9a};

    @Retain(permanent = true, classNS = "DemoActivity")
    private String mSelectedPalette = null;

    public interface TextEditor {
        void onDone(String inputText, int colorCode);

        void onDone(Typeface typeface, String inputText, int colorCode, int textSize);
    }


    //Show dialog with provide text and text color
    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity,
                                                @NonNull String inputText,
                                                @ColorInt int colorCode) {
        Bundle args = new Bundle();
        args.putString(EXTRA_INPUT_TEXT, inputText);
        args.putInt(EXTRA_COLOR_CODE, colorCode);
        args.putInt(EXTRA_SIZE_CODE, 40);
        TextEditorDialogFragment fragment = new TextEditorDialogFragment();
        fragment.setArguments(args);
        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);

        mTypeface = Typeface.createFromAsset(appCompatActivity.getAssets(),
                Contants.folderFontPath + "AbrilFatface-Regular.ttf");

        return fragment;
    }

    //Show dialog with provide text and text color, typeFace
    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity,
                                                @NonNull String inputText,
                                                @ColorInt int colorCode, Typeface typeface, int textSize) {
        Bundle args = new Bundle();
        String type = typeface.toString();
        Log.d("TAGGG", type);
        args.putString(EXTRA_INPUT_TEXT, inputText);
        args.putInt(EXTRA_COLOR_CODE, colorCode);
        args.putInt(EXTRA_SIZE_CODE, textSize);

        TextEditorDialogFragment fragment = new TextEditorDialogFragment();
        fragment.setArguments(args);
        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);

        if (typeface != null) {
            mTypeface = typeface;
        }
//        mTypeface = Typeface.createFromAsset(appCompatActivity.getAssets(),
//                "fonts/" + "Amatic-Bold.ttf");

        return fragment;
    }

    //Show dialog with default text input as empty and text color white
    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity) {
        return show(appCompatActivity,
                "", ContextCompat.getColor(appCompatActivity, R.color.white));
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        //Make dialog full screen with transparent background
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_text_dialog_test, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootView = view.findViewById(R.id.rootView);

        mAddTextEditText = view.findViewById(R.id.add_text_edit_text);
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mAddTextDoneTextView = view.findViewById(R.id.add_text_done_tv);
        tvPhotoEditorTextPreview = view.findViewById(R.id.tvPhotoEditorfffTextPreview);


        sbBrushSize = view.findViewById(R.id.sbSize);
        containerTextSize = view.findViewById(R.id.containerTextSize);
        containerSpacing = view.findViewById(R.id.containerSpacing);


        imgClose = view.findViewById(R.id.imgClose);

        mSeekBarShadow = view.findViewById(R.id.sbSizeShadow);
        sbBlurSize = view.findViewById(R.id.sbBlurSize);
        sbLetterSpacing = view.findViewById(R.id.sbLetterSpacing);
        sbLineSpacing = view.findViewById(R.id.sbLineSpacing);

        shadowContainer = view.findViewById(R.id.container_add_shadow_recycler_view);
        containerBlur = view.findViewById(R.id.containerBlur);

        rdGroupBlur = view.findViewById(R.id.rdGroupBlur);

        //Setup the color picker for text color
        addTextColorPickerRecyclerView = view.findViewById(R.id.add_text_color_picker_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        addTextColorPickerRecyclerView.setLayoutManager(layoutManager);
        addTextColorPickerRecyclerView.setHasFixedSize(true);
        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(getActivity());
        //This listener will change the text color when clicked on any color from picker
        colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
            @Override
            public void onColorPickerClickListener(int colorCode) {
                mColorCode = colorCode;
                mAddTextEditText.setTextColor(colorCode);
            }
        });
        addTextColorPickerRecyclerView.setAdapter(colorPickerAdapter);
        mAddTextEditText.setText(getArguments().getString(EXTRA_INPUT_TEXT));
        mColorCode = getArguments().getInt(EXTRA_COLOR_CODE);
        mSizeCode = getArguments().getInt(EXTRA_SIZE_CODE);
        mAddTextEditText.setTextColor(mColorCode);
        mAddTextEditText.setTypeface(mTypeface);
        if (mSizeCode != 0) {
            mAddTextEditText.setTextSize(mSizeCode);
        } else {
            mAddTextEditText.setTextSize(40);
        }
//        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        //Make a callback on activity when user is done with text editing
        mAddTextDoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TABBB", "done");
                mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                dismiss();
                String inputText = mAddTextEditText.getText().toString();
                if (!TextUtils.isEmpty(inputText) && mTextEditor != null) {
                    mTextEditor.onDone(mTypeface, inputText, mColorCode, mSizeCode);
                }
            }
        });

        //Setup the color picker for text color
        addRecyclerView = view.findViewById(R.id.add_picker_recycler_view);
        LinearLayoutManager layoutEditManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        addRecyclerView.setLayoutManager(layoutEditManager);
        addRecyclerView.setHasFixedSize(true);
        mEditViewAdapter = new EditViewAdapter(getActivity(), this);
        //This listener will change the text fonts when clicked on any fonts from picker
        addRecyclerView.setAdapter(mEditViewAdapter);


        //Setup the color picker for text color
        shadowRecyclerView = view.findViewById(R.id.add_shadow_recycler_view);
        LinearLayoutManager layoutShadowEditManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        shadowRecyclerView.setLayoutManager(layoutShadowEditManager);
        shadowRecyclerView.setHasFixedSize(true);
        ShadowPickerAdapter shadowPickerAdapter = new ShadowPickerAdapter(getActivity());
        shadowPickerAdapter.setOnShadowPickerClickListener(new ShadowPickerAdapter.OnShadowPickerClickListener() {
            @Override
            public void onShadowPickerClickListener(int colorCode) {
                //
                currentColorShadow = colorCode;
                mAddTextEditText.setShadowLayer(currentSeekBar, 0, 0, currentColorShadow);
            }
        });
        //This listener will change the text fonts when clicked on any fonts from picker
        shadowRecyclerView.setAdapter(shadowPickerAdapter);

        mSeekBarShadow.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentSeekBar = i;
                mAddTextEditText.setShadowLayer(currentSeekBar, 0, 0, currentColorShadow);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Setup the color picker for text color
        addTextFontsRecyclerView = view.findViewById(R.id.add_text_fonts_recycler_view);
        LinearLayoutManager layoutFontsManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        addTextFontsRecyclerView.setLayoutManager(layoutFontsManager);
        addTextFontsRecyclerView.setHasFixedSize(true);
        FontsAdapter fontsAdapter = new FontsAdapter(getActivity());
        //This listener will change the text fonts when clicked on any fonts from picker
        fontsAdapter.setFontsPickerListener(new FontsAdapter.OnTextPickerClickListener() {
            @Override
            public void onTextPickerClickListener(String textContent) {
                mTypeface = Typeface.createFromAsset(getActivity().getAssets(),
                        Contants.folderFontPath + textContent);
                mAddTextEditText.setTypeface(mTypeface);
            }
        });
//        colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
//            @Override
//            public void onColorPickerClickListener(int colorCode) {
//                mColorCode = colorCode;
//                mAddTextEditText.setTextColor(colorCode);
//            }
//        });
        addTextFontsRecyclerView.setAdapter(fontsAdapter);

        //Setup the color picker for text color
        strokeRecyclerView = view.findViewById(R.id.add_stroke_recycler_view);
        LinearLayoutManager layoutStrokeManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        strokeRecyclerView.setLayoutManager(layoutStrokeManager);
        strokeRecyclerView.setHasFixedSize(true);
        StrokeColorPickerAdapter strokeAdapter = new StrokeColorPickerAdapter(getActivity());
        //This listener will change the text color when clicked on any color from picker
        strokeAdapter.setOnStrokeColorPickerClickListener(new StrokeColorPickerAdapter.OnStrokeColorPickerClickListener() {
            @Override
            public void onStrokeColorPickerClickListener(final int colorCode) {
//                mColorCode = colorCode;
//                mAddTextEditText.setTextColor(colorCode);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        tvPhotoEditorTextPreview.setStrokeWidth(10);
//                        tvPhotoEditorTextPreview.setStrokeColor(Color.RED);
//                        applyBlurMaskFilter(mAddTextEditText, BlurMaskFilter.Blur.OUTER);
                    }
                });
            }
        });

        strokeRecyclerView.setAdapter(strokeAdapter);

        sbBrushSize.setOnSeekBarChangeListener(this);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgMore.setVisibility(View.GONE);
                if (mIsFonts) {
                    showFontsText(false);
                } else if (mIsSizes) {
                    showSizeText(false);
                } else if (mIsShadow) {
                    showShadowText(false);
                } else if (mIsColor) {
                    showColorText(false);
                } else if (mIsStroke) {
                    showStrokeText(false);
                } else if (mIsBlur) {
                    showBlueText(false);
                } else if (mIsSpacing) {
                    showSpacingText(false);
                } else {
                    dismiss();
                }
            }
        });

        imgMore = view.findViewById(R.id.imgMore);
        imgMore.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                onClickColor();
            }
        });

        rdGroupBlur.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rbNoBlur:
                        // If no blur is checked
                        // Set the TextView layer type
                        mAddTextEditText.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        // Clear any previous MaskFilter
                        mAddTextEditText.getPaint().setMaskFilter(null);
                        break;
                    case R.id.rbNormalBlur:
                        currentBlur = BlurMaskFilter.Blur.NORMAL;
                        applyBlurMaskFilter(mAddTextEditText, BlurMaskFilter.Blur.NORMAL, currentSeekBarBlur);
                        break;
                    case R.id.rbInnerBlur:
                        currentBlur = BlurMaskFilter.Blur.INNER;
                        applyBlurMaskFilter(mAddTextEditText, BlurMaskFilter.Blur.INNER, currentSeekBarBlur);
                        break;
                    case R.id.rbOuterBlur:
                        currentBlur = BlurMaskFilter.Blur.OUTER;
                        applyBlurMaskFilter(mAddTextEditText, BlurMaskFilter.Blur.OUTER, currentSeekBarBlur);
                        break;
                    case R.id.rbSolidBlur:
                        currentBlur = BlurMaskFilter.Blur.SOLID;
                        applyBlurMaskFilter(mAddTextEditText, BlurMaskFilter.Blur.SOLID, currentSeekBarBlur);
                        break;
                }

            }
        });

        sbBlurSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentSeekBarBlur = i;
                applyBlurMaskFilter(mAddTextEditText, currentBlur, currentSeekBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbLetterSpacing.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mAddTextEditText.setLetterSpacing(i / 30);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbLineSpacing.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mAddTextEditText.setLineSpacing(0, i / 30);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


    //Callback to listener if user is done with text editing
    public void setOnTextEditorListener(TextEditor textEditor) {
        mTextEditor = textEditor;
    }

    void showColorText(boolean isVisible) {
        mIsColor = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(addTextColorPickerRecyclerView.getId(), ConstraintSet.START);
            mConstraintSet.connect(addTextColorPickerRecyclerView.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(addTextColorPickerRecyclerView.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(addTextColorPickerRecyclerView.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(addTextColorPickerRecyclerView.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    void showShadowText(boolean isVisible) {
        mIsShadow = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(shadowContainer.getId(), ConstraintSet.START);
            mConstraintSet.connect(shadowContainer.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(shadowContainer.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(shadowContainer.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(shadowContainer.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    void showFontsText(boolean isVisible) {
        mIsFonts = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(addTextFontsRecyclerView.getId(), ConstraintSet.START);
            mConstraintSet.connect(addTextFontsRecyclerView.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(addTextFontsRecyclerView.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(addTextFontsRecyclerView.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(addTextFontsRecyclerView.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    void showSizeText(boolean isVisible) {
        mIsSizes = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(containerTextSize.getId(), ConstraintSet.START);
            mConstraintSet.connect(containerTextSize.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(containerTextSize.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(containerTextSize.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(containerTextSize.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    LinearLayout containerBlur;

    void showStrokeText(boolean isVisible) {
        mIsStroke = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(strokeRecyclerView.getId(), ConstraintSet.START);
            mConstraintSet.connect(strokeRecyclerView.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(strokeRecyclerView.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(strokeRecyclerView.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(strokeRecyclerView.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    void showBlueText(boolean isVisible) {
        mIsBlur = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(containerBlur.getId(), ConstraintSet.START);
            mConstraintSet.connect(containerBlur.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(containerBlur.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(containerBlur.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(containerBlur.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    void showSpacingText(boolean isVisible) {
        mIsSpacing = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(containerSpacing.getId(), ConstraintSet.START);
            mConstraintSet.connect(containerSpacing.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(containerSpacing.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(containerSpacing.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(containerSpacing.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }


    @Override
    public void onFormatListener() {
        Log.d("TAGGG", "onFormatListener");
//        showColorText(true);
        showFontsText(true);
    }

    @Override
    public void onFontListener() {
        showFontsText(true);
        Log.d("TAGGG", "onFontListener");
    }

    @Override
    public void onTextSizeListener() {
        Log.d("TAGGG", "onTextSizeListener");
//        showColorText(true);
        imgMore.setVisibility(View.VISIBLE);
        showSizeText(true);
    }

    @Override
    public void onColorListener() {
        Log.d("TAGGG", "onColorListener");
        imgMore.setVisibility(View.VISIBLE);
        showColorText(true);
    }

    @Override
    public void onShadowListener() {
        Log.d("TAGGG", "onShadowListener");
        imgMore.setVisibility(View.VISIBLE);
        showShadowText(true);
    }

    @Override
    public void onStrokeListener() {
        imgMore.setVisibility(View.VISIBLE);
        showStrokeText(true);
        Log.d("TAGGG", "onStrokeListener");
    }

    @Override
    public void onHighLightListener() {
        imgMore.setVisibility(View.VISIBLE);
        Log.d("TAGGG", "onHighLightListener");
    }

    @Override
    public void onSpacingListener() {
        imgMore.setVisibility(View.VISIBLE);
        showSpacingText(true);
        Log.d("TAGGG", "onSpacingListener");
    }

    @Override
    public void onBlurListener() {
        imgMore.setVisibility(View.VISIBLE);
        showBlueText(true);
        Log.d("TAGGG", "onBlurListener");
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.sbSize:
                mSizeCode = i;
                mAddTextEditText.setTextSize(i);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void onClickColor() {
        ColorPickerDialogFragment d = new ColorPickerDialogFragment();

        ArrayList<Palette> palettes = new ArrayList<Palette>();

        palettes.add(new ArrayPalette("material_primary", "Material Colors", MATERIAL_COLORS_PRIMARY, 4));
        palettes.add(new ArrayPalette("material_secondary", "Dark Material Colors", MATERIAL_COLORS_SECONDARY, 4));

        // add a palette from the resources
        palettes.add(ArrayPalette.fromResources(getActivity().getApplicationContext(), "base", R.string.base_palette_name, R.array.base_palette_colors, R.array.base_palette_color_names));

        palettes.add(new ArrayPalette("base2", "Base 2", COLORS));

        // Android Material color schema
        // Add a palette with rainbow colors
        palettes.add(new FactoryPalette("rainbow", "Rainbow", ColorFactory.RAINBOW, 16));

        // Add a palette with many darker rainbow colors
        palettes.add(new FactoryPalette("rainbow2", "Dirty Rainbow", new RainbowColorFactory(0.5f, 0.5f), 16));

        // Add a palette with pastel colors
        palettes.add(new FactoryPalette("pastel", "Pastel", ColorFactory.PASTEL, 16));

        // Add a palette with red+orange colors
        palettes.add(new FactoryPalette("red/orange", "Red/Orange", new CombinedColorFactory(ColorFactory.RED, ColorFactory.ORANGE), 16));

        // Add a palette with yellow+green colors
        palettes.add(new FactoryPalette("yellow/green", "Yellow/Green", new CombinedColorFactory(ColorFactory.YELLOW, ColorFactory.GREEN), 16));

        // Add a palette with cyan+blue colors
        palettes.add(new FactoryPalette("cyan/blue", "Cyan/Blue", new CombinedColorFactory(ColorFactory.CYAN, ColorFactory.BLUE), 16));

        // Add a palette with purple+pink colors
        palettes.add(new FactoryPalette("purble/pink", "Purple/Pink", new CombinedColorFactory(ColorFactory.PURPLE, ColorFactory.PINK), 16));

        // Add a palette with red colors
        palettes.add(new FactoryPalette("red", "Red", ColorFactory.RED, 16));
        // Add a palette with green colors
        palettes.add(new FactoryPalette("green", "Green", ColorFactory.GREEN, 16));
        // Add a palette with blue colors
        palettes.add(new FactoryPalette("blue", "Blue", ColorFactory.BLUE, 16));

        // Add a palette with few random colors
        palettes.add(new RandomPalette("random1", "Random 1", 1));
        // Add a palette with few random colors
        palettes.add(new RandomPalette("random4", "Random 4", 4));
        // Add a palette with few random colors
        palettes.add(new RandomPalette("random9", "Random 9", 9));
        // Add a palette with few random colors
        palettes.add(new RandomPalette("random16", "Random 16", 16));

        // Add a palette with random colors
        palettes.add(new RandomPalette("random25", "Random 25", 25));

        // Add a palette with many random colors
        palettes.add(new RandomPalette("random81", "Random 81", 81));

        // Add a palette with secondary colors
        palettes.add(new FactoryPalette("secondary1", "Secondary 1", new CombinedColorFactory(new ColorShadeFactory(18),
                new ColorShadeFactory(53), new ColorShadeFactory(80), new ColorShadeFactory(140)), 16, 4));

        // Add another palette with secondary colors
        palettes.add(new FactoryPalette("secondary2", "Secondary 2", new CombinedColorFactory(new ColorShadeFactory(210),
                new ColorShadeFactory(265), new ColorShadeFactory(300), new ColorShadeFactory(340)), 16, 4));

        // set the palettes
        d.setPalettes(palettes.toArray(new Palette[palettes.size()]));

        // set the initial palette
        d.selectPaletteId(mSelectedPalette);

        // show the fragment
        d.show(getActivity().getSupportFragmentManager(), "");
    }


    @Override
    public void onColorChanged(int color, String paletteId, String colorName, String paletteName) {
        mSelectedPalette = paletteId;
    }


    @Override
    public void onColorDialogCancelled() {
    }

    // Custom method to apply BlurMaskFilter to a TextView text
    protected void applyBlurMaskFilter(EditText tv, BlurMaskFilter.Blur style, int radiusOrg) {
        /*
            MaskFilter
                Known Direct Subclasses
                    BlurMaskFilter, EmbossMaskFilter

                MaskFilter is the base class for object that perform transformations on an
                alpha-channel mask before drawing it. A subclass of MaskFilter may be installed
                into a Paint. Blur and emboss are implemented as subclasses of MaskFilter.

        */
        /*
            BlurMaskFilter
                This takes a mask, and blurs its edge by the specified radius. Whether or or not to
                include the original mask, and whether the blur goes outside, inside, or straddles,
                the original mask's border, is controlled by the Blur enum.
        */
        /*
            public BlurMaskFilter (float radius, BlurMaskFilter.Blur style)
                Create a blur maskfilter.

            Parameters
                radius : The radius to extend the blur from the original mask. Must be > 0.
                style : The Blur to use
            Returns
                The new blur maskfilter
        */
        /*
            BlurMaskFilter.Blur
                INNER : Blur inside the border, draw nothing outside.
                NORMAL : Blur inside and outside the original border.
                OUTER : Draw nothing inside the border, blur outside.
                SOLID : Draw solid inside the border, blur outside.
        */
        /*
            public float getTextSize ()
                Returns the size (in pixels) of the default text size in this TextView.
        */

        // Define the blur effect radius
        float radius = radiusOrg / 5;


        // Initialize a new BlurMaskFilter instance
        BlurMaskFilter filter = new BlurMaskFilter(radius, style);

        /*
            public void setLayerType (int layerType, Paint paint)
                Specifies the type of layer backing this view. The layer can be LAYER_TYPE_NONE,
                LAYER_TYPE_SOFTWARE or LAYER_TYPE_HARDWARE.

                A layer is associated with an optional Paint instance that controls how the
                layer is composed on screen.

            Parameters
                layerType : The type of layer to use with this view, must be one of
                    LAYER_TYPE_NONE, LAYER_TYPE_SOFTWARE or LAYER_TYPE_HARDWARE
                paint : The paint used to compose the layer. This argument is optional and
                    can be null. It is ignored when the layer type is LAYER_TYPE_NONE
        */
        /*
            public static final int LAYER_TYPE_SOFTWARE
                Indicates that the view has a software layer. A software layer is backed by
                a bitmap and causes the view to be rendered using Android's software rendering
                pipeline, even if hardware acceleration is enabled.
        */

        // Set the TextView layer type
        tv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        /*
            public MaskFilter setMaskFilter (MaskFilter maskfilter)
                Set or clear the maskfilter object.

                Pass null to clear any previous maskfilter. As a convenience, the parameter
                passed is also returned.

            Parameters
                maskfilter : May be null. The maskfilter to be installed in the paint
            Returns
                maskfilter
        */

        // Finally, apply the blur effect on TextView text
        tv.getPaint().setMaskFilter(filter);
    }

}
