package com.burhanrashid52.imageeditor.image;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.burhanrashid52.imageeditor.R;
import com.burhanrashid52.imageeditor.tools.ToolType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.2
 * @since 5/23/2018
 */
public class EditingImageToolsAdapter extends RecyclerView.Adapter<EditingImageToolsAdapter.ViewHolder> {

    private List<ToolModel> mToolList = new ArrayList<>();
    private OnItemSelected mOnItemSelected;

    public EditingImageToolsAdapter(OnItemSelected onItemSelected) {
        mOnItemSelected = onItemSelected;
        mToolList.add(new ToolModel("ADJUST", R.drawable.ic_brush, ToolImageType.ADJUST));
        mToolList.add(new ToolModel("OPACITY", R.drawable.ic_text, ToolImageType.OPACITY));
        mToolList.add(new ToolModel("BLENDING", R.drawable.ic_eraser, ToolImageType.BLENDING));
        mToolList.add(new ToolModel("FILTER", R.drawable.ic_photo_filter, ToolImageType.FILTER));
        mToolList.add(new ToolModel("FRAME", R.drawable.ic_insert_emoticon, ToolImageType.FRAME));
    }

    public interface OnItemSelected {
        void onToolSelected(ToolImageType toolType);
    }

    class ToolModel {
        private String mToolName;
        private int mToolIcon;
        private ToolImageType mToolType;

        ToolModel(String toolName, int toolIcon, ToolImageType toolType) {
            mToolName = toolName;
            mToolIcon = toolIcon;
            mToolType = toolType;
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_editing_tools, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToolModel item = mToolList.get(position);
        holder.txtTool.setText(item.mToolName);
        holder.imgToolIcon.setImageResource(item.mToolIcon);
    }

    @Override
    public int getItemCount() {
        return mToolList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgToolIcon;
        TextView txtTool;

        ViewHolder(View itemView) {
            super(itemView);
            imgToolIcon = itemView.findViewById(R.id.imgToolIcon);
            txtTool = itemView.findViewById(R.id.txtTool);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemSelected.onToolSelected(mToolList.get(getLayoutPosition()).mToolType);
                }
            });
        }
    }
}
