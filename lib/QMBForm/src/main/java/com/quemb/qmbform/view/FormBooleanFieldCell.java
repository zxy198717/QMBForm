package com.quemb.qmbform.view;

import com.quemb.qmbform.R;
import com.quemb.qmbform.descriptor.RowDescriptor;
import com.quemb.qmbform.descriptor.Value;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * Created by tonimoeckel on 15.07.14.
 */
public class FormBooleanFieldCell extends FormBaseCell {

    private SwitchCompat mSwitch;

    public FormBooleanFieldCell(Context context,
                                RowDescriptor rowDescriptor) {
        super(context, rowDescriptor);
    }

    @Override
    protected void init() {

        super.init();

        mSwitch = (SwitchCompat) findViewById(R.id.switchControl);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onValueChanged(new Value<Boolean>(isChecked));
            }
        });

    }

    @Override
    protected int getResource() {
        return R.layout.boolean_field_cell;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void update() {

        String title = getFormItemDescriptor().getTitle();

        mSwitch.setText(title);
        mSwitch.setEnabled(!getRowDescriptor().getDisabled());

        Value<Boolean> value = (Value<Boolean>) getRowDescriptor().getValue();
        if (value != null && value.getValue() != null) {
            mSwitch.setChecked(value.getValue());
        }

    }

    public SwitchCompat getSwitch() {
        return mSwitch;
    }
}
