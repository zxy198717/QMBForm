package com.quemb.qmbform.view;

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
import android.widget.Toast;

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

/**
 * Created by Years.im on 16/3/19.
 */
public class FormMultipleImageFieldCell extends FormTitleFieldCell {

    GridView gridView;
    ArrayList<ImageItem> imageItems;
    ImageGridAdapter imageGridAdapter;
    private int max = 3;
    private ImagePickerManager mImagePickerManager;

    public FormMultipleImageFieldCell(Context context, RowDescriptor rowDescriptor) {
        super(context, rowDescriptor);
    }

    @Override
    protected void init() {
        super.init();
        imageItems = new ArrayList<>();
        mImagePickerManager = new ImagePickerManager(getRowDescriptor().getFragment());

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(imageGridAdapter = new ImageGridAdapter());

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imageItems.size()) {
                    if(imageItems.size() >= max) {
                        Toast.makeText(getContext(), "最多可选择"+max+"张图片", Toast.LENGTH_SHORT).show();
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

    protected void onAddClick() {
        setWaitingActivityResult(true);
        mImagePickerManager.pickImage(false, new ImagePickerManager.ImagePickerListener() {
            @Override
            public void onImageChosen(final ChosenImage image) {

                final Uri source = Uri.parse(new File(image
                        .getFileThumbnailSmall()).toString());
                gridView.post(new Runnable() {
                    @Override
                    public void run() {
                        ImageItem imageItem = new ImageItem();
                        imageItem.setPath(image.getFileThumbnail());
                        imageItems.add(imageItem);
                        onValueChanged(new Value<List<ImageItem>>(imageItems));
                        imageGridAdapter.notifyDataSetChanged();
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

        if (getFormItemDescriptor().getCellConfig() != null && getFormItemDescriptor().getCellConfig().containsKey("max")) {
            max = Integer.valueOf(getFormItemDescriptor().getCellConfig().get("max").toString());
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
        super.activityResult(requestCode, resultCode, data);
    }

    class ImageGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return imageItems.size() + 1;
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
            if (position == getCount() - 1) {
                imageView.setImageResource(R.drawable.ic_action_new);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
            } else {
                Glide.with(getContext()).load(getItem(position).getPath()).into(imageView);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            return imageView;
        }
    }
}
