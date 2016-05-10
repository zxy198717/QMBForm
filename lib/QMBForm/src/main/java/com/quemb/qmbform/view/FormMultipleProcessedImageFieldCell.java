package com.quemb.qmbform.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.quemb.qmbform.R;
import com.quemb.qmbform.brower.PhotoBrowserActivity;
import com.quemb.qmbform.descriptor.MediaFile;
import com.quemb.qmbform.descriptor.RowDescriptor;
import com.quemb.qmbform.descriptor.Value;
import com.quemb.qmbform.pojo.ImageItem;
import com.quemb.qmbform.pojo.ProcessedFile;
import com.quemb.qmbform.widget.PhotoBrowserViewPager;
import com.quemb.qmbform.widget.SquareImageView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class FormMultipleProcessedImageFieldCell extends FormTitleFieldCell {

    private static final int REQUEST_IMAGE = 124;
    private static final int REQUEST_IMAGE_PREVIEW = 125;
    public static final String MAX_COUNT = "MAX_COUNT";

    GridView gridView;
    ArrayList<ProcessedFile> imageItems;
    ImageGridAdapter imageGridAdapter;
    private int max;

    TimerTask task = new TimerTask() {
        public void run() {
            FormMultipleProcessedImageFieldCell.this.post(new Runnable() {
                @Override
                public void run() {
                    if (imageGridAdapter != null && imageGridAdapter.getCount() > 1) {
                        imageGridAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    Timer timer = new Timer(true);

    public FormMultipleProcessedImageFieldCell(Context context, RowDescriptor rowDescriptor) {
        super(context, rowDescriptor);
    }

    @Override
    protected void init() {
        super.init();
        gridView = (GridView) findViewById(R.id.gridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imageItems.size()) {
                    if (imageItems.size() >= max) {
                        showToast("最多可选择" + max + "张图片");
                    } else {
                        multipleImagesPicker();
                    }
                } else {
                    Intent intent = new Intent(getContext(), PhotoBrowserActivity.class);
                    intent.putExtra(PhotoBrowserActivity.PHOTOS, imageItems);
                    intent.putExtra(PhotoBrowserActivity.SELECTED_ITEM, position);

                    startActivityForResult(intent, REQUEST_IMAGE_PREVIEW);
                }
            }
        });
    }

    @Override
    protected void update() {
        super.update();

        if (getFormItemDescriptor().getCellConfig() != null) {
            if (getFormItemDescriptor().getCellConfig().containsKey(MAX_COUNT)) {
                max = Integer.valueOf(getFormItemDescriptor().getCellConfig().get(MAX_COUNT).toString());
            }
        }

        if (max <= 0) {
            max = 3;
        }

        Value<List<ProcessedFile>> value = getRowDescriptor().getValue();
        if (value != null && value.getValue() != null) {
            imageItems = (ArrayList<ProcessedFile>) value.getValue();
        }

        if (imageItems == null) {
            imageItems = new ArrayList<>();
        }
        reOrderImages();
        gridView.setAdapter(imageGridAdapter = new ImageGridAdapter());

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (getRowDescriptor().getDisabled()) {
                    return false;
                }
                if (position < imageItems.size()) {
                    confirmDialog(position);
                } else {
                    //Long Click
                    if (findVideo()) {
                        showToast("只能包含一个视频文件");
                        return true;
                    }
                    Fragment fragment = getFormItemDescriptor().getFragment();
                    if (fragment != null) {
                        try {
                            Class clazz = fragment.getClass();
                            Method m1 = clazz.getMethod("onAddIconLongClick", RowDescriptor.class);
                            m1.setAccessible(true);
                            m1.invoke(fragment, getRowDescriptor());
                        } catch (Exception exc) {
                            exc.printStackTrace();
                        }
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected int getResource() {
        return R.layout.multiple_image_field_cell;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        timer.schedule(task, 1000, 1000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        timer.cancel();
    }

    protected void multipleImagesPicker() {
        Intent intent = new Intent(getContext(), MultiImageSelectorActivity.class);
        // whether show camera
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // max select image amount
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, max - uploadedImageCount());
        // select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        // default select images (support array list)
        ArrayList<String> paths = new ArrayList<>();
        for (ProcessedFile imageItem : imageItems) {
            if (!imageItem.getPath().startsWith("http") && !MediaFile.isVideoFileType(imageItem.getPath())) {
                paths.add(imageItem.getPath());
            }
        }
        intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, paths);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private int uploadedImageCount() {
        int count = 0;
        for (ProcessedFile imageItem : imageItems) {
            if (imageItem.getPath().startsWith("http")) {
                count ++;
            }
        }
        return count;
    }

    private boolean findImage(String path) {
        for (ProcessedFile imageItem : imageItems) {
            if (imageItem.getPath().equals(path)) {
                return true;
            }
        }

        return false;
    }

    private boolean findVideo() {
        for (ProcessedFile imageItem : imageItems) {
            if (imageItem.isVideo()) {
                return true;
            }
        }
        return false;
    }

    private void reOrderImages() {
        ProcessedFile video = null;
        for (ProcessedFile imageItem : imageItems) {
            if (imageItem.isVideo()) {
                video = imageItem;
            }
        }

        if (video != null) {
            imageItems.remove(video);
            imageItems.add(video);
        }
    }

    private void confirmDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder
                .setMessage("确定要删除吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        imageItems.remove(position);
                        onValueChanged(new Value<List<ProcessedFile>>(imageItems));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                List<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                for (String path : paths) {
                    if (!findImage(path)) {
                        imageItems.add(new ProcessedFile(path));
                    }
                }
                getRowDescriptor().setValue(null);
                reOrderImages();
                onValueChanged(new Value<List<ProcessedFile>>(imageItems));
                imageGridAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == REQUEST_IMAGE_PREVIEW) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<ProcessedFile> imageItems = (ArrayList<ProcessedFile>) data.getSerializableExtra(PhotoBrowserActivity.PHOTOS);
                this.imageItems.clear();
                this.imageItems.addAll(imageItems);
                reOrderImages();
                onValueChanged(new Value<List<ProcessedFile>>(this.imageItems));
                imageGridAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class ImageGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (getRowDescriptor().getDisabled()) {
                return imageItems.size();
            }
            return max <= imageItems.size() ? imageItems.size() : imageItems.size() + 1;
        }

        @Override
        public ProcessedFile getItem(int position) {
            return imageItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.multiple_image_field_grid_item, null);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            CircleProgress circleProgress = (CircleProgress) convertView.findViewById(R.id.circleProgress);
            ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.deleteButton);
            circleProgress.setVisibility(GONE);
            deleteButton.setVisibility(GONE);
            convertView.findViewById(R.id.playImageView).setVisibility(GONE);
            deleteButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageItems.remove(position);
                    getRowDescriptor().setValue(null);
                    reOrderImages();
                    onValueChanged(new Value<List<ProcessedFile>>(imageItems));
                    notifyDataSetChanged();
                }
            });
            if (position >= imageItems.size()) {
                imageView.setImageResource(R.drawable.image_button_upload);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } else {

                ProcessedFile processedFile = getItem(position);

                if (processedFile.getProcessedStatus() != ProcessedFile.ProcessedStatus.READY) {
                    circleProgress.setVisibility(VISIBLE);
                    circleProgress.setProgress((int) (processedFile.getCurrentPercent()));
                }
                if (processedFile.getProcessedStatus() == ProcessedFile.ProcessedStatus.FAIL || processedFile.getProcessedStatus() == ProcessedFile.ProcessedStatus.SUCCESS) {
                    deleteButton.setVisibility(VISIBLE);
                }

                if (processedFile.isVideo()) {
                    convertView.findViewById(R.id.playImageView).setVisibility(VISIBLE);
                    Glide.with(getContext()).load(getItem(position).getThumbPath()).into(imageView);
                } else {
                    convertView.findViewById(R.id.playImageView).setVisibility(GONE);
                    Glide.with(getContext()).load(getItem(position).getPath()).into(imageView);
                }

                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            return convertView;
        }
    }
}
