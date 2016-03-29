package com.quemb.qmbform.view;

import com.quemb.qmbform.R;
import com.quemb.qmbform.descriptor.RowDescriptor;
import com.quemb.qmbform.descriptor.Value;

import android.content.Context;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by pmaccamp on 9/4/2015.
 */
public class FormDetailTextInlineFieldCell extends FormTitleFieldCell {

    private TextView mDetailTextView;

    public FormDetailTextInlineFieldCell(Context context,
                                         RowDescriptor rowDescriptor) {
        super(context, rowDescriptor);
    }

    @Override
    protected void init() {

        super.init();
        mDetailTextView = (TextView) findViewById(R.id.detailTextView);
        mDetailTextView.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Body1);
    }

    @Override
    protected int getResource() {
        return R.layout.detail_text_inline_field_cell;
    }

    @Override
    protected void update() {
        super.update();

        if (getRowDescriptor().getHint(getContext()) != null) {
            getDetailTextView().setHint(getRowDescriptor().getHint(getContext()));
        }

        Value<?> value = getRowDescriptor().getValue();
        if (value != null && value.getValue() != null) {
            if (value.getValue() instanceof String) {
                getDetailTextView().setText((String) value.getValue());
            } else if(value.getValue() instanceof List) {
                List<Object> objects = (List<Object>) value.getValue();
                String valueStr = "";
                int index = 0;
                for (Object object : objects) {
                    String str = String.valueOf(object);
                    Class clazz = object.getClass();
                    boolean find = false;
                    try {
                        Method m1 = clazz.getDeclaredMethod("getName");
                        str = (String) m1.invoke(object);
                        find = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if(!find) {
                        try {
                            Method m1 = clazz.getDeclaredMethod("getTitle");
                            str = (String) m1.invoke(object);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    valueStr += str;
                    if (index != objects.size() - 1) {
                        valueStr += ",";
                    }
                    index ++;
                }
                getDetailTextView().setText(valueStr);
            } else {
                getDetailTextView().setText(String.valueOf(value.getValue()));
            }
        }
    }

    public TextView getDetailTextView() {
        return mDetailTextView;
    }
}
