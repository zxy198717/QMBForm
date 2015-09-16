package com.quemb.qmbform.descriptor;

import android.content.Context;

import com.quemb.qmbform.R;
import com.quemb.qmbform.annotation.FormValidator;

import java.util.ArrayList;

/**
 * Created by tonimoeckel on 14.07.14.
 */
public class RowDescriptor<T> extends FormItemDescriptor {

    public static final String FormRowDescriptorTypeText = "text";
    public static final String FormRowDescriptorTypeTextInline = "textInline";
    public static final String FormRowDescriptorTypeHTMLText = "htmlText";
    public static final String FormRowDescriptorTypeDetailInline = "detailInline";
    public static final String FormRowDescriptorTypeDetail = "detail";
    public static final String FormRowDescriptorTypeURL = "url";
    public static final String FormRowDescriptorTypeEmail = "email";
    public static final String FormRowDescriptorTypeEmailInline = "emailInline";
    public static final String FormRowDescriptorTypePassword = "password";
    public static final String FormRowDescriptorTypePasswordInline = "passwordInline";
    public static final String FormRowDescriptorTypeNumber = "number";
    public static final String FormRowDescriptorTypeNumberInline = "numberInline";
    public static final String FormRowDescriptorTypeIntegerSlider = "integerSlider";
    public static final String FormRowDescriptorTypeCurrency = "currency";
    public static final String FormRowDescriptorTypePhone = "phone";
    public static final String FormRowDescriptorTypeTwitter = "twitter";
    public static final String FormRowDescriptorTypeAccount = "account";
    public static final String FormRowDescriptorTypeInteger = "integer";
    public static final String FormRowDescriptorTypeIntegerInline = "integerInline";
    public static final String FormRowDescriptorTypeTextView = "textView";
    public static final String FormRowDescriptorTypeTextViewInline = "textViewInline";
    public static final String FormRowDescriptorTypeSelectorPush = "selectorPush";
    public static final String FormRowDescriptorTypeSelectorActionSheet = "selectorActionSheet";
    public static final String FormRowDescriptorTypeSelectorAlertView = "selectorAlertView";
    public static final String FormRowDescriptorTypeSelectorPickerView = "selectorPickerView";
    public static final String FormRowDescriptorTypeSelectorPickerViewInline = "selectorPickerViewInline";
    public static final String FormRowDescriptorTypeSelectorSpinner = "selectorSpinner";
    public static final String FormRowDescriptorTypeSelectorSpinnerInline = "selectorSpinnerInline";
    public static final String FormRowDescriptorTypeTextPickerDialog = "textPickerDialog";
    public static final String FormRowDescriptorTypeSelectorPickerDialog = "selectorPickerDialog";
    public static final String FormRowDescriptorTypeSelectorPickerDialogVertical = "selectorPickerDialogVertical";
    public static final String FormRowDescriptorTypeMultipleSelector = "multipleSelector";
    public static final String FormRowDescriptorTypeSelectorLeftRight = "selectorLeftRight";
    public static final String FormRowDescriptorTypeSelectorSegmentedControlInline = "selectorSegmentedControlInline";
    public static final String FormRowDescriptorTypeSelectorSegmentedControl = "selectorSegmentedControl";
    public static final String FormRowDescriptorTypeDateInline = "dateInline";
    public static final String FormRowDescriptorTypeDateTimeInline = "datetimeInline";
    public static final String FormRowDescriptorTypeTimeInline = "timeInline";
    public static final String FormRowDescriptorTypeDate = "date";
    public static final String FormRowDescriptorTypeDateTime = "datetime";
    public static final String FormRowDescriptorTypeTime = "time";
    public static final String FormRowDescriptorTypeDatePicker = "datePicker";
    public static final String FormRowDescriptorTypePicker = "picker";
    public static final String FormRowDescriptorTypeBooleanCheck = "booleanCheck";
    public static final String FormRowDescriptorTypeBooleanSwitch = "booleanSwitch";
    public static final String FormRowDescriptorTypeButton = "button";
    public static final String FormRowDescriptorTypeButtonInline = "buttonInline";
    public static final String FormRowDescriptorTypeImage = "image";
    public static final String FormRowDescriptorTypeWeb = "web";
    public static final String FormRowDescriptorTypeExternal = "external";
    public static final String FormRowDescriptorTypeStepCounter = "stepCounter";
    public static final String FormRowDescriptorTypeSectionSeperator = "sectionSeperator";
    public static final String FormRowDescriptorTypeHtmlVertical = "htmlVertical";

    private String mRowType;
    private Value<T> mValue;
    /**
     * A list of valid values to pick from (e.g. used for spinners)
     */
    private DataSource<T> mDataSource;
    private Boolean mRequired = false;
    private Boolean mDisabled = false;

    private ArrayList<FormValidator> mValidators;

    private ArrayList<FormOptionsObject> mSelectorOptions;

    private SectionDescriptor mSectionDescriptor;

    private int mHint = android.R.string.untitled;

    public static RowDescriptor newInstance(String tag) {

        return RowDescriptor.newInstance(tag, FormRowDescriptorTypeDetailInline);

    }

    public static RowDescriptor newInstance(String tag, String rowType) {

        return RowDescriptor.newInstance(tag, rowType, null);

    }

    public static RowDescriptor newInstance(String tag, String rowType, String title) {

        return RowDescriptor.newInstance(tag, rowType, title, null);

    }

    public static RowDescriptor newInstance(String tag, String rowType, String title, Value<?> value) {

        RowDescriptor descriptor = new RowDescriptor();
        descriptor.mTitle = title;
        descriptor.mTag = tag;
        descriptor.mRowType = rowType;
        descriptor.setValue(value);
        descriptor.mValidators = new ArrayList<FormValidator>();

        return descriptor;

    }

    public SectionDescriptor getSectionDescriptor() {
        return mSectionDescriptor;
    }

    public void setSectionDescriptor(SectionDescriptor sectionDescriptor) {
        mSectionDescriptor = sectionDescriptor;
    }

    public Value<T> getValue() {
        return mValue;
    }

    public void setValue(Value<T> value) {
        mValue = value;
    }

    public Object getValueData() {
        return mValue.getValue();
    }

    public Boolean getRequired() {
        return mRequired;
    }

    public void setRequired(Boolean required) {
        mRequired = required;
    }

    public String getRowType() {
        return mRowType;
    }

    public boolean hasDataSource() {
        return mDataSource != null;
    }

    public DataSource<T> getDataSource() {
        return mDataSource;
    }

    public void setDataSource(DataSource<T> dataSource) {
        mDataSource = dataSource;
    }

    public Boolean getDisabled() {
        return mDisabled;
    }

    public void setDisabled(Boolean disabled) {
        mDisabled = disabled;
    }

    public void setHint(int hint) {
        mHint = hint;
    }

    public int getHint() {
        return mHint;
    }

    public String getHint(Context context) {

        if (mHint == android.R.string.untitled) {
            return null;
        }
        return context.getString(mHint);

    }

    public boolean isValid() {
        boolean valid = true;

        if (getRequired()) {
            valid = getValue() != null && getValue().getValue() != null;
        }

        if (getValidators() != null && valid) {
            for (FormValidator validator : getValidators()) {
                if (validator.validate(this) != null) {
                    valid = false;
                    break;
                }
            }
        }

        return valid;
    }

    public ArrayList<FormOptionsObject> getSelectorOptions() {
        return mSelectorOptions;
    }

    public void setSelectorOptions(ArrayList<FormOptionsObject> selectorOptions) {
        mSelectorOptions = selectorOptions;
    }

    public ArrayList<RowValidationError> getValidationErrors() {
        ArrayList<RowValidationError> rowValidationErrors = new ArrayList<RowValidationError>();

        if (getRequired()) {
            if (!(getValue() != null && getValue().getValue() != null)) {
                rowValidationErrors.add(new RowValidationError(this, R.string.validation_is_required));
            }
        }

        if (getValidators() != null) {
            for (FormValidator validator : getValidators()) {
                RowValidationError error = validator.validate(this);
                if (error != null) {
                    rowValidationErrors.add(error);
                }
            }
        }

        return (rowValidationErrors.isEmpty()) ? null : rowValidationErrors;
    }

    public static RowDescriptor newInstance(RowDescriptor rowDescriptor) {

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        RowDescriptor newInstance = RowDescriptor.newInstance(rowDescriptor.getTag() + "_" + ts, rowDescriptor.getRowType());
        newInstance.setDataSource(rowDescriptor.getDataSource());
        return newInstance;
    }

    public ArrayList<FormValidator> getValidators() {
        return mValidators;
    }

    public void addValidator(FormValidator validator) {
        mValidators.add(validator);
    }
}

