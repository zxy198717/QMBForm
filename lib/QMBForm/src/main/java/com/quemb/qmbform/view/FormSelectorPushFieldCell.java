package com.quemb.qmbform.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.quemb.qmbform.descriptor.RowDescriptor;
import com.quemb.qmbform.descriptor.Value;

import java.io.Serializable;

public class FormSelectorPushFieldCell extends FormDetailTextInlineFieldCell {

    public static final String PUSH_INTENT = "FormSelectorPushFieldCell.PUSH_INTENT";
    private static final int PUSH_INTENT_CODE = 102;

    public FormSelectorPushFieldCell(Context context,
                                     RowDescriptor rowDescriptor) {
        super(context, rowDescriptor);
    }


    @Override
    public void onCellSelected() {
        super.onCellSelected();

        if (getRowDescriptor().getDisabled()) {
            return;
        }

        Intent intent = (Intent) getRowDescriptor().getCellConfig().get(PUSH_INTENT);
        if (getRowDescriptor().getValue() != null && getRowDescriptor().getValueData() != null) {
            intent.putExtra("SELECTED_VALUES", (Serializable) getRowDescriptor().getValueData());
        }
        startActivityForResult(intent, PUSH_INTENT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PUSH_INTENT_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Object value = data.getSerializableExtra("SELECTED_VALUES");
            onValueChanged(new Value<Object>(value));
            update();
        }
    }
}
