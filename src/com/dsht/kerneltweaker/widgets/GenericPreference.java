package com.dsht.kerneltweaker.widgets;

import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.database.Category;
import com.dsht.kerneltweaker.database.DatabaseHelpers;
import com.dsht.kerneltweaker.database.Item;
import com.dsht.kerneltweaker.interfaces.OnValueChangedListener;
import com.dsht.kerneltweaker.utils.UiHelpers;
import com.dsht.kerneltweaker.utils.UvItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class GenericPreference extends Preference{

    private Context mContext;
    private CheckBox mCheckBox;
    private String mKey;
    private String mBootKey;
    private String mEntryKey;
    private String mFilePath;
    private Category mCategory;
    private Config mConfig;
    private String mValue;
    private DatabaseHelpers mHelpers;
    private String defaultValue;
    private UiHelpers mUiHelpers;
    private Preference mAdvancedPref;
    private String masterDir;
    private AlertDialog mDialog;
    private TextView title, summary;
    private boolean fromJava = false;
    private boolean hideBoot = false;
    private boolean isMulticore = false;
    private boolean maxFreq = false;
    private boolean mustApplyValues = true;
    private View separator;
    private OnValueChangedListener mListener;
    private LayoutInflater mInflater;
    private UvItem mItem;
    private int position = 0;
    private boolean isCustomCommand = false;


    public GenericPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.preference);
        init(context);
    }

    public GenericPreference(Context context, String key) {
        super(context);
        setLayoutResource(R.layout.preference);
        init(context, key);
    }

    public GenericPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference);
        init(context);
    }

    public GenericPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        setLayoutResource(R.layout.preference);
        init(context);
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        title = (TextView) view.findViewById(android.R.id.title);
        summary = (TextView) view.findViewById(android.R.id.summary);
        mCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
        separator = (View) view.findViewById(R.id.separator);

        if(hideBoot) {
            mCheckBox.setVisibility(View.GONE);
            separator.setVisibility(View.GONE);
        } else {
            mCheckBox.setVisibility(View.VISIBLE);
            separator.setVisibility(View.VISIBLE);
        }

        Typeface tfTitle = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Condensed.ttf");
        Typeface tfSummary = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");

        title.setTypeface(tfTitle);
        summary.setTypeface(tfSummary);

        if(this.getSummary() != null) {
            summary.setText(this.getSummary());
        }

        mCheckBox.setChecked(getPersistedBoot());
        mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                // TODO Auto-generated method stub
                persistBoot(isChecked);
                mUiHelpers.animRotateView(arg0);
                if(isChecked) {
                    saveBootItem();
                }else {
                    deleteBootItem();
                }

            }
        });

    }

    public void setOnValueChangedListener(OnValueChangedListener listener) {
        this.mListener = listener;
    }

    public OnValueChangedListener getOnValueChangedListener() {
        return this.mListener;
    }

    protected void init(Context context) {
        mContext = context;
        mKey = getKey();
        mBootKey = mKey+"_onBoot";
        mEntryKey = mKey+"_entry";
        mConfig = Config.getInstance();
        mHelpers = new DatabaseHelpers();
        mUiHelpers = new UiHelpers(getContext());
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setPersistent(true);

    }

    protected void init(Context context, String key, boolean from) {
        mContext = context;
        fromJava = from;
        mKey = key;
        mBootKey = mKey+"_onBoot";
        mEntryKey = mKey+"_entry";
        mConfig = Config.getInstance();
        mHelpers = new DatabaseHelpers();
        mUiHelpers = new UiHelpers(getContext());
        setPersistent(true);

    }

    protected void init(Context context, String key) {
        mContext = context;
        mKey = key;
        mBootKey = mKey+"_onBoot";
        mEntryKey = mKey+"_entry";
        mConfig = Config.getInstance();
        mHelpers = new DatabaseHelpers();
        mUiHelpers = new UiHelpers(getContext());
        setPersistent(true);

    }

    protected void persistEntry(String entry) {
        if(entry != getPersistedEntry()) {
            SharedPreferences.Editor editor = this.getSharedPreferences().edit();
            editor.putString(mEntryKey, entry);
            editor.commit();
        }

    }


    private void persistBoot(boolean value) {
        if(value != getPersistedBoot()) {
            SharedPreferences.Editor editor = this.getSharedPreferences().edit();
            editor.putBoolean(mBootKey, value);
            editor.commit();
        }

    }

    private void saveBootItem() {
        Item item = new Item(this.getTitle().toString(), mFilePath, mValue);
        item.save();
    }

    public void updateValue(String entry, String value) {
        // TODO Auto-generated method stub
        if(isBootChecked()) {
            getDatabaseHelpers().saveItem(this.getTitle().toString(), this.getFilePath(), value);
        }

        this.persistString(value);
        this.setValue(value);
        this.persistEntry(entry);
        this.setSummary(entry);

    }

    private void deleteBootItem() {
        getDatabaseHelpers().deleteItem(this.getTitle().toString());
    }

    protected boolean getPersistedBoot() {
        return getSharedPreferences().getBoolean(mBootKey, false);
    }

    protected String getPersistedEntry() {

        String entry = getSharedPreferences().getString(mEntryKey, null);
        if(entry == null) {
            setSummary(defaultValue);
            return defaultValue;
        }
        setSummary(entry);
        return entry;
    }

    public void notifyChanges() {
        this.notifyChanged();
    }

    public void setFilePath(String path) {
        this.mFilePath = path;
    }

    public String getFilePath() {
        return this.mFilePath;
    }

    public boolean isBootChecked() {
        return this.getPersistedBoot();
    }

    public void setCategory(Category cat) {
        this.mCategory = cat;
    }

    public Category getCategory() {
        return this.mCategory;
    }

    public void setValue(String value) {
        this.mValue = value;
    }

    public String getValue() {
        return this.mValue;
    }

    public Config getConfig() {
        if(mConfig != null) {
            return this.mConfig;
        }
        return Config.getInstance();
    }

    public Context getContext() {
        return this.mContext;
    }

    public DatabaseHelpers getDatabaseHelpers() {
        return this.mHelpers;
    }
    public void setDefaultSummary(String sum) {
        this.defaultValue = sum;
    }

    public void setAdvancedPreference(Preference pref) {
        this.mAdvancedPref = pref;
    }

    public Preference getAdvancedPreference() {
        return this.mAdvancedPref;
    }

    public void setAdvancedDir(String dir) {
        this.masterDir = dir;
    }

    public String getAdvancedDir() {
        return this.masterDir;
    }

    public void setDialog(AlertDialog dialog) {
        this.mDialog = dialog;
    }

    public AlertDialog getDialog() {
        return this.mDialog;
    }

    public void setFromJava(boolean from) {
        this.fromJava = from;
    }

    public boolean isFromJava() {
        return this.fromJava;
    }

    public void hideBoot(boolean hide) {
        this.hideBoot = hide;
        notifyChanged();
    }

    public boolean isBootHidden() {
        return this.hideBoot;
    }

    public void setMulticore(boolean multicore) {
        this.isMulticore = multicore;
    }

    public boolean mustUseMulticore() {
        return this.isMulticore;
    }

    public void setIsMaxFreq(boolean max) {
        this.maxFreq = max;
    }

    public boolean isMaxFreq() {
        return this.maxFreq;
    }

    public boolean mustApplyValues() {
        return this.mustApplyValues;
    }

    public void setMustApplyValues(boolean must) {
        this.mustApplyValues = must;
    }


    public void setSummaryPlus(int value) {
        int curValue = Integer.parseInt(this.getValue());
        int newValue = curValue + value;
        this.setSummary(newValue+"");
        this.setValue(newValue+"");
        this.mItem.setValue(newValue+"");
    }

    public void setSummaryMinus(int value) {
        int curValue = Integer.parseInt(this.getValue());
        int newValue = curValue - value;
        this.setSummary(newValue+"");
        this.setValue(newValue+"");
        this.mItem.setValue(newValue+"");
    }

    public void setUvItem(UvItem item) {
        this.mItem = item;
    }

    public UvItem getUvItem() {
        return this.mItem;
    }

    public void setPosition(int pos) {
        this.position = pos;
    }

    public int getPosition() {
        return this.position;
    }
    
    public void setIsCustomCommand(boolean custom) {
        this.isCustomCommand = custom;
    }
    
    public boolean isCustomCommand() {
        return this.isCustomCommand;
    }
    
    public void saveToPrefs() {
        this.persistString(mValue);
    }


}
