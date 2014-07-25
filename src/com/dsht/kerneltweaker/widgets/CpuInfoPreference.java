package com.dsht.kerneltweaker.widgets;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.utils.CPUStateMonitor;
import com.dsht.kerneltweaker.utils.Helpers;
import com.dsht.kerneltweaker.utils.UiHelpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CpuInfoPreference extends Preference implements OnCheckedChangeListener {
    
    private boolean showContent = false;
    private UiHelpers mUiHelpers;
    private LinearLayout content;
    private Resources res;
    private Config mConfig;
   
    private LayoutInflater mInflater;
    private int mCpuNum = 1;
    private CpuInfoListAdapter mCpuInfoListAdapter;
    private CurCPUThread mCurCPUThread;
    private CurCPUTempThread mCurCPUTempThread;
    private TextView mCpuTemp;
    private TextView mBattTemp;
    private TextView mTitle;
    private CheckBox mExpand;
    
    private CPUStateMonitor monitor = new CPUStateMonitor();
    private Context context;
    private SharedPreferences preferences;
    private List<String> mCpuInfoListData;
    private String CPUTEMP_PATH = "/sys/class/thermal/thermal_zone0/temp";
    public static final String PREF_OFFSETS = "pref_offsets";
    public static final String CPU_PATH = "/sys/devices/system/cpu/cpu";
    public static final String CPU_FREQ_TAIL = "/cpufreq/scaling_cur_freq";

    public CpuInfoPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.preference_info_cpu);
        
        initConfig(context);
    }
    
    public CpuInfoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_info_cpu);
        initConfig(context);
    }
    
    public CpuInfoPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutResource(R.layout.preference_info_cpu);
        initConfig(context);
    }
    
    @Override
    public View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        View view = inflater.inflate(R.layout.preference_info_cpu, parent, false);

        context = getContext();
        context.registerReceiver(mBatInfoReceiver, 
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        loadOffsets();
        mCpuNum = Helpers.getNumOfCpus();
        
        mCpuTemp = (TextView) view.findViewById(R.id.temp_cpu_value);
        mBattTemp = (TextView) view.findViewById(R.id.temp_batt_value);
        
        TextView statusHeader = (TextView) view.findViewById(R.id.status);
        statusHeader.getBackground().setColorFilter(Color.parseColor("#27af89"), Mode.SRC_ATOP);
        TextView tempHeader = (TextView) view.findViewById(R.id.ui_header_temperatures);
        tempHeader.getBackground().setColorFilter(Color.parseColor("#27af89"), Mode.SRC_ATOP);
        
        ////
        mInflater = inflater;
        mCpuInfoListData = new ArrayList<String>(mCpuNum);
        for (int i = 0; i < mCpuNum; i++) {
            mCpuInfoListData.add("Core " + String.valueOf(i) + ": ");
        }

        mCpuInfoListAdapter = new CpuInfoListAdapter(
                context, android.R.layout.simple_list_item_1, mCpuInfoListData);
        
        return view;
        
    }
    
    @Override
    public void onBindView(View view) {
        super.onBindView(view);

        content = (LinearLayout) view.findViewById(R.id.content_layout);
        mExpand = (CheckBox) view.findViewById(R.id.checkBox1);
        mTitle = (TextView) view.findViewById(R.id.cb_title);
 
        Log.d("INFO", "LOADING CONFIG");
        mConfig.load();  
        
        mExpand.setOnCheckedChangeListener(this);
        
        expandCollapse();
        
        ListView mCpuInfoList = (ListView) view.findViewById(R.id.cpu_info_list);
        mCpuInfoList.setAdapter(mCpuInfoListAdapter);
        mCurCPUThread = new CurCPUThread();
        mCurCPUThread.start();
        mCurCPUTempThread = new  CurCPUTempThread();
        mCurCPUTempThread.start();
    }
    
    
    private void initConfig(Context context) {
        mUiHelpers = new UiHelpers(context);
        mConfig = Config.getInstance();
        res = getContext().getResources();
    }
    
    public void setShouldExpand(boolean expand) {
        mExpand.setChecked(expand);
    }
    
    public boolean isExpanded() {
        return this.showContent;
    }
    
    
    @Override
    public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
        mUiHelpers.animExpandCollapse(arg0,content, isChecked);
        showContent = isChecked;
        expandCollapse();
        if(isChecked) {
            mTitle.setText(R.string.cpu_info_cb_title_hide);
            onResume();
        }else{
            mTitle.setText(R.string.cpu_info_cb_title_show);
            onPause();
        }
        
    }
    
    
    public void onResume() {
        if (mCurCPUThread == null) {
            mCurCPUThread = new CurCPUThread();
            mCurCPUThread.start();
        }
        if(mCurCPUTempThread == null) {
            mCurCPUTempThread = new  CurCPUTempThread();
            mCurCPUTempThread.start();
        }
        Log.d("ONRESUME", "resuming thread");
    }


    public void onPause() {

        if (mCurCPUThread != null) {
            if (mCurCPUThread.isAlive()) {
                mCurCPUThread.interrupt();
            }

            mCurCPUThread = null;
        }
        if (mCurCPUTempThread != null) {
            if (mCurCPUTempThread.isAlive()) {
                mCurCPUTempThread.interrupt();
            }

            mCurCPUTempThread = null;
        }
        Log.d("ONPAUSE", "pausing thread");
    }
    
    
    
    public void setExpanded(boolean expanded) {
        this.showContent = expanded;
        notifyChanged();
    }
    
    public void expandCollapse() {
        if(showContent) {
            mUiHelpers.expand(content);
        }else {
            mUiHelpers.collapse(content);
        }
    }
    
    public class CpuInfoListAdapter extends ArrayAdapter<String> {

        public CpuInfoListAdapter(Context context, int resource, List<String> values) {
            super(context, R.layout.cpu_info_item, resource, values);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = mInflater.inflate(R.layout.cpu_info_item, parent, false);
            TextView cpuInfoCore = (TextView) rowView.findViewById(R.id.cpu_info_core);
            TextView cpuInfoFreq = (TextView) rowView.findViewById(R.id.cpu_info_freq);
            cpuInfoCore.setText(getContext().getString(R.string.core) + " " + String.valueOf(position) + ": ");
            cpuInfoFreq.setText(mCpuInfoListData.get(position));
            return rowView;
        }
    }
    
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
          int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
          double temp = temperature /10.0;
          mBattTemp.setText(temp+ " °C");
        }
      };


      public void loadOffsets() {
          String prefs = preferences.getString(PREF_OFFSETS, "");
          if (prefs == null || prefs.length() < 1) {
              return;
          }

          Map<Integer, Long> offsets = new HashMap<Integer, Long>();
          String[] sOffsets = prefs.split(",");
          for (String offset : sOffsets) {
              String[] parts = offset.split(" ");
              offsets.put(Integer.parseInt(parts[0]), Long.parseLong(parts[1]));
          }

          monitor.setOffsets(offsets);
      }

      public void saveOffsets() {
          SharedPreferences.Editor editor = preferences.edit();
          String str = "";
          for (Map.Entry<Integer, Long> entry : monitor.getOffsets().entrySet()) {
              str += entry.getKey() + " " + entry.getValue() + ",";
          }
          editor.putString(PREF_OFFSETS, str).commit();
      }


      protected class CurCPUThread extends Thread {
          private boolean mInterrupt = false;

          public void interrupt() {
              mInterrupt = true;
          }

          @Override
          public void run() {
              try {
                  while (!mInterrupt) {
                      sleep(1000);
                      List<String> freqs = new ArrayList<String>();
                      for (int i = 0; i < mCpuNum; i++) {
                          String cpuFreq = CPU_PATH + String.valueOf(i) + CPU_FREQ_TAIL;
                          String curFreq = "0";
                          if (Helpers.fileExists(cpuFreq)) {
                              curFreq = Helpers.readOneLine(cpuFreq);
                          }
                          freqs.add(curFreq);
                      }
                      String[] freqArray = freqs.toArray(new String[freqs.size()]);
                      mCurCPUHandler.sendMessage(mCurCPUHandler.obtainMessage(0, freqArray));
                      
                  }
              } catch (InterruptedException e) {
                  //return;
              }
          }
      }

      protected Handler mCurCPUHandler = new Handler() {
          public void handleMessage(Message msg) {
              String[] freqArray = (String[]) msg.obj;
              for (int i = 0; i < freqArray.length; i++) {
                  // Convert freq in MHz
                  try {
                      int freqHz = Integer.parseInt(freqArray[i]);

                      if (freqHz == 0) {
                          mCpuInfoListData.set(i, context.getString(R.string.core_offline));
                      } else {
                          mCpuInfoListData.set(i, Integer.toString(freqHz / 1000) + " MHz");
                      }
                  } catch (Exception e) {
                      // Do nothing
                  }
              }
              mCpuInfoListAdapter.notifyDataSetChanged();
          }
          
          
      };
      protected class CurCPUTempThread extends Thread {
          private boolean mInterrupt = false;

          public void interrupt() {
              mInterrupt = true;
          }

          @Override
          public void run() {
              try {
                  while (!mInterrupt) {
                      sleep(1000);
                      String[] tempArray = new String[1];
                      tempArray[0] = Helpers.getFileContent(new File(CPUTEMP_PATH));
                      mCurCPUTempHandler.sendMessage(mCurCPUTempHandler.obtainMessage(0, tempArray));
                  }
              } catch (InterruptedException e) {
                  //return;
              }
          }
      }

      protected Handler mCurCPUTempHandler = new Handler() {
          public void handleMessage(Message msg) {
              String[] tempArray = (String[]) msg.obj;
              String temp = tempArray[0];
              mCpuTemp.setText(temp+" °C");
          }
          
          
      };

}
