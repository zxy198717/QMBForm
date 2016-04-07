package com.quemb.qmbform.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.quemb.qmbform.descriptor.RowDescriptor;
import com.quemb.qmbform.descriptor.Value;
import com.quemb.qmbform.pojo.ImageItem;
import com.quemb.qmbform.pojo.ProcessedFile;
import com.quemb.qmbform.widget.SquareImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class FormMultipleProcessedImageFieldCell extends FormTitleFieldCell {

    private static final int REQUEST_IMAGE = 124;

    GridView gridView;
    ArrayList<ProcessedFile> imageItems;
    ImageGridAdapter imageGridAdapter;
    private int max = 3;

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
                }
            }
        });
    }

    @Override
    protected void update() {
        super.update();

        if (getFormItemDescriptor().getCellConfig() != null) {
            if(getFormItemDescriptor().getCellConfig().containsKey("max")) {
                max = Integer.valueOf(getFormItemDescriptor().getCellConfig().get("max").toString());
            }
        }

        Value<List<ProcessedFile>> value = getRowDescriptor().getValue();
        if (value != null && value.getValue() != null) {
            imageItems = (ArrayList<ProcessedFile>) value.getValue();
        }

        if(imageItems == null) {
            imageItems = new ArrayList<>();
        }

        gridView.setAdapter(imageGridAdapter = new ImageGridAdapter());
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
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, max);
        // select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        // default select images (support array list)
        ArrayList<String> paths = new ArrayList<>();
        for (ProcessedFile imageItem: imageItems) {
            if (!imageItem.getPath().startsWith("http")) {
                paths.add(imageItem.getPath());
            }
        }
        intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, paths);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private boolean findImage(String path) {
        for (ProcessedFile imageItem: imageItems) {
            if (imageItem.getPath().equals(path)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE){
            if(resultCode == Activity.RESULT_OK){
                List<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                for (String path : paths) {
                    if(!findImage(path)) {
                        imageItems.add(new ProcessedFile(path));
                    }
                }
                getRowDescriptor().setValue(null);
                onValueChanged(new Value<List<ProcessedFile>>(imageItems));
                imageGridAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class ImageGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return max <= imageItems.size() ? imageItems.size(): imageItems.size() + 1;
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

            ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView);
            CircleProgress circleProgress = (CircleProgress)convertView.findViewById(R.id.circleProgress);
            ImageButton deleteButton = (ImageButton)convertView.findViewById(R.id.deleteButton);
            circleProgress.setVisibility(GONE);
            deleteButton.setVisibility(GONE);
            deleteButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageItems.remove(position);
                    getRowDescriptor().setValue(null);
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
                    circleProgress.setProgress( (int)(processedFile.getCurrentPercent()));
                }
                if(processedFile.getProcessedStatus() == ProcessedFile.ProcessedStatus.FAIL || processedFile.getProcessedStatus() == ProcessedFile.ProcessedStatus.SUCCESS) {
                    deleteButton.setVisibility(VISIBLE);
                }

                Glide.with(getContext()).load(getItem(position).getPath()).into(imageView);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            return convertView;
        }
    }
}
