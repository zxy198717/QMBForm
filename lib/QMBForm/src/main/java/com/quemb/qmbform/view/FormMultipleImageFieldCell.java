package com.quemb.qmbform.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.quemb.qmbform.R;
import com.quemb.qmbform.descriptor.RowDescriptor;
import com.quemb.qmbform.descriptor.Value;
import com.quemb.qmbform.pojo.ImageItem;
import com.quemb.qmbform.widget.SquareImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import im.years.imagepicker.ImagePickerManager;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by Years.im on 16/3/19.
 */
public class FormMultipleImageFieldCell extends FormTitleFieldCell {

    private static final int REQUEST_IMAGE = 123;
    GridView gridView;
    ArrayList<ImageItem> imageItems;
    ImageGridAdapter imageGridAdapter;
    private int max = 3;
    private boolean multipleImagesPicker;
    private ImagePickerManager mImagePickerManager;

    public FormMultipleImageFieldCell(Context context, RowDescriptor rowDescriptor) {
        super(context, rowDescriptor);
    }

    @Override
    protected void init() {
        super.init();
        imageItems = new ArrayList<>();
        if(getRowDescriptor().getFragment() != null) {
            mImagePickerManager = new ImagePickerManager(getRowDescriptor().getFragment());
        } else {
            mImagePickerManager = new ImagePickerManager((Activity)getContext());
        }

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(imageGridAdapter = new ImageGridAdapter());

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imageItems.size()) {
                    if (imageItems.size() >= max) {
                        showToast("最多可选择" + max + "张图片");
                    } else {
                        onAddClick();
                    }
                }
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < imageItems.size()) {
                    confirmDialog(position);
                }
                return true;
            }
        });
    }

    @Override
    protected int getResource() {
        return R.layout.multiple_image_field_cell;
    }

    protected void multipleImagesPicker() {
        Intent intent = new Intent(getContext(), MultiImageSelectorActivity.class);
        // whether show camera
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // max select image amount
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, max - imageItems.size());
        // select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        // default select images (support array list)
        ArrayList<String> paths = new ArrayList<>();
        for (ImageItem imageItem: imageItems) {
            if (!imageItem.getPath().startsWith("http")) {
                paths.add(imageItem.getPath());
            }
        }
        intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, paths);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    protected void onAddClick() {

        if(multipleImagesPicker) {
            multipleImagesPicker();
            return;
        }

        this.setWaitingActivityResult(true);

        mImagePickerManager.pickImage(false, new ImagePickerManager.ImagePickerListener() {
            @Override
            public void onImageChosen(final ChosenImage image) {

                final Uri source = Uri.parse(new File(image
                        .getFileThumbnailSmall()).toString());
                gridView.post(new Runnable() {
                    @Override
                    public void run() {
                        ImageItem imageItem = new ImageItem(image.getFileThumbnail());
                        imageItems.add(imageItem);
                        onValueChanged(new Value<List<ImageItem>>(imageItems));
                        imageGridAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(String reason) {
                showToast(reason);
            }
        });
    }

    private void confirmDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder
                .setMessage("删除该图片?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        imageItems.remove(position);
                        onValueChanged(new Value<List<ImageItem>>(imageItems));
                        imageGridAdapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    protected void update() {
        super.update();

        if (getFormItemDescriptor().getCellConfig() != null) {
            if(getFormItemDescriptor().getCellConfig().containsKey("max")) {
                max = Integer.valueOf(getFormItemDescriptor().getCellConfig().get("max").toString());
            }
            if(getFormItemDescriptor().getCellConfig().containsKey("multipleImagesPicker")) {
                multipleImagesPicker = (Boolean) getFormItemDescriptor().getCellConfig().get("multipleImagesPicker");
            }
        }

        Value<List<ImageItem>> value = getRowDescriptor().getValue();
        if (value != null && value.getValue() != null) {
            imageItems = (ArrayList<ImageItem>) value.getValue();
        }

        imageGridAdapter.notifyDataSetChanged();
    }

    @Override
    protected void activityResult(int requestCode, int resultCode, Intent data) {
        mImagePickerManager.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE){
            if(resultCode == Activity.RESULT_OK){
                List<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                for (String path : paths) {
                    if(!findImage(path)) {
                        imageItems.add(new ImageItem(path));
                    }
                }
                onValueChanged(new Value<List<ImageItem>>(imageItems));
                imageGridAdapter.notifyDataSetChanged();
            }
        }

        super.activityResult(requestCode, resultCode, data);
    }

    private boolean findImage(String path) {
        for (ImageItem imageItem: imageItems) {
            if (imageItem.getPath().equals(path)) {
                return true;
            }
        }

        return false;
    }

    class ImageGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return max <= imageItems.size() ? imageItems.size(): imageItems.size() + 1;
        }

        @Override
        public ImageItem getItem(int position) {
            return imageItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SquareImageView imageView = new SquareImageView(getContext());

            if (position >= imageItems.size()) {
                imageView.setImageResource(R.drawable.image_button_upload);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
                Glide.with(getContext()).load(getItem(position).getPath()).into(imageView);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            return imageView;
        }
    }
}
