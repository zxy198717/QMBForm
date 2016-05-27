package com.quemb.qmbform.view;

import com.quemb.qmbform.descriptor.RowDescriptor;
import com.quemb.qmbform.descriptor.Value;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by tonimoeckel on 15.07.14.
 */
public class FormEditNumberFieldCell extends FormEditTextFieldCell {

    private static final String TAG = "FormEditNumberFieldCell";

    public FormEditNumberFieldCell(Context context,
                                   RowDescriptor rowDescriptor) {
        super(context, rowDescriptor);
    }


    @Override
    protected void init() {
        super.init();

        EditText editView = getEditView();
        editView.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }


    @Override
    protected void updateEditView() {

        String hint = getRowDescriptor().getHint(getContext());
        if (hint != null) {
            getEditView().setHint(hint);
        }

        Value<Number> value = (Value<Number>) getRowDescriptor().getValue();
        if (value != null && value.getValue() != null) {
            String valueString = String.valueOf(value.getValue());
            getEditView().setText(valueString);
        }

    }


    protected void onEditTextChanged(String string) {

        try {
            Double doubleValue = Double.parseDouble(string);
            onValueChanged(new Value<Number>(doubleValue));
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage(), e);
        }


    }
}
