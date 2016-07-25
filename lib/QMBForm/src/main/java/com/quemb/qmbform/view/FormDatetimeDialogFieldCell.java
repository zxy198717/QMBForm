package com.quemb.qmbform.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.quemb.qmbform.descriptor.RowDescriptor;
import com.quemb.qmbform.descriptor.Value;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Years.im on 16/7/25.
 */
public class FormDatetimeDialogFieldCell extends FormDateDialogFieldCell implements TimePickerDialog.OnTimeSetListener {
    public FormDatetimeDialogFieldCell(Context context, RowDescriptor rowDescriptor) {
        super(context, rowDescriptor);
    }

    @Override
    public void onCellSelected() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        TimePickerDialog dialog = new TimePickerDialog(getContext(), this, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
        dialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);

        onDateChanged(mCalendar.getTime());
    }

    @Override
    public void onDateChanged(Date date) {
        getDetailTextView().setText(DateFormat.format("yyyy-MM-dd HH:mm", date).toString());
        onValueChanged(new Value<Date>(date));
    }
}
