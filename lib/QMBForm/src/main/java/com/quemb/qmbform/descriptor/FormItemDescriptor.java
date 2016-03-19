package com.quemb.qmbform.descriptor;

import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;

import com.quemb.qmbform.OnFormRowClickListener;
import com.quemb.qmbform.view.Cell;

import java.util.HashMap;

/**
 * Created by tonimoeckel on 14.07.14.
 */
public class FormItemDescriptor {

    protected Cell mCell;

    protected String mTag;


    protected String mTitle;
    private OnFormRowClickListener mOnFormRowClickListener;
    private HashMap<String, Object> mCellConfig;

    private int resourceId;

    private Fragment fragment;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public Cell getCell() {
        return mCell;
    }

    public void setCell(Cell cell) {
        mCell = cell;
    }


    public OnFormRowClickListener getOnFormRowClickListener() {
        return mOnFormRowClickListener;
    }

    public void setOnFormRowClickListener(OnFormRowClickListener onFormRowClickListener) {
        mOnFormRowClickListener = onFormRowClickListener;
    }

    public HashMap<String, Object> getCellConfig() {
        return mCellConfig;
    }

    public void setCellConfig(HashMap<String, Object> cellConfig) {
        mCellConfig = cellConfig;
    }

    public void setResourceId(@LayoutRes int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
