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

    private boolean isFirstInSection;
    private boolean isLastInSection;
    private boolean shouldAddDivider = true;

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

    public void setIsFirstInSection(boolean isFirstInSection) {
        this.isFirstInSection = isFirstInSection;
    }

    public void setIsLastInSection(boolean isLastInSection) {
        this.isLastInSection = isLastInSection;
    }

    public boolean isLastInSection() {
        return isLastInSection;
    }

    public boolean isFirstInSection() {
        return isFirstInSection;
    }

    public void setShouldAddDivider(boolean shouldAddDivider) {
        this.shouldAddDivider = shouldAddDivider;
    }

    public boolean isShouldAddDivider() {
        return shouldAddDivider;
    }

    public OnFormRowClickListener getOnFormRowClickListener() {
        return mOnFormRowClickListener;
    }

    public void setOnFormRowClickListener(OnFormRowClickListener onFormRowClickListener) {
        mOnFormRowClickListener = onFormRowClickListener;
    }

    public HashMap<String, Object> getCellConfig() {

        if (mCellConfig == null) {
            mCellConfig = new HashMap<>();
        }

        return mCellConfig;
    }

    public void setCellConfig(HashMap<String, Object> cellConfig) {
        mCellConfig = cellConfig;
    }

    public FormItemDescriptor putCellConfig(String key, Object value) {
        if (mCellConfig == null) {
            mCellConfig = new HashMap<>();
        }

        mCellConfig.put(key, value);

        return this;
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
