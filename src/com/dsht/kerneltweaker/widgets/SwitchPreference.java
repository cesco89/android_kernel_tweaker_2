package com.dsht.kerneltweaker.widgets;

import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.utils.CMDHelpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SwitchPreference extends GenericPreference implements OnCheckedChangeListener {
    
    private Switch mSwitch;
    private String mSwitchKey;
    private String mPositive;
    private String mNegative;
    private CMDHelpers mCMD;
    private Boolean checked;

    public SwitchPreference(Context context) {
        super(context);
        initConfig();
        init(context);
    }

    public SwitchPreference(Context context, String key) {
        super(context);
        init(context, key);
        initConfig();
    }
    
    public SwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initConfig();
        init(context);
    }
    
    public SwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initConfig();
        init(context);
    }
    
    
    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        
        mSwitch = (Switch) view.findViewById(R.id.switch1);
        mSwitch.setChecked(checked != null ? checked : getPersistedSwitch());
        mSwitch.setOnCheckedChangeListener(this);
        
        
    }
    
    @Override
    public void onClick() {
        super.onClick();
        boolean check = mSwitch.isChecked() ? false : true;
        mSwitch.setChecked(check);
        persistSwitch(check);
    }
    
    private void initConfig() {
        setLayoutResource(R.layout.preference_switch);
        mSwitchKey = getKey()+"_switch";
        mCMD = new CMDHelpers();
    }
    
    private void persistSwitch(boolean value) {
        if(value != getPersistedSwitch()) {
            checked = value;
            SharedPreferences.Editor editor = this.getSharedPreferences().edit();
            editor.putBoolean(mSwitchKey, value);
            editor.commit();
        }

    }


    private boolean getPersistedSwitch() {
        return getSharedPreferences().getBoolean(mSwitchKey, false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        persistSwitch(isChecked);
        runCommand(isChecked);
    }
    
    private void runCommand(boolean isChecked) {
        if(isChecked) {
            mCMD.setValuesShell(this.getFilePath(), mPositive);
        }else {
            mCMD.setValuesShell(this.getFilePath(), mNegative);
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
        this.checked = checked;
        notifyChanged();
    }
    
    public boolean isChecked() {
        return checked;
    }
    
    
    
    


}
