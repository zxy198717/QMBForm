package com.quemb.qmbform.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.quemb.qmbform.R;
import com.quemb.qmbform.descriptor.RowDescriptor;
import com.quemb.qmbform.descriptor.Value;

import java.io.File;
import java.util.Date;

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
        mImagePickerManager = new ImagePickerManager(getRowDescriptor().getFragment());
    }

    @Override
    protected int getResource() {
        return R.layout.image_field_cell;
    }

    @Override
    protected void update() {
        super.update();
        Value<String> value = (Value<String>) getRowDescriptor().getValue();
        if(value != null && value.getValue() != null) {
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
        mImagePickerManager.pickImage(crop, new ImagePickerManager.ImagePickerListener() {
            @Override
            public void onImageChosen(final ChosenImage image) {

                final Uri source = Uri.parse(new File(image
                        .getFileThumbnailSmall()).toString());
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageURI(source);
                        onValueChanged(new Value<String>(crop ? image
                                .getFileThumbnailSmall() : image.getFileThumbnail()));
                    }
                });
                setWaitingActivityResult(false);

            }

            @Override
            public void onError(String reason) {
                setWaitingActivityResult(false);
            }
        });
    }

    @Override
    protected void activityResult(int requestCode, int resultCode, Intent data) {
        mImagePickerManager.onActivityResult(requestCode, resultCode, data);
        super.activityResult(requestCode, resultCode, data);
    }
}
