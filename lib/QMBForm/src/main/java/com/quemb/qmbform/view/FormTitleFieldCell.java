package com.quemb.qmbform.view;

import com.quemb.qmbform.R;
import com.quemb.qmbform.descriptor.RowDescriptor;

import android.content.Context;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by tonimoeckel on 15.07.14.
 */
public class FormTitleFieldCell extends FormBaseCell {
    private TextView mTextView;

    public FormTitleFieldCell(Context context,
                              RowDescriptor rowDescriptor) {
        super(context, rowDescriptor);
    }

    @Override
    protected void init() {
        super.init();
        mTextView = (TextView) findViewById(R.id.textView);
        /*
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.editTextColor, typedValue, true);
        final int color = typedValue.data;

        mTextView.setTextColor(color);
        */
    }

    @Override
    protected int getResource() {
        return R.layout.text_field_cell;
    }

    @Override
    protected void update() {
        String title = getFormItemDescriptor().getTitle();
        mTextView.setText(title);
        mTextView.setVisibility(title == null ? GONE : VISIBLE);


        if (getRowDescriptor().getDisabled()) {
            getRowDescriptor().setOnFormRowClickListener(null);
            mTextView.setTextColor(getResources().getColor(R.color.form_cell_disabled));
        }
    }

    public TextView getTextView() {
        return mTextView;
    }
}
