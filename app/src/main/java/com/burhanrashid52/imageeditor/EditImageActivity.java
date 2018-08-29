package com.burhanrashid52.imageeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.burhanrashid52.imageeditor.base.BaseActivity;
import com.burhanrashid52.imageeditor.edit.image.FiltersListFragment;
import com.burhanrashid52.imageeditor.emoji.EmojiAdapters;
import com.burhanrashid52.imageeditor.filters.FilterListener;
import com.burhanrashid52.imageeditor.filters.FilterViewAdapter;
import com.burhanrashid52.imageeditor.image.ImageEditorDialogFragment;
import com.burhanrashid52.imageeditor.stickers.StickerAdapters;
import com.burhanrashid52.imageeditor.stickers.StickerBSFragment;
import com.burhanrashid52.imageeditor.tools.EditingToolsAdapter;
import com.burhanrashid52.imageeditor.tools.ToolType;
import com.burhanrashid52.imageeditor.utils.BitmapUtils;
import com.burhanrashid52.imageeditor.views.OnPhotoEditorListener;
import com.burhanrashid52.imageeditor.views.PhotoEditor;
import com.burhanrashid52.imageeditor.views.PhotoEditorView;
import com.burhanrashid52.imageeditor.views.SaveSettings;
import com.burhanrashid52.imageeditor.views.ViewType;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.burhanrashid52.imageeditor.views.PhotoFilter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zomato.photofilters.imageprocessors.Filter;

import static com.burhanrashid52.imageeditor.edit.image.FiltersListFragment.IMAGE_NAME;

public class EditImageActivity extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected, FilterListener {

    private static final String TAG = EditImageActivity.class.getSimpleName();
    public static final String EXTRA_IMAGE_PATHS = "extra_image_paths";
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    private PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private TextView mTxtCurrentTool;
    private Typeface mWonderFont;
    private RecyclerView mRvTools;
    private RecyclerView mRvFilters;
    private RecyclerView mRvSticker;
    private RecyclerView mRvEmoji;
    private RecyclerView mRvFrame;
    private ImageView imgOpen;


    private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private StickerAdapters mStickerViewAdapter = new StickerAdapters(this);
    private StickerAdapters mFrameViewAdapter = new StickerAdapters(this);
    private EmojiAdapters mEmojiViewAdapter;
    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible;
    private boolean mIsStickerVisible;
    private boolean mIsEmojiVisible;
    private String key = "1";


    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_edit_image_test_ui);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if (getIntent().getStringExtra(StartActivity.KEY_TYPE) != null) {
            key = getIntent().getStringExtra(StartActivity.KEY_TYPE);
        }

        mEmojiViewAdapter = new EmojiAdapters(EditImageActivity.this, this);
        initViews();

        mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf");

        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);

        LinearLayoutManager llmStickers = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvSticker.setLayoutManager(llmStickers);
        mRvSticker.setAdapter(mStickerViewAdapter);

        LinearLayoutManager llmEmojis = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvEmoji.setLayoutManager(llmEmojis);
        mRvEmoji.setAdapter(mEmojiViewAdapter);


        LinearLayoutManager llmFrame = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFrame.setLayoutManager(llmFrame);
        mRvFrame.setAdapter(mFrameViewAdapter);


        //Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        //Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

        loadType();
    }

    private void loadType() {
        switch (key) {
            case "1":
                Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.got_s);
                Bitmap imageScale = Bitmap.createScaledBitmap(image, 300, 300, false);
                mPhotoEditor.addImage(imageScale);
                break;
            case "2":
                mPhotoEditorView.getSource().setImageResource(R.drawable.got);
                break;
            case "3":
                break;
            default:
                break;
        }

//        // Set Image Dynamically
//        mPhotoEditorView.getSource().setImageResource(R.drawable.color_palette);
    }

    private void initViews() {
        ImageView imgUndo;
        ImageView imgRedo;
        ImageView imgCamera;
        ImageView imgGallery;
        ImageView imgSave;
        ImageView imgClose;

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mRvTools = findViewById(R.id.rvConstraintTools);
        mRvFilters = findViewById(R.id.rvFilterView);
        mRvSticker = findViewById(R.id.rvSticker);
        mRvEmoji = findViewById(R.id.rvEmoji);
        mRvFrame = findViewById(R.id.rvFrame);
        mRootView = findViewById(R.id.rootView);

        imgUndo = findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(this);

        imgRedo = findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(this);

        imgCamera = findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);

        imgGallery = findViewById(R.id.imgGallery);
        imgGallery.setOnClickListener(this);

        imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);

        imgOpen = findViewById(R.id.imgOpen);
        imgOpen.setOnClickListener(this);

    }
    Bitmap originalImage;
    Bitmap filteredImage;

    // load the default image from assets on app launch
    private void loadImage() {
        originalImage = BitmapUtils.getBitmapFromAssets(this, IMAGE_NAME, 300, 300);
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    public void onImageChangeListener(final View rootView, final Bitmap bitmap) {

        ImageEditorDialogFragment editorDialogFragment = ImageEditorDialogFragment.getInstance();
        editorDialogFragment.setBitmap(bitmap);
        editorDialogFragment.setOnToolImageSelectedListener(new ImageEditorDialogFragment.OnToolImageSelectedListener() {
            @Override
            public void onAdjustClick() {

            }

            @Override
            public void onOpacityClick() {

            }

            @Override
            public void onBlendingClick() {

            }

            @Override
            public void onFilterClick(Filter filter) {
                filteredImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                mPhotoEditor.addImageTemp(rootView, filter.processFilter(filteredImage));
                // preview filtered image
//        imagePreview.setBitmapBackground(filter.processFilter(filteredImage), true);
//                .setImageBitmap(filter.processFilter(filteredImage));

            }

            @Override
            public void onFrameClick() {

            }

            @Override
            public void onCrop(Bitmap bitmap) {
                mPhotoEditor.addImageTemp(rootView, bitmap);
            }
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTrasaction = fragmentManager.beginTransaction();
        beginTrasaction.replace(R.id.frContainer, editorDialogFragment);
        beginTrasaction.commit();
    }


//    /**
//     * Resets image edit controls to normal when new filter
//     * is selected
//     */
//    private void resetControls() {
//        if (editImageFragment != null) {
//            editImageFragment.resetControls();
//        }
//        brightnessFinal = 0;
//        saturationFinal = 1.0f;
//        contrastFinal = 1.0f;
//    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                mPhotoEditor.editText(rootView, inputText, colorCode);
                mTxtCurrentTool.setText(R.string.label_text);
            }

            @Override
            public void onDone(Typeface typeface, String inputText, int colorCode, int textSize) {
                Log.d("TABBBB", textSize + "");
                mPhotoEditor.editText(rootView, typeface, inputText, colorCode, textSize);
                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode, final Typeface typeface, final int textSize) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode, typeface, textSize);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                mPhotoEditor.editText(rootView, inputText, colorCode);
                mTxtCurrentTool.setText(R.string.label_text);
            }

            @Override
            public void onDone(Typeface typeface, String inputText, int colorCode, int textSize) {
                Log.d("TABBBB", textSize + "");
                mPhotoEditor.editText(rootView, typeface, inputText, colorCode, textSize);
                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {
//        TextView textView = mPhotoEditor.getTextViewAdd(numberOfAddedViews - 1);
//        textView.setText("bbb");
        Log.d(TAG, "onRemoveViewListener() called with: numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgUndo:
                mPhotoEditor.undo();
                break;

            case R.id.imgRedo:
                mPhotoEditor.redo();
                break;

            case R.id.imgSave:
                saveImage();
                break;

            case R.id.imgClose:
                onBackPressed();
                break;

            case R.id.imgOpen:
//                mTxtCurrentTool.setText(R.string.label_emoji);
//                showEmoji(true);
                if (mIsEmojiVisible) {
                    mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
                } else if (mIsStickerVisible) {
                    mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
                }

                break;

            case R.id.imgCamera:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;

            case R.id.imgGallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void saveImage() {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showLoading("Saving...");
            String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/addTextapp";

//            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/photoEditor"
//                    + System.currentTimeMillis() + ".png");

            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + ""
                    + System.currentTimeMillis() + ".png");
            Log.d("TABBBB", file.getAbsolutePath());
            try {
                file.createNewFile();

                SaveSettings saveSettings = new SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build();

                mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull final String imagePath) {
                        hideLoading();
//                        showSnackbar("Image Saved Successfully");
                        Log.d("TABBBB", imagePath);
                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                        mPhotoEditorView.getSource().buildDrawingCache();
                        Bitmap bmap = mPhotoEditorView.getSource().getDrawingCache();
                        saveImageToGallery(bmap);

//                        if (!TextUtils.isEmpty(imagePath)) {
//                            Snackbar snackbar = Snackbar
//                                    .make(mRootView, "Image saved to gallery!", Snackbar.LENGTH_LONG)
//                                    .setAction("OPEN", new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            openImage(imagePath);
//                                        }
//                                    });
//
//                            snackbar.show();
//                        } else {
//                            Snackbar snackbar = Snackbar
//                                    .make(mRootView, "Unable to save image!", Snackbar.LENGTH_LONG);
//
//                            snackbar.show();
//                        }
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        hideLoading();
                        showSnackbar("Failed to save Image");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                hideLoading();
                showSnackbar(e.getMessage());
            }
        }
    }

//    @SuppressLint("MissingPermission")
//    private void saveImage() {
//        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            showLoading("Saving...");
//            File file = new File(Environment.getExternalStorageDirectory()
//                    + File.separator + ""
//                    + System.currentTimeMillis() + ".png");
//            try {
//                file.createNewFile();
//
//                SaveSettings saveSettings = new SaveSettings.Builder()
//                        .setClearViewsEnabled(true)
//                        .setTransparencyEnabled(true)
//                        .build();
//
//                mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
//                    @Override
//                    public void onSuccess(@NonNull final String imagePath) {
//                        hideLoading();
//                        //addImageToGallery(imagePath);
////                        showSnackbar("Image Saved Successfully");
//                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
//                        if (!TextUtils.isEmpty(imagePath)) {
//                            Snackbar snackbar = Snackbar
//                                    .make(mRootView, "Image saved to gallery!", Snackbar.LENGTH_LONG)
//                                    .setAction("OPEN", new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            openImage(imagePath);
//                                        }
//                                    });
//
//                            snackbar.show();
//                        } else {
//                            Snackbar snackbar = Snackbar
//                                    .make(mRootView, "Unable to save image!", Snackbar.LENGTH_LONG);
//
//                            snackbar.show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        hideLoading();
//                        showSnackbar("Failed to save Image");
//                    }
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//                hideLoading();
//                showSnackbar(e.getMessage());
//            }
//        }
//    }

    /*
   * saves image to camera gallery
   * */
    private void saveImageToGallery(final Bitmap finalImage) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            final String path = BitmapUtils.insertImage(getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
                            if (!TextUtils.isEmpty(path)) {
                                Snackbar snackbar = Snackbar
                                        .make(mRootView, "Image saved to gallery!", Snackbar.LENGTH_LONG)
                                        .setAction("OPEN", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                openImage(path);
                                            }
                                        });

                                snackbar.show();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(mRootView, "Unable to save image!", Snackbar.LENGTH_LONG);

                                snackbar.show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    public void addImageToGallery(final String filePath) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    // opening image in default image viewer app
    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    mPhotoEditor.clearAllViews();
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    if (key.equals("1")) {
                        mPhotoEditorView.getSource().setImageBitmap(photo);
                    } else {
                        mPhotoEditor.addImage(photo);
                    }

                    break;
                case PICK_REQUEST:
                    try {
                        mPhotoEditor.clearAllViews();
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        if (key.equals("1")) {
                            mPhotoEditorView.getSource().setImageBitmap(bitmap);
                        } else {
                            mPhotoEditor.addImage(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.label_emoji);

    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_sticker);
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you want to exit without saving image ?");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImage();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();

    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    TextEditorDialogFragment textEditorDialogFragment;

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case BACKGROUND:
                mTxtCurrentTool.setText(R.string.label_filter);
                showFrame(true);
                break;
            case BRUSH:
                mPhotoEditor.setBrushDrawingMode(true);
                mTxtCurrentTool.setText(R.string.label_brush);
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        //todo
                        Log.d("TABBB", "a");
                    }

                    @Override
                    public void onDone(Typeface typeface, String inputText, int colorCode, int textSize) {
                        Log.d("TABBB", "b");
                        mPhotoEditor.addText(typeface, inputText, colorCode, textSize);
                        mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                break;
            case ERASER:
                mPhotoEditor.brushEraser();
                mTxtCurrentTool.setText(R.string.label_eraser);
                break;
            case FILTER:
                mTxtCurrentTool.setText(R.string.label_filter);
                showFilter(true);
                break;
            case EMOJI:
                mTxtCurrentTool.setText(R.string.label_emoji);
                imgOpen.setVisibility(View.VISIBLE);
                showEmoji(true);
//                mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
                break;
            case STICKER:
                mTxtCurrentTool.setText(R.string.label_sticker);
                imgOpen.setVisibility(View.VISIBLE);
                showSticker(true);
//                mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
                break;
        }
    }


    void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    void showSticker(boolean isVisible) {
        mIsStickerVisible = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(mRvSticker.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvSticker.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvSticker.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvSticker.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvSticker.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    void showFrame(boolean isVisible) {
        mIsFrame = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(mRvFrame.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvFrame.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvFrame.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvFrame.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvFrame.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    void showEmoji(boolean isVisible) {
        mIsEmojiVisible = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(mRvEmoji.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvEmoji.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvEmoji.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvEmoji.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvEmoji.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }
    boolean mIsFrame;

    @Override
    public void onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false);
            mTxtCurrentTool.setText(R.string.app_name);
        } else if(mIsFrame){
            showFrame(false);
            mTxtCurrentTool.setText(R.string.app_name);
        }

        else if (mIsStickerVisible) {
            imgOpen.setVisibility(View.GONE);
            showSticker(false);
            mTxtCurrentTool.setText(R.string.app_name);
        } else if (mIsEmojiVisible) {
            imgOpen.setVisibility(View.GONE);
            showEmoji(false);
            mTxtCurrentTool.setText(R.string.app_name);
        } else if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }
}
