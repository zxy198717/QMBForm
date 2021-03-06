package com.quemb.qmbform;

import com.quemb.qmbform.adapter.FormAdapter;
import com.quemb.qmbform.adapter.FormRecyclerViewAdapter;
import com.quemb.qmbform.descriptor.FormDescriptor;
import com.quemb.qmbform.descriptor.FormItemDescriptor;
import com.quemb.qmbform.descriptor.FormRecycledViewPool;
import com.quemb.qmbform.descriptor.OnFormRowChangeListener;
import com.quemb.qmbform.descriptor.OnFormRowValueChangedListener;
import com.quemb.qmbform.descriptor.RowDescriptor;
import com.quemb.qmbform.descriptor.SectionDescriptor;
import com.quemb.qmbform.descriptor.Value;
import com.quemb.qmbform.view.Cell;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by tonimoeckel on 15.07.14.
 */
public class FormManager implements OnFormRowChangeListener, OnFormRowValueChangedListener {

    private FormDescriptor mFormDescriptor;
    protected ListView mListView;
    private FormAdapter adapter;
    protected RecyclerView mRecyclerView;
    private FormRecyclerViewAdapter formRecyclerViewAdapter;
    protected OnFormRowClickListener mOnFormRowClickListener;
    private OnFormRowChangeListener mOnFormRowChangeListener;
    private OnFormRowValueChangedListener mOnFormRowValueChangedListener;

    public FormManager() {

    }

    public void setup(FormDescriptor formDescriptor, final RecyclerView recyclerView, Activity activity) {
        mRecyclerView = recyclerView;
        if (mRecyclerView.getLayoutManager() == null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        }
        formRecyclerViewAdapter = FormRecyclerViewAdapter.newInstance(formDescriptor, activity);
        formRecyclerViewAdapter.setOnFormRowClickListener(new OnFormRowClickListener() {
            @Override
            public void onFormRowClick(FormItemDescriptor itemDescriptor) {
                if (mOnFormRowClickListener != null) {
                    mOnFormRowClickListener.onFormRowClick(itemDescriptor);
                }
            }
        });

        mFormDescriptor = formDescriptor;
        mFormDescriptor.setOnFormRowChangeListener(this);
        mFormDescriptor.setOnFormRowValueChangedListener(this);
        mRecyclerView.setAdapter(formRecyclerViewAdapter);
        mRecyclerView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        mRecyclerView.setRecycledViewPool(new FormRecycledViewPool());
    }

    public void setup(FormDescriptor formDescriptor, final RecyclerView recyclerView, Fragment fragment) {
        setup(formDescriptor, recyclerView, fragment.getActivity());
        formRecyclerViewAdapter.setFragment(fragment);
    }

    public void setup(FormDescriptor formDescriptor, final ListView listView, Fragment fragment) {
        setup(formDescriptor, listView, fragment.getActivity());
        adapter.setFragment(fragment);
    }

    public void setup(FormDescriptor formDescriptor, final ListView listView, Activity activity) {

        Context context = activity;

//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mFormDescriptor = formDescriptor;
        mFormDescriptor.setOnFormRowChangeListener(this);
        mFormDescriptor.setOnFormRowValueChangedListener(this);

        adapter = FormAdapter.newInstance(mFormDescriptor, context);
        listView.setAdapter(adapter);
        listView.setDividerHeight(1);
        listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FormItemDescriptor itemDescriptor = (FormItemDescriptor)parent.getAdapter().getItem(position);


                Cell cell = (Cell) view;
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
        mListView = listView;

    }

    public void setHeader(View header) {
        if (formRecyclerViewAdapter != null) {
            formRecyclerViewAdapter.setHeaderView(header);
        }
    }

    public void setFooter(View footer) {
        if (formRecyclerViewAdapter != null) {
            formRecyclerViewAdapter.setFooterView(footer);
        }
    }

    public OnFormRowClickListener getOnFormRowClickListener() {
        return mOnFormRowClickListener;
    }

    public void setOnFormRowClickListener(OnFormRowClickListener onFormRowClickListener) {
        mOnFormRowClickListener = onFormRowClickListener;
    }

    public void updateRows() {

        if (mListView != null) {
            ListAdapter listAdapter = mListView.getAdapter();
            FormAdapter adapter = null;
            if (listAdapter instanceof HeaderViewListAdapter) {
                adapter = (FormAdapter)((HeaderViewListAdapter)listAdapter).getWrappedAdapter();
            } else {
                adapter = (FormAdapter) listAdapter;
            }

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        } else {
            formRecyclerViewAdapter.notifyDataSetChanged();
        }
    }


    public OnFormRowChangeListener getOnFormRowChangeListener() {
        return mOnFormRowChangeListener;
    }

    public void setOnFormRowChangeListener(OnFormRowChangeListener onFormRowChangeListener) {
        mOnFormRowChangeListener = onFormRowChangeListener;
    }

    @Override
    public void onRowAdded(RowDescriptor rowDescriptor, SectionDescriptor sectionDescriptor) {
        updateRows();
        if (mOnFormRowChangeListener != null) {
            mOnFormRowChangeListener.onRowAdded(rowDescriptor, sectionDescriptor);
        }
    }

    @Override
    public void onRowRemoved(RowDescriptor rowDescriptor, SectionDescriptor sectionDescriptor) {
        updateRows();
        if (mOnFormRowChangeListener != null) {
            mOnFormRowChangeListener.onRowRemoved(rowDescriptor, sectionDescriptor);
        }
    }

    @Override
    public void onRowChanged(RowDescriptor rowDescriptor, SectionDescriptor sectionDescriptor) {
        updateRows();
        if (mOnFormRowChangeListener != null) {
            mOnFormRowChangeListener.onRowChanged(rowDescriptor, sectionDescriptor);
        }
    }

    @Override
    public void onValueChanged(RowDescriptor rowDescriptor, Value<?> oldValue, Value<?> newValue) {
        if (mOnFormRowValueChangedListener != null) {
            mOnFormRowValueChangedListener.onValueChanged(rowDescriptor, oldValue, newValue);
        }
    }

    public void setOnFormRowValueChangedListener(
            OnFormRowValueChangedListener onFormRowValueChangedListener) {
        mOnFormRowValueChangedListener = onFormRowValueChangedListener;
    }

    public FormDescriptor getFormDescriptor() {
        return mFormDescriptor;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        for(SectionDescriptor sectionDescriptor: mFormDescriptor.getSections()) {
            for (RowDescriptor rowDescriptor: sectionDescriptor.getRows()) {
                Cell cell = rowDescriptor.getCell();
                if(cell != null && cell.isWaitingActivityResult()) {
                    cell.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
        /*
        for (int i=mListView.getFirstVisiblePosition();i<=mListView.getLastVisiblePosition();i++) {
            View v = mListView.getChildAt(i);
            if (v instanceof Cell) {
                Cell cell = (Cell)v;
                if(cell != null && cell.isWaitingActivityResult()) {
                    cell.onActivityResult(requestCode, resultCode, data);
                }
            }

        }
        */
    }
}

