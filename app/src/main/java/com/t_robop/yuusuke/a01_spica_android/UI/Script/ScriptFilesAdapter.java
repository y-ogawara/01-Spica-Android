package com.t_robop.yuusuke.a01_spica_android.UI.Script;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.t_robop.yuusuke.a01_spica_android.R;
import com.t_robop.yuusuke.a01_spica_android.databinding.ItemScriptFilesBinding;

import java.util.ArrayList;

public class ScriptFilesAdapter extends RecyclerView.Adapter<ScriptFilesAdapter.BindingHolder> {
    private ArrayList<FilesItemModel> mTitles;
    private Context mContext;
    private OnDeleteBtnClickListener deleteBtnClickListener;
    private OnItemClickListener itemClickListener;

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ItemScriptFilesBinding mBinding;

        public BindingHolder(ItemScriptFilesBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public ViewDataBinding getBinding() {
            return mBinding;
        }
    }

    public ScriptFilesAdapter(Context context) {
        mTitles = new ArrayList<>();
        this.mContext = context;
    }

    public void titleAdd(String title, boolean isCurrent) {
        mTitles.add(new FilesItemModel(title, isCurrent));
    }

    public void clear() {
        mTitles.clear();
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // recycler_itemレイアウト
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemScriptFilesBinding binding = ItemScriptFilesBinding.inflate(layoutInflater, parent, false);
        return new BindingHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(BindingHolder holder, final int position) {
        final FilesItemModel item = mTitles.get(position);
        holder.mBinding.textFileName.setText(item.title);
        holder.mBinding.fileDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBtnClickListener.onClick(item.title);
            }
        });
        holder.mBinding.fileContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onClick(item.title);
            }
        });
        if (item.isCurrent) {
            holder.mBinding.fileContainer.setBackgroundResource(R.drawable.corner_shape_gray_green);
        }
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    public interface OnDeleteBtnClickListener {
        void onClick(String title);
    }

    public interface OnItemClickListener {
        void onClick(String title);
    }

    public void setOnDeleteBtnClickListener(OnDeleteBtnClickListener listener) {
        this.deleteBtnClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    class FilesItemModel {
        private String title;
        private boolean isCurrent;

        FilesItemModel(String title, boolean isCurrent) {
            this.title = title;
            this.isCurrent = isCurrent;
        }
    }
}
