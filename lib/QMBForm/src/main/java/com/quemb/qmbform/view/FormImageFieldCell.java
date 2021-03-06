package com.quemb.qmbform.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.quemb.qmbform.R;
import com.quemb.qmbform.descriptor.RowDescriptor;
import com.quemb.qmbform.descriptor.Value;

import im.years.imagepicker.ImagePickerManager;

/**
 * Created by Years.im on 16/3/19.
 */
public class FormImageFieldCell extends FormTitleFieldCell {

    private ImagePickerManager mImagePickerManager;

    private ImageView imageView;

    private boolean crop;

    public FormImageFieldCell(Context context, RowDescriptor rowDescriptor) {
        super(context, rowDescriptor);
    }

    @Override
    protected void init() {
        super.init();
        imageView = (ImageView) findViewById(R.id.imageView);
        if (getRowDescriptor().getFragment() != null) {
            mImagePickerManager = new ImagePickerManager(getRowDescriptor().getFragment());
        } else {
            mImagePickerManager = new ImagePickerManager((Activity) getContext());
        }
    }

    @Override
    protected int getResource() {
        return R.layout.image_field_cell;
    }

    @Override
    protected void update() {
        super.update();
        Value<String> value = (Value<String>) getRowDescriptor().getValue();
        if (value != null && value.getValue() != null) {
            Glide.with(getContext()).load(value.getValue()).into(imageView);
        }

        if (getRowDescriptor().getCellConfig() != null && getRowDescriptor().getCellConfig().containsKey("crop")) {
            crop = (boolean) getRowDescriptor().getCellConfig().get("crop");
        }
    }

    @Override
    public void onCellSelected() {
        super.onCellSelected();
        setWaitingActivityResult(true);

        final ImagePickerManager.ImagePickerListener ls = new ImagePickerManager.ImagePickerListener() {
            @Override
            public void onImageChosen(final ChosenImage image) {
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getContext()).load(crop ? image
                                .getThumbnailSmallPath() : image.getOriginalPath()).into(imageView);
                        onValueChanged(new Value<String>(crop ? image
                                .getThumbnailSmallPath() : image.getOriginalPath()));
                    }
                });
            }

            @Override
            public void onError(String reason) {
                showToast(reason);
            }
        };

        mImagePickerManager.pickImage(crop, ls);
    }

    @Override
    protected void activityResult(int requestCode, int resultCode, Intent data) {
        mImagePickerManager.onActivityResult(requestCode, resultCode, data);
        super.activityResult(requestCode, resultCode, data);
    }
}
