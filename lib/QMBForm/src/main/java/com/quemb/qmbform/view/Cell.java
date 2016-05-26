package com.quemb.qmbform.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.quemb.qmbform.R;
import com.quemb.qmbform.descriptor.FormItemDescriptor;
import com.quemb.qmbform.descriptor.SectionDescriptor;

/**
 * Created by tonimoeckel on 14.07.14.
 */
public abstract class Cell extends LinearLayout {

    private FormItemDescriptor mFormItemDescriptor;

    private View mDividerView;

    private boolean mWaitingActivityResult;

    BroadcastReceiver NewWaitingBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String tag = intent.getStringExtra("row_tag");
            if (mFormItemDescriptor.getTag() !=null &&!mFormItemDescriptor.getTag().equals(tag)) {
                setWaitingActivityResult(false);
            }
        }
    };

    public Cell(Context context, FormItemDescriptor formItemDescriptor) {
        super(context);
        setFormItemDescriptor(formItemDescriptor);

        init();
        update();
        afterInit();
    }

    protected void afterInit() {

    }

    protected void init() {

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);

        @LayoutRes int resource = mFormItemDescriptor.getResourceId() > 0 ? mFormItemDescriptor.getResourceId() : getResource();
        if (resource > 0) {
            inflate(getContext(), resource, getSuperViewForLayoutInflation());
        }

        setBackgroundColor(getResources().getColor(R.color.form_cell_background));

        if (shouldAddDivider()) {
            addView(getDividerView());
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("new_waiting_activity_result");
        getContext().registerReceiver(NewWaitingBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(NewWaitingBroadcastReceiver);
    }

    protected ViewGroup getSuperViewForLayoutInflation() {
        return this;
    }

    protected abstract int getResource();

    protected abstract void update();

    public void valueUpdate() {
        update();
    }

    public FormItemDescriptor getFormItemDescriptor() {
        return mFormItemDescriptor;
    }

    public void setFormItemDescriptor(FormItemDescriptor formItemDescriptor) {

        mFormItemDescriptor = formItemDescriptor;
        mFormItemDescriptor.setCell(this);
    }

    public void onCellSelected() {

    }

    protected View getDividerView() {
        if (mDividerView == null) {
            mDividerView = new View(getContext());
            configDivider(mDividerView);
        }
        return mDividerView;
    }

    private void configDivider(View dividerView) {

        LinearLayout.LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                1
        );
        if ( !(getFormItemDescriptor() instanceof SectionDescriptor) && !(this instanceof SeperatorSectionCell) && !getFormItemDescriptor().isLastInSection()) {
            params.setMargins(getResources().getDimensionPixelSize(R.dimen.cell_padding), 0, 0, 0);
        }

        dividerView.setLayoutParams(params);

        dividerView.setBackgroundColor(getThemeValue(android.R.attr.listDivider));

    }

    protected int getThemeValue(int resource) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(resource, typedValue, true);

        return typedValue.data;
    }

    public boolean shouldAddDivider() {
        return true;
    }

    public void lastInSection() {

    }

    protected void setDividerView(View dividerView) {
        mDividerView = dividerView;
    }

    protected void startActivityForResult(Intent intent, int requestCode) {
        setWaitingActivityResult(true);
        if (mFormItemDescriptor.getFragment() != null) {
            mFormItemDescriptor.getFragment().startActivityForResult(intent, requestCode);
        } else {
            if (getContext() instanceof Activity) {
                ((Activity)getContext()).startActivityForResult(intent, requestCode);
            }
        }
    }

    protected void setWaitingActivityResult(boolean mWaitingActivityResult) {
        this.mWaitingActivityResult = mWaitingActivityResult;
        if (mWaitingActivityResult) {
            Intent intent = new Intent("new_waiting_activity_result");
            intent.putExtra("row_tag", mFormItemDescriptor.getTag());
            getContext().sendBroadcast(intent);
        }
    }

    public boolean isWaitingActivityResult() {
        return mWaitingActivityResult;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(mWaitingActivityResult) {
            activityResult(requestCode, resultCode, data);
        }
    }

    protected void activityResult(int requestCode, int resultCode, Intent data){

    }

    protected void showToast(final String message) {
        post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
