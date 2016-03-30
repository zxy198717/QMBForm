package com.quemb.qmbform.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.FilePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.FilePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenFile;
import com.quemb.qmbform.R;
import com.quemb.qmbform.descriptor.RowDescriptor;
import com.quemb.qmbform.descriptor.Value;
import com.quemb.qmbform.pojo.ProcessedFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FormMultipleFileFieldCell extends FormTitleFieldCell implements FilePickerCallback {

    ListView listView;

    TimerTask task = new TimerTask() {
        public void run() {
            FormMultipleFileFieldCell.this.post(new Runnable() {
                @Override
                public void run() {
                    if (fileListAdapter != null && fileListAdapter.getCount() > 0) {
                        fileListAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    Timer timer = new Timer(true);

    FileListAdapter fileListAdapter;

    ArrayList<ProcessedFile> processedFiles;

    private FilePicker filePicker;

    public FormMultipleFileFieldCell(Context context, RowDescriptor rowDescriptor) {
        super(context, rowDescriptor);
    }

    @Override
    protected void init() {
        super.init();
        listView = (ListView) findViewById(R.id.listView);

        findViewById(R.id.addControl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWaitingActivityResult(true);
                getFilePicker().pickFile();
            }
        });
    }

    @Override
    protected void update() {
        super.update();
        Value<List<ProcessedFile>> value = getRowDescriptor().getValue();
        if (value != null && value.getValue() != null) {
            processedFiles = (ArrayList<ProcessedFile>) value.getValue();
        }

        if (processedFiles == null) {
            processedFiles = new ArrayList<>();
        }

        listView.setAdapter(fileListAdapter = new FileListAdapter(getContext(), R.layout.multiple_file_field_list_item, processedFiles));
    }

    @Override
    protected int getResource() {
        return R.layout.multiple_file_field_cell;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Picker.PICK_FILE && resultCode == Activity.RESULT_OK) {
            getFilePicker().submit(data);
        }
    }

    @Override
    public void onFilesChosen(List<ChosenFile> list) {
        for (ChosenFile file: list) {
            processedFiles.add(new ProcessedFile(file.getDisplayName(), file.getOriginalPath()));
        }
        getRowDescriptor().setValue(null);
        onValueChanged(new Value<ArrayList<ProcessedFile>>(processedFiles));
        fileListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(String s) {
        showToast(s);
    }

    private FilePicker getFilePicker() {
        if (filePicker != null) {
            return filePicker;
        }
        if (getRowDescriptor().getFragment() != null ) {
            filePicker = new FilePicker(getRowDescriptor().getFragment());
        } else {
            filePicker = new FilePicker((Activity)getContext());
        }

        filePicker.setFilePickerCallback(this);
        filePicker.setCacheLocation(CacheLocation.EXTERNAL_CACHE_DIR);
        return filePicker;
    }

    class FileListAdapter extends ArrayAdapter<ProcessedFile> {

        public FileListAdapter(Context context, int resource, List<ProcessedFile> objects) {
            super(context, resource, R.id.titleTextView, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            Button opsButton = (Button)convertView.findViewById(R.id.opsButton);
            final ProcessedFile processedFile = getItem(position);
            opsButton.setEnabled(false);
            opsButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (processedFile.getProcessedStatus() == ProcessedFile.ProcessedStatus.FAIL) {
                        processedFile.setProcessedStatus(ProcessedFile.ProcessedStatus.READY);
                    } else {
                        processedFiles.remove(processedFile);
                    }

                    getRowDescriptor().setValue(null);
                    onValueChanged(new Value<ArrayList<ProcessedFile>>(processedFiles));
                    notifyDataSetChanged();
                }
            });
            if (processedFile.getProcessedStatus() == ProcessedFile.ProcessedStatus.READY) {
                opsButton.setVisibility(GONE);
            } else {
                opsButton.setVisibility(VISIBLE);
            }
            if (processedFile.getProcessedStatus() == ProcessedFile.ProcessedStatus.FAIL) {
                opsButton.setText(R.string.processed_fail);
                opsButton.setEnabled(true);
            } else if (processedFile.getProcessedStatus() == ProcessedFile.ProcessedStatus.SUCCESS){
                opsButton.setText(R.string.processed_success);
                opsButton.setEnabled(true);
            } else {
                opsButton.setText(processedFile.getCurrentPercent()+"");
            }

            return convertView;
        }
    }
}
