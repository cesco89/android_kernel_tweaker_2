package com.dsht.kerneltweaker.widgets;

import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.utils.CMDHelpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;

public class CheckBoxPreference extends GenericPreference{
    
    private CheckBox mCheckBox;
    private String mCheckBoxKey;
    private String mPositive;
    private String mNegative;
    private boolean isCustomCommand = false;
    private CMDHelpers mCMD;
    private String COMMAND_REGEX = "echo %s > %s";

    public CheckBoxPreference(Context context) {
        super(context);
        initConfig();
    }
    
    public CheckBoxPreference(Context context, String key) {
        super(context);
        init(context, key);
    }
    
    public CheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initConfig();
    }
    
    public CheckBoxPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initConfig();
    }
    
    
    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        
        mCheckBox = (CheckBox) view.findViewById(R.id.cb);
        mCheckBox.setClickable(false);
        mCheckBox.setChecked(getPersistedCheckBox());
        
        
    }
    
    private void initConfig() {
        setLayoutResource(R.layout.preference_checkbox);
        mCheckBoxKey = getKey()+"_checkbox";
        mCMD = new CMDHelpers();
    }
    
    private void persistCheckBox(boolean value) {
        if(value != getPersistedCheckBox()) {
            SharedPreferences.Editor editor = this.getSharedPreferences().edit();
            editor.putBoolean(mCheckBoxKey, value);
            editor.commit();
        }

    }


    private boolean getPersistedCheckBox() {
        return getSharedPreferences().getBoolean(mCheckBoxKey, true);
    }
    
    @Override
    public void onClick() {
        super.onClick();
        boolean check = mCheckBox.isChecked() ? false : true;
        mCheckBox.setChecked(check);
        persistCheckBox(check);
        runCommand(check);
    }
    
    private void runCommand(boolean isChecked) {
        if(isCustomCommand) {
            if(isChecked) {
                mCMD.runCustomCommand(mPositive);
            }else {
                mCMD.runCustomCommand(mNegative);
            }
        }else{
            if(isChecked) {
                mCMD.setValuesShell(this.getFilePath(), mPositive);
            }else {
                mCMD.setValuesShell(this.getFilePath(), mNegative);
            }
        }
    }
    
    public void setPositiveValue(String val) {
        this.mPositive = val;
    }
    
    public void setNegativeValue(String val) {
        this.mNegative = val;
    }
    
    public String getPositiveValue() {
        return this.mPositive;
    }
    
    public String getNegativeValue() {
        return this.mNegative;
    }
    
    public void setChecked(boolean checked) {
        this.persistCheckBox(checked);
        this.notifyChanged();
    }
    
    public boolean isChecked() {
        return this.getPersistedCheckBox();
    }
    
    public void setIsCustomCommand(boolean custom) {
        this.isCustomCommand = custom;
    }
    
    public boolean isCustomCommand() {
        return this.isCustomCommand;
    }
    


}
