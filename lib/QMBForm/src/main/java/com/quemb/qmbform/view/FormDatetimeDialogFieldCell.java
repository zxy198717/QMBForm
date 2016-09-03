package com.quemb.qmbform.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bigkoo.pickerview.TimePickerView;
import com.quemb.qmbform.descriptor.RowDescriptor;
import com.quemb.qmbform.descriptor.Value;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Years.im on 16/7/25.
 */
public class FormDatetimeDialogFieldCell extends FormDetailTextInlineFieldCell {

    public static final String MAX_YEAR = "FormDatetimeDialogFieldCell.MAX_YEAR";
    public static final String MIN_YEAR = "FormDatetimeDialogFieldCell.MIN_YEAR";

    public FormDatetimeDialogFieldCell(Context context, RowDescriptor rowDescriptor) {
        super(context, rowDescriptor);
    }

    @Override
    public void onCellSelected() {

        TimePickerView pvTime = new TimePickerView(getContext(), TimePickerView.Type.ALL);

        Date currentDate = new Date();

        if (getRowDescriptor().getValue() != null && getRowDescriptor().getValueData() != null && getRowDescriptor().getValueData() instanceof Date) {
            currentDate = (Date) getRowDescriptor().getValueData();
        }

        if (getRowDescriptor().getCellConfig().containsKey(MAX_YEAR) && getRowDescriptor().getCellConfig().containsKey(MIN_YEAR)) {
            pvTime.setRange((Integer) getRowDescriptor().getCellConfig().get(MIN_YEAR), (Integer) getRowDescriptor().getCellConfig().get(MAX_YEAR));
        }

        pvTime.setTime(currentDate);
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);

        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                getDetailTextView().setText(format.format(date));
                onValueChanged(new Value<Date>(date));
            }
        });

        pvTime.show();

        View view = ((Activity)getContext()).getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );
            if ( imm.isActive( ) ) {
                imm.hideSoftInputFromWindow( view.getApplicationWindowToken( ) , 0 );
            }
        }
    }
}
