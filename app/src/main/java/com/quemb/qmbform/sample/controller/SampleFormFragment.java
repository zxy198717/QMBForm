package com.quemb.qmbform.sample.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.VideoPicker;
import com.kbeanie.multipicker.api.callbacks.VideoPickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenVideo;
import com.quemb.qmbform.FormManager;
import com.quemb.qmbform.OnFormRowClickListener;
import com.quemb.qmbform.descriptor.DataSource;
import com.quemb.qmbform.descriptor.DataSourceListener;
import com.quemb.qmbform.descriptor.FormDescriptor;
import com.quemb.qmbform.descriptor.FormItemDescriptor;
import com.quemb.qmbform.descriptor.FormOptionsObject;
import com.quemb.qmbform.descriptor.OnFormRowValueChangedListener;
import com.quemb.qmbform.descriptor.RowDescriptor;
import com.quemb.qmbform.descriptor.SectionDescriptor;
import com.quemb.qmbform.descriptor.Value;
import com.quemb.qmbform.pojo.ProcessedFile;
import com.quemb.qmbform.sample.R;
import com.quemb.qmbform.sample.model.MockContent;
import com.quemb.qmbform.view.FormMultipleProcessedImageFieldCell;
import com.quemb.qmbform.view.FormSelectorPushFieldCell;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tonimoeckel on 17.07.14.
 */
public class SampleFormFragment extends Fragment implements OnFormRowValueChangedListener,
        OnFormRowClickListener{

    private RecyclerView mListView;
    private HashMap<String, Value<?>> mChangesMap;
    private MenuItem mSaveMenuItem;

    public static String TAG = "SampleFormFragment";
    private FormManager mFormManager;
    VideoPicker videoPicker;

    public static final SampleFormFragment newInstance()
    {
        SampleFormFragment f = new SampleFormFragment();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.form_sample_recycler, container, false);

        mListView = (RecyclerView) v.findViewById(R.id.list);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mChangesMap = new HashMap<String, Value<?>>();

        FormDescriptor descriptor = FormDescriptor.newInstance();


        SectionDescriptor sectionDescriptor = SectionDescriptor.newInstance("section","Text Inputs");
        descriptor.addSection(sectionDescriptor);

        RowDescriptor rowDescriptor = RowDescriptor.newInstance("image", RowDescriptor.FormRowDescriptorTypeImage, "Image");
        sectionDescriptor.addRow(rowDescriptor);

        RowDescriptor rowDescriptorImages = RowDescriptor.newInstance("images", RowDescriptor.FormRowDescriptorTypeMultipleImage, "Images");
        sectionDescriptor.addRow(rowDescriptorImages);

        rowDescriptor = RowDescriptor.newInstance("content", RowDescriptor.FormRowDescriptorTypeSelectorPush, "PushSelector", new Value<List<MockContent>>(new ArrayList<MockContent>()));
        HashMap<String, Object> config = new HashMap<>();
        Intent intent = new Intent(getActivity(), ContentSelectActivity.class);
        config.put(FormSelectorPushFieldCell.PUSH_INTENT, intent);
        rowDescriptor.setCellConfig(config);
        sectionDescriptor.addRow(rowDescriptor);

        rowDescriptor = RowDescriptor.newInstance("segmented", RowDescriptor.FormRowDescriptorTypeSelectorSegmentedControlInline, "SegmentedControl", new Value<String>("false"));
        ArrayList<FormOptionsObject> objects = new ArrayList<>();
        objects.add(FormOptionsObject.createFormOptionsObject("true", "Agree"));
        objects.add(FormOptionsObject.createFormOptionsObject("false", "Not Agree"));
        rowDescriptor.setSelectorOptions(objects);
        sectionDescriptor.addRow(rowDescriptor);

        RowDescriptor rowDescriptorFiles = RowDescriptor.newInstance("files", RowDescriptor.FormRowDescriptorTypeMultipleFile, "Files");
        sectionDescriptor.addRow(rowDescriptorFiles);

        RowDescriptor rowDescriptorUploadImages = RowDescriptor.newInstance("uploadimages", RowDescriptor.FormRowDescriptorTypeMultipleProcessedImage, "Upload Images");
        config = new HashMap<>();
        config.put(FormMultipleProcessedImageFieldCell.MAX_COUNT, 9);
        rowDescriptorUploadImages.setCellConfig(config);
        sectionDescriptor.addRow(rowDescriptorUploadImages);

        sectionDescriptor.addRow( RowDescriptor.newInstance("detail", RowDescriptor.FormRowDescriptorTypeTextInline, "Title",new Value<String>("Detail")) );
        sectionDescriptor.addRow( RowDescriptor.newInstance("detail", RowDescriptor.FormRowDescriptorTypeText, "Title",new Value<String>("Detail")) );
        sectionDescriptor.addRow( RowDescriptor.newInstance("text",RowDescriptor.FormRowDescriptorTypeText, "Text", new Value<String>("test")) );
        RowDescriptor textDisabled = RowDescriptor.newInstance("textViewDisabled",RowDescriptor.FormRowDescriptorTypeText, "Text Disabled", new Value<String>("test"));
        textDisabled.setDisabled(true);
        sectionDescriptor.addRow( textDisabled );
        sectionDescriptor.addRow( RowDescriptor.newInstance("text",RowDescriptor.FormRowDescriptorTypeURL, "URL", new Value<String>("http://www.github.com/")) );
        RowDescriptor textUrlDisabled = RowDescriptor.newInstance("textViewDisabled",RowDescriptor.FormRowDescriptorTypeURL, "URL Disabled", new Value<String>("http://www.github.com/"));
        textUrlDisabled.setDisabled(true);
        sectionDescriptor.addRow( textUrlDisabled );
        sectionDescriptor.addRow( RowDescriptor.newInstance("text",RowDescriptor.FormRowDescriptorTypeEmail, "Email", new Value<String>("support@github.com")) );
        RowDescriptor textEmailDisabled = RowDescriptor.newInstance("textDisabled",RowDescriptor.FormRowDescriptorTypeEmail, "Email Disabled", new Value<String>("support@github.com"));
        textEmailDisabled.setDisabled(true);
        sectionDescriptor.addRow( textEmailDisabled );
        sectionDescriptor.addRow( RowDescriptor.newInstance("textView",RowDescriptor.FormRowDescriptorTypeTextView, "Text View", new Value<String>("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et ...")) );
        RowDescriptor textViewDisabled = RowDescriptor.newInstance("textViewDisabled",RowDescriptor.FormRowDescriptorTypeTextView, "Text View Disabled", new Value<String>("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et ..."));
        textViewDisabled.setDisabled(true);
        sectionDescriptor.addRow( textViewDisabled );
        sectionDescriptor.addRow( RowDescriptor.newInstance("number",RowDescriptor.FormRowDescriptorTypeNumber, "Number", new Value<Number>(555.456)) );
        RowDescriptor numberRowDisabled = RowDescriptor.newInstance("numberDisabled",RowDescriptor.FormRowDescriptorTypeNumber, "Number Disabled", new Value<Number>(555.456));
        numberRowDisabled.setDisabled(true);
        sectionDescriptor.addRow( numberRowDisabled );

        final RowDescriptor integerRow = RowDescriptor.newInstance("integer",RowDescriptor.FormRowDescriptorTypeInteger, "Integer", new Value<Number>(55));
        sectionDescriptor.addRow( integerRow );
        final RowDescriptor integerRowDisabled = RowDescriptor.newInstance("integerDisabled",RowDescriptor.FormRowDescriptorTypeInteger, "Integer Disabled", new Value<Number>(55));
        integerRowDisabled.setDisabled(true);
        sectionDescriptor.addRow( integerRowDisabled );

        sectionDescriptor.addRow( RowDescriptor.newInstance("integerSlider",RowDescriptor.FormRowDescriptorTypeIntegerSlider, "Integer Slider", new Value<Integer>(50)) );
        RowDescriptor integerSliderDisabled = RowDescriptor.newInstance("integerSliderDisabled",RowDescriptor.FormRowDescriptorTypeIntegerSlider, "Integer Slider Disabled", new Value<Number>(50));
        integerSliderDisabled.setDisabled(true);
        sectionDescriptor.addRow( integerSliderDisabled );
        SectionDescriptor sectionDescriptor1 = SectionDescriptor.newInstance("sectionOne","Picker");
        descriptor.addSection(sectionDescriptor1);
        RowDescriptor pickerDescriptor = RowDescriptor.newInstance("picker",RowDescriptor.FormRowDescriptorTypeSelectorPickerDialog, "Picker", new Value<String>("Item 5"));
        pickerDescriptor.setDataSource(new DataSource() {

            @Override
            public void loadData(final DataSourceListener listener) {
                // Can be async
                CustomTask task = new CustomTask();
                task.execute(listener);

            }
        });
        sectionDescriptor1.addRow( pickerDescriptor );

        RowDescriptor pickerDisabledDescriptor = RowDescriptor.newInstance("pickerDisabled",RowDescriptor.FormRowDescriptorTypeSelectorPickerDialog, "Picker Disabled", new Value<String>("Value"));
        pickerDisabledDescriptor.setDisabled(true);
        sectionDescriptor1.addRow(pickerDisabledDescriptor);

        SectionDescriptor sectionDescriptor2 = SectionDescriptor.newInstance("sectionTwo","Boolean Inputs");
        descriptor.addSection(sectionDescriptor2);

        sectionDescriptor2.addRow( RowDescriptor.newInstance("boolean",RowDescriptor.FormRowDescriptorTypeBooleanSwitch, "Boolean Switch", new Value<Boolean>(true)) );
        RowDescriptor booleanDisabled = RowDescriptor.newInstance("booleanDisabled",RowDescriptor.FormRowDescriptorTypeBooleanSwitch, "Boolean Switch Disabled", new Value<Boolean>(true));
        booleanDisabled.setDisabled(true);
        sectionDescriptor2.addRow( booleanDisabled );

        sectionDescriptor2.addRow( RowDescriptor.newInstance("check",RowDescriptor.FormRowDescriptorTypeBooleanCheck, "Check", new Value<Boolean>(true)) );
        RowDescriptor checkDisabled = RowDescriptor.newInstance("checkDisabled",RowDescriptor.FormRowDescriptorTypeBooleanCheck, "Check Disabled", new Value<Boolean>(true)) ;
        checkDisabled.setDisabled(true);
        sectionDescriptor2.addRow(checkDisabled);

        SectionDescriptor sectionDescriptor3 = SectionDescriptor.newInstance("sectionThree","Button");
        descriptor.addSection(sectionDescriptor3);

        final RowDescriptor button = RowDescriptor.newInstance("button",RowDescriptor.FormRowDescriptorTypeButton, "Tap Me");
        button.setOnFormRowClickListener(new OnFormRowClickListener() {
            @Override
            public void onFormRowClick(FormItemDescriptor itemDescriptor) {

//                You need to call updateRows in order to update titles
//                itemDescriptor.setTitle("New Title");
//                mFormManager.updateRows();

                integerRow.getValue().setValue(100);
                integerRow.setDisabled(true);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Tapped");
                builder.show();
            }
        });
        sectionDescriptor3.addRow( button );
        RowDescriptor buttonDisabled = RowDescriptor.newInstance("buttonDisabled",RowDescriptor.FormRowDescriptorTypeButton, "Tap Me Disabled");
        buttonDisabled.setOnFormRowClickListener(new OnFormRowClickListener() {
            @Override
            public void onFormRowClick(FormItemDescriptor itemDescriptor) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Tapped");
                builder.show();

            }
        });
        buttonDisabled.setDisabled(true);
        sectionDescriptor3.addRow(buttonDisabled);
        sectionDescriptor3.addRow(RowDescriptor.newInstance("external",RowDescriptor.FormRowDescriptorTypeExternal, "github.com", new Value<String>("http://github.com")));

        SectionDescriptor sectionDescriptor4 = SectionDescriptor.newInstance("sectionFour","Dates");
        descriptor.addSection(sectionDescriptor4);

        sectionDescriptor4.addRow( RowDescriptor.newInstance("dateInline",RowDescriptor.FormRowDescriptorTypeDateInline, "Date Inline", new Value<Date>(new Date()) ));
        RowDescriptor dateInlineDisabled = RowDescriptor.newInstance("dateInlineDisabled",RowDescriptor.FormRowDescriptorTypeDateInline, "Date Inline Disabled", new Value<Date>(new Date()) );
        dateInlineDisabled.setDisabled(true);
        sectionDescriptor4.addRow(dateInlineDisabled);
        sectionDescriptor4.addRow( RowDescriptor.newInstance("dateDialog",RowDescriptor.FormRowDescriptorTypeDate, "Date Dialog") );
        RowDescriptor dateDialogDisabled = RowDescriptor.newInstance("dateDialogDisabled",RowDescriptor.FormRowDescriptorTypeDate, "Date Dialog Disabled" );
        dateDialogDisabled.setDisabled(true);
        sectionDescriptor4.addRow(dateDialogDisabled);
        sectionDescriptor4.addRow( RowDescriptor.newInstance("timeInline",RowDescriptor.FormRowDescriptorTypeTimeInline, "Time Inline" , new Value<Date>(new Date())) );
        RowDescriptor timeInlineDisabled = RowDescriptor.newInstance("timeInlineDisabled",RowDescriptor.FormRowDescriptorTypeTimeInline, "Time Inline Disabled", new Value<Date>(new Date()) );
        timeInlineDisabled.setDisabled(true);
        sectionDescriptor4.addRow(timeInlineDisabled);
        sectionDescriptor4.addRow( RowDescriptor.newInstance("timeDialog",RowDescriptor.FormRowDescriptorTypeTime, "Time Dialog", new Value<Date>(new Date())) );
        RowDescriptor timeDialogDisabled = RowDescriptor.newInstance("timeDialogDisabled",RowDescriptor.FormRowDescriptorTypeTime, "Time Dialog Disabled", new Value<Date>(new Date()) );
        timeDialogDisabled.setDisabled(true);
        sectionDescriptor4.addRow(timeDialogDisabled);

        RowDescriptor datetime = RowDescriptor.newInstance("datetime",RowDescriptor.FormRowDescriptorTypeDateTime, "DateTime selected", new Value<Date>(new Date()) );
        sectionDescriptor4.addRow(datetime);

        mFormManager = new FormManager();
        mFormManager.setup(descriptor, mListView, this);
        Button button1 = new Button(getContext());
        button1.setText("Header");
        mFormManager.setHeader(button1);

        button1 = new Button(getContext());
        button1.setText("Footer");
        mFormManager.setFooter(button1);

        mFormManager.setOnFormRowClickListener(this);
        mFormManager.setOnFormRowValueChangedListener(this);



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.sample, menu);
        mSaveMenuItem = menu.findItem(R.id.action_save);
    }

    public void onAddIconLongClick(final RowDescriptor rowDescriptor) {
        Log.e("ddd", "onAddIconLongClick");
        videoPicker = new VideoPicker(this);
        videoPicker.shouldGenerateMetadata(true);
        videoPicker.shouldGeneratePreviewImages(true);
        videoPicker.setVideoPickerCallback(new VideoPickerCallback() {
            @Override
            public void onVideosChosen(List<ChosenVideo> list) {
                ChosenVideo video = list.get(0);
                Value<List<ProcessedFile>> value = rowDescriptor.getValue();
                ArrayList<ProcessedFile> imageItems = null;
                if (value != null && value.getValue() != null) {
                    imageItems = (ArrayList<ProcessedFile>) value.getValue();
                }

                if (imageItems == null) {
                    imageItems = new ArrayList<>();
                }

                ProcessedFile vv = new ProcessedFile(video.getOriginalPath());
                vv.setVideo(true);
                vv.setThumbPath(video.getPreviewImage());
                imageItems.add(vv);

                rowDescriptor.setValue(new Value<ArrayList<ProcessedFile>>(imageItems));
                if (null != rowDescriptor.getCell()) {
                    rowDescriptor.getCell().valueUpdate();
                }
            }

            @Override
            public void onError(String s) {

            }
        });
        videoPicker.pickVideo();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        updateSaveItem();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == mSaveMenuItem){
            mChangesMap.clear();
            updateSaveItem();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFormRowClick(FormItemDescriptor itemDescriptor) {

    }

    @Override
    public void onValueChanged(RowDescriptor rowDescriptor, Value<?> oldValue, Value<?> newValue) {

        Log.d(TAG, "Value Changed: " + rowDescriptor.getTitle());
//
        mChangesMap.put(rowDescriptor.getTag(), newValue);
        updateSaveItem();

        if ("files".equals(rowDescriptor.getTag()) || "uploadimages".equals(rowDescriptor.getTag())) {
            ArrayList<ProcessedFile> processedFiles = (ArrayList<ProcessedFile>) newValue.getValue();
            for (ProcessedFile file: processedFiles) {
                uploadFile(file);
            }
        }
    }

    private void uploadFile(final ProcessedFile processedFile) {
        if (processedFile.getProcessedStatus() != ProcessedFile.ProcessedStatus.READY) {
            return;
        }
        processedFile.setProcessedStatus(ProcessedFile.ProcessedStatus.UPLOADING);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i<= 100; i ++) {
                    try {
                        Thread.sleep(500);
                        processedFile.setCurrentPercent(i);
                        if(i == 100) {
                            processedFile.setProcessedStatus(ProcessedFile.ProcessedStatus.SUCCESS);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFormManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Picker.PICK_VIDEO_DEVICE && resultCode == Activity.RESULT_OK) {
            videoPicker.submit(data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateSaveItem() {
        if (mSaveMenuItem != null){
            mSaveMenuItem.setVisible(mChangesMap.size()>0);
        }
    }

    private class CustomTask extends AsyncTask<DataSourceListener, Void, ArrayList<String>> {

        private DataSourceListener mListener;
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "Loading",
                    "Do some work", true);
        }

        protected ArrayList<String> doInBackground(DataSourceListener... listeners) {

            mListener = (DataSourceListener)listeners[0];

            ArrayList<String> items = new ArrayList<String>();
            for (Integer i=0;i<10;i++){
                doFakeWork();
                items.add("Item "+String.valueOf(i));
            }

            return items;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            mProgressDialog.dismiss();
            mListener.onDataSourceLoaded(strings);
        }

        private void doFakeWork() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
