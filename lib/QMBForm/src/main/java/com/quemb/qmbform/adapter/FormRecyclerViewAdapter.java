package com.quemb.qmbform.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.quemb.qmbform.CellViewFactory;
import com.quemb.qmbform.OnFormRowClickListener;
import com.quemb.qmbform.descriptor.FormDescriptor;
import com.quemb.qmbform.descriptor.FormItemDescriptor;
import com.quemb.qmbform.descriptor.RowDescriptor;
import com.quemb.qmbform.descriptor.SectionDescriptor;
import com.quemb.qmbform.view.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Years.im on 16/5/28.
 */
public class FormRecyclerViewAdapter extends RecyclerView.Adapter {

    private FormDescriptor mFormDescriptor;
    private ArrayList<FormItemDescriptor> mItems;
    private Context mContext;
    private Fragment mFragment;
    private Boolean mEnableSectionSeperator;
    protected OnFormRowClickListener mOnFormRowClickListener;

    public static FormRecyclerViewAdapter newInstance(FormDescriptor formDescriptor, Context context) {
        FormRecyclerViewAdapter formAdapter = new FormRecyclerViewAdapter();
        formAdapter.mFormDescriptor = formDescriptor;
        formAdapter.mContext = context;
        formAdapter.setEnableSectionSeperator(true);
        return formAdapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        FormItemDescriptor rowDescriptor = mItems.get(viewType);
        rowDescriptor.setFragment(mFragment);
        Cell cell = CellViewFactory.getInstance().createViewForFormItemDescriptor(mContext, mItems.get(viewType));
        cell.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        FormRecyclerViewHolder holder = new FormRecyclerViewHolder(cell);

        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Cell cell = (Cell) holder.itemView;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormItemDescriptor itemDescriptor = mItems.get(position);

                if (cell != null && itemDescriptor instanceof RowDescriptor) {
                    RowDescriptor rowDescriptor = (RowDescriptor) itemDescriptor;
                    rowDescriptor.setCell(cell);
                    if (!rowDescriptor.getDisabled()) {
                        cell.onCellSelected();
                    }

                    if (rowDescriptor.getRowType().equals(RowDescriptor.FormRowDescriptorTypeSectionSeperator)) {
                        return;
                    }
                }

                OnFormRowClickListener descriptorListener = itemDescriptor.getOnFormRowClickListener();
                if (descriptorListener != null) {
                    descriptorListener.onFormRowClick(itemDescriptor);
                }

                if (mOnFormRowClickListener != null) {
                    mOnFormRowClickListener.onFormRowClick(itemDescriptor);
                }
            }
        });

        cell.valueUpdate();
    }

    @Override
    public int getItemCount() {
        mItems = new ArrayList<FormItemDescriptor>();
        int sectionCount = 1;
        for (SectionDescriptor sectionDescriptor : mFormDescriptor.getSections()) {

            if (sectionDescriptor.hasTitle()) {
                mItems.add(sectionDescriptor);
            }

            List<RowDescriptor> rows = sectionDescriptor.getRows();
            if (rows.size() > 0) {
                rows.get(0).setIsFirstInSection(true);
                rows.get(rows.size() - 1).setIsLastInSection(true);
            }

            mItems.addAll(rows);

            if (getEnableSectionSeperator() && sectionCount < mFormDescriptor.getSections().size()) {
                mItems.add(RowDescriptor.newInstance(null, RowDescriptor.FormRowDescriptorTypeSectionSeperator));
            }
            sectionCount++;
        }

        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Boolean getEnableSectionSeperator() {
        return mEnableSectionSeperator;
    }

    public void setFragment(Fragment mFragment) {
        this.mFragment = mFragment;
    }

    public void setEnableSectionSeperator(Boolean enableSectionSeperator) {
        mEnableSectionSeperator = enableSectionSeperator;
    }

    public void setOnFormRowClickListener(OnFormRowClickListener onFormRowClickListener) {
        mOnFormRowClickListener = onFormRowClickListener;
    }
}
