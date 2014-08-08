package com.dsht.kerneltweaker.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceScreen;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.view.animation.Transformation;

import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.adapters.AppsBaseAdapter;
import com.dsht.kerneltweaker.adapters.AppsBaseAdapter.AppItem;
import com.dsht.kerneltweaker.adapters.ListPreferenceBaseAdapter;
import com.dsht.kerneltweaker.adapters.ProfileDialogBaseAdapter;
import com.dsht.kerneltweaker.adapters.ProfilesBaseAdapter;
import com.dsht.kerneltweaker.database.AppProfile;
import com.dsht.kerneltweaker.database.DatabaseHelpers;
import com.dsht.kerneltweaker.database.Profile;
import com.dsht.kerneltweaker.database.UserItem;
import com.dsht.kerneltweaker.interfaces.OnLoadingFinishedListener;
import com.dsht.kerneltweaker.interfaces.OnValueChangedListener;
import com.dsht.kerneltweaker.widgets.EditPreference;
import com.dsht.kerneltweaker.widgets.FloatLabelLayout;
import com.dsht.kerneltweaker.widgets.GenericPreference;
import com.dsht.kerneltweaker.widgets.ListPreference;
import com.dsht.kerneltweaker.widgets.SwitchPreference;

public class UiHelpers {

    private Context mContext;
    private Resources mRes;
    private Config mConfig;
    private CMDHelpers mCMD;
    private DatabaseHelpers mDb;
    private ProgressDialog mProgDiag;
    private LayoutInflater mInflater;

    public UiHelpers(Context c) {
        this.mContext = c;
        this.mRes = mContext.getResources();
        this.mConfig = Config.getInstance();
        this.mCMD = new CMDHelpers();
        this.mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mDb = new DatabaseHelpers();
        mConfig.load();
    }
    /**
     * 
     * @param pref
     * {@link com.dsht.kerneltweaker.widgets.ListPreference}
     * 
     * @param entries
     * User readable values
     * 
     * @param values
     * values to set
     */
    public AlertDialog buildListPreferenceDialog(final GenericPreference pref, final String[] entries, final String[] values) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(null);
        View v = mInflater.inflate(R.layout.list_preference_dialog_layout, null, false);
        ListView list = (ListView) v.findViewById(R.id.list);
        final ListPreferenceBaseAdapter mAdapter = new ListPreferenceBaseAdapter(mContext, entries, pref.getSummary().toString());
        Log.d("PREF", pref.getSummary().toString());
        list.setAdapter(mAdapter);
        builder.setView(v);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = ((AlertDialog) dialog).getButton(Dialog.BUTTON_POSITIVE);
                btnPositive.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));

                Button btnNegative = ((AlertDialog) dialog).getButton(Dialog.BUTTON_NEGATIVE);
                btnNegative.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
            }
        });

        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                    long id) {
                // TODO Auto-generated method stub
                mAdapter.setValue(entries[position]);
                pref.updateValue(entries[position],values[position]);
                if(pref.mustApplyValues()) {
                    if(pref.mustUseMulticore()) {
                        mCMD.setMulticoreValues(values[position], pref.isMaxFreq());
                    }else {
                        if(pref.isCustomCommand()) {
                            String command = String.format(pref.getFilePath(), values[position]);
                            mCMD.runCustomCommand(command);
                        }else {
                            mCMD.setValuesShell(pref.getFilePath(), values[position]);
                        }
                    }
                }
                OnValueChangedListener listener =  pref.getOnValueChangedListener();
                if(listener != null) {
                    listener.onValueChanged(pref, values[position]);
                }
                dialog.dismiss();
            }

        });
        return dialog;
    }



    public AlertDialog buildEditTextDialog(final EditPreference pref, String defvalue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(null);
        View v = mInflater.inflate(R.layout.edittext_dialog_layout, null, false);
        FloatLabelLayout mLayout = (FloatLabelLayout) v.findViewById(R.id.fl_layout);
        mLayout.setLabelHead(mRes.getString(R.string.label_default));
        mLayout.setLabelText(defvalue);
        mLayout.setLabelTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Condensed.ttf"));
        final EditText et = (EditText) v.findViewById(R.id.edit_value);
        builder.setView(v);
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                pref.updateValue(et.getText().toString(), et.getText().toString());
                et.setHint(et.getText().toString());
                if(pref.mustApplyValues()) {
                    mCMD.setValuesShell(pref.getFilePath(), et.getText().toString());
                }
                OnValueChangedListener listener =  pref.getOnValueChangedListener();
                if(listener != null) {
                    listener.onValueChanged(pref, et.getText().toString());
                }
                et.setText("");
                dialog.cancel();

            }
        })
        .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });
        AlertDialog diag = builder.create();
        diag.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = ((AlertDialog) dialog).getButton(Dialog.BUTTON_POSITIVE);
                btnPositive.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));

                Button btnNegative = ((AlertDialog) dialog).getButton(Dialog.BUTTON_NEGATIVE);
                btnNegative.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
            }
        });
        return diag;
    }


    public AlertDialog buildSaveProfileDialog(final String maxFreq, final String minFreq, final String governor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(null);
        View v = mInflater.inflate(R.layout.edittext_dialog_layout, null, false);
        FloatLabelLayout mLayout = (FloatLabelLayout) v.findViewById(R.id.fl_layout);
        mLayout.setLabelHead("");
        mLayout.setLabelText(mRes.getString(R.string.label_insert_name));
        mLayout.setLabelTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Condensed.ttf"));
        final EditText et = (EditText) v.findViewById(R.id.edit_value);
        builder.setView(v);
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if(!et.getText().equals("")){
                    mDb.saveProfile(et.getText().toString(), maxFreq, minFreq, governor);
                    et.setText("");
                }else{
                    Toast.makeText(mContext, mRes.getString(R.string.empty_name_error), Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();

            }
        })
        .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });
        AlertDialog diag = builder.create();
        diag.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = ((AlertDialog) dialog).getButton(Dialog.BUTTON_POSITIVE);
                btnPositive.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));

                Button btnNegative = ((AlertDialog) dialog).getButton(Dialog.BUTTON_NEGATIVE);
                btnNegative.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
            }
        });
        return diag;
    }

    public void showLoadingDialog() {
        mProgDiag = new ProgressDialog(mContext);
        mProgDiag.setIndeterminate(true);
        mProgDiag.setMessage("Loading...");
        mProgDiag.show(); 
    }

    public void dismissLoadingDialog() {
        if(mProgDiag.isShowing()) {
            mProgDiag = null;
        }
    }

    public void loadPreferences(final PreferenceScreen mRoot, final File[] files) {      
        mRoot.removeAll();
        for(int i = 0; i < files.length ; i++) {
            if(!files[i].isDirectory()) {
                if(!files[i].getName().equals("uevent") && !files[i].getName().equals("dev")) {
                    String content = Helpers.getFileContent(files[i]);
                    EditPreference pref = new EditPreference(mContext, files[i].getName(), true);
                    mRoot.addPreference(pref);
                    pref.setTitle(files[i].getName());
                    pref.setValue(content);
                    pref.setSummary(content);
                    pref.setFilePath(files[i].getAbsolutePath());
                    if(files[i].getName().contains("freq")) {
                        pref.setDialog(buildListPreferenceDialog(pref, mConfig.getCpuFreqValues(), mConfig.getCpuFreqValues()));
                    }else{
                        pref.setDialog(buildEditTextDialog(pref, content));
                    }
                }
            }
        }
    }

    public void createUvPreferences(final PreferenceScreen mRoot, final ArrayList<UvItem> list,
            final String path, final Fragment f, final OnLoadingFinishedListener mListener) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i<list.size(); i++) {
                    EditPreference pref = new EditPreference(mContext, list.get(i).getName(), true);
                    mRoot.addPreference(pref);
                    pref.setUvItem(list.get(i));
                    pref.setTitle(list.get(i).getName());
                    pref.hideBoot(true);
                    pref.setSummary(list.get(i).getValue());
                    pref.setValue(list.get(i).getValue());
                    pref.setFilePath(path);
                    pref.setOnValueChangedListener((OnValueChangedListener)f);
                    pref.setDialog(buildEditTextDialog(pref, list.get(i).getValue()));
                    pref.setMustApplyValues(false);
                    pref.setPosition(i);
                    pref.notifyChanges();
                }
                mListener.onLoadingFinished();
            }
        };
        new Handler().post(r);
    }

    public void animRotateView(View v) {
        Animator animation = AnimatorInflater.loadAnimator(mContext, R.anim.rotate_animation);
        animation.setTarget(v);
        animation.start();
    }

    public void animExpandCollapse(View v, View content, boolean expand) {
        if(expand) {
            rotateHalfFwd(v);
            expand(content);
        } else {
            rotateHalfBack(v);
            collapse(content);
        }
    }

    public void rotateHalfFwd(View v) {
        Animator animation = AnimatorInflater.loadAnimator(mContext, R.anim.rotate_half_fw_anim);
        animation.setTarget(v);
        animation.start();
    }

    public void rotateHalfBack(View v) {
        Animator animation = AnimatorInflater.loadAnimator(mContext, R.anim.rotate_half_bk_anim);
        animation.setTarget(v);
        animation.start();
    }

    public void expand(final View v) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                                : (int)(targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targtetHeight / v.getContext().getResources().getDisplayMetrics().density)/2);
        v.startAnimation(a);
    }

    public void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density)/2);
        v.startAnimation(a);
    }

    public void loadFragment(Fragment frag) {
        FragmentManager fm = ((Activity)mContext).getFragmentManager();
        Fragment old = fm.findFragmentByTag(Config.FRAGMENT_TAG);
        if(old != null) {
            fm.beginTransaction()
            .remove(old);
        }
        fm.beginTransaction()
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        .replace(R.id.container, frag, Config.FRAGMENT_TAG)
        .commit();
    }

    public void loadChildFragmentAnim(Fragment parent,Fragment frag, int containerId) {
        FragmentTransaction ft = parent.getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(containerId, frag, Config.FRAGMENT_TAG)
        .commit();
    }


    @SuppressWarnings("rawtypes")
    public void startActivity(Activity act, Class c, String bundleValue, String bundleKey) {
        Bundle b = new Bundle();
        b.putString(bundleKey, bundleValue);
        Intent i = new Intent(act, c);
        i.putExtras(b);
        act.startActivity(i);
        act.overridePendingTransition(R.anim.anim_slide_left_in, R.anim.anim_slide_right_out);
    }

    @SuppressWarnings("rawtypes")
    public void startActivity(Activity act, Class c, int bundleValue, String bundleKey) {
        Bundle b = new Bundle();
        b.putInt(bundleKey, bundleValue);
        Intent i = new Intent(act, c);
        i.putExtras(b);
        act.startActivity(i);
        act.overridePendingTransition(R.anim.anim_slide_left_in, R.anim.anim_slide_right_out);
    }
    
    @SuppressWarnings("rawtypes")
    public void startActivity(Activity act, Class c, Bundle b) {
        Intent i = new Intent(act, c);
        i.putExtras(b);
        act.startActivity(i);
        act.overridePendingTransition(R.anim.anim_slide_left_in, R.anim.anim_slide_right_out);
    }

    @SuppressWarnings("rawtypes")
    public void startActivity(Activity act, Class c) {
        Intent i = new Intent(act, c);
        act.startActivity(i);
        act.overridePendingTransition(R.anim.anim_slide_left_in, R.anim.anim_slide_right_out);
    }

    public void finishActivity( Activity act) {
        act.finish();
        act.overridePendingTransition(R.anim.anim_slide_alt_in, R.anim.anim_slide_alt_out);
    }

    public int getViewHeight(View view) {
        view.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }

    public int getGridViewHeight(GridView gv) {

        ListAdapter GvAdapter = gv.getAdapter();
        int listviewElementsheight = 0;
        for (int i = 0; i < GvAdapter.getCount(); i++) {
            View mView = GvAdapter.getView(i, null, gv);
            mView.measure(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            listviewElementsheight += mView.getMeasuredHeight();
        }
        return listviewElementsheight/gv.getNumColumns();
    }


    public Bitmap getApplicationIcon(String packageName, Context context){
        Bitmap appIcon = null;
        try {
            Drawable icon = context.getPackageManager().getApplicationIcon(packageName);
            Bitmap tmp = ((BitmapDrawable)icon).getBitmap();
            int iconSize = context.getResources()
                    .getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
            int appIconSize = context.getResources()
                    .getDimensionPixelSize(android.R.dimen.app_icon_size);
            Bitmap bmpWithBorder = Bitmap.createBitmap(iconSize, iconSize, tmp.getConfig());
            Canvas canvas = new Canvas(bmpWithBorder);
            canvas.drawColor(Color.TRANSPARENT);
            int margin = (iconSize - appIconSize) / 2;
            canvas.drawBitmap(tmp, null, new Rect(margin, margin,
                    appIconSize + margin, appIconSize + margin), null);
            appIcon = bmpWithBorder;
        } catch (NameNotFoundException e) {
            // Ups!
        }

        return appIcon;
    }

    public Drawable getApplicationIconDrawable(String packageName, Context context){    
        try {
            Drawable appIcon = context.getPackageManager().getApplicationIcon(packageName);
            return appIcon;
        } catch (NameNotFoundException e) {
            // Ups!
        }
        return null;
    }

    public String getApplicationName(String packageName, Context context){
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) pm.getApplicationLabel(ai);
        return applicationName;
    }
    
    public Dialog buildDeleteProfileDialog(final Profile mProfile, final ProfilesBaseAdapter mAdapter, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mRes.getString(R.string.delete_profile_title));
        builder.setMessage(mRes.getString(R.string.delete_profile_desc));
        builder.setPositiveButton(mRes.getString(R.string.dialog_button_delete), new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                mDb.deleteProfile(mProfile);
                mAdapter.remove(position);
                dialog.cancel();
            }
        })
        .setNegativeButton(mRes.getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });
        AlertDialog diag = builder.create();
        diag.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = ((AlertDialog) dialog).getButton(Dialog.BUTTON_POSITIVE);
                btnPositive.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));

                Button btnNegative = ((AlertDialog) dialog).getButton(Dialog.BUTTON_NEGATIVE);
                btnNegative.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
            }
        });
        return diag;
    }
    
    public Dialog buildProfileDialog(final int appPos, final AppItem item, final AppsBaseAdapter mListAdapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        ListView list = new ListView(mContext);
        final ProfileDialogBaseAdapter mAdapter = new ProfileDialogBaseAdapter(mContext, mDb.getAllProfiles());
        builder.setView(list);
        list.setAdapter(mAdapter);
        
        if(item.haveProfile){
            builder.setPositiveButton(mRes.getString(R.string.dialog_button_remove), new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    AppProfile mApp = item.mProfile;
                    mDb.deleteAppProfile(mApp);
                    mListAdapter.removeAppProfile(appPos);
                }
            });
        }
        
        final AlertDialog diag = builder.create();
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                // TODO Auto-generated method stub
                Profile mProfile = mAdapter.getItem(arg2);
                AppProfile mApp = mDb.saveAppProfile(item.packageName, mProfile);
                mListAdapter.updateItem(appPos, mApp);
                diag.cancel();
            }
            
        });
        return diag;
    }

}
