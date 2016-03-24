package com.quemb.qmbform.view;

import com.quemb.qmbform.descriptor.FormItemDescriptor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

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

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction("new_waiting_activity_result");

        getContext().registerReceiver(NewWaitingBroadcastReceiver, intentFilter);
    }

    protected void afterInit() {

    }

    protected void init() {

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);

        int resource = mFormItemDescriptor.getResourceId() > 0 ? mFormItemDescriptor.getResourceId() : getResource();
        if (resource > 0) {
            inflate(getContext(), resource, getSuperViewForLayoutInflation());
        }

        if (shouldAddDivider()) {
            addView(getDividerView());
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
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

        dividerView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                1
        ));

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
        mWaitingActivityResult = true;
        mFormItemDescriptor.getFragment().startActivityForResult(intent, requestCode);
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
