package com.dsht.kerneltweaker;

import java.io.File;
import java.util.ArrayList;

import com.dsht.kerneltweaker.database.Category;
import com.dsht.kerneltweaker.database.DatabaseHelpers;
import com.dsht.kerneltweaker.utils.Helpers;
import com.dsht.kerneltweaker.utils.UvItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class Config {

    private static Config mConfig;
    private ArrayList<OnConfigChangedListener> mListeners;
    private Context mContext;
    private Category mBootCategory;
    public static final String FRAGMENT_TAG = "fragment";
    public static final String MAKER_TYPE = "maker_type";
    public static final int MAKER_TYPE_MAKE = 0;
    public static final int MAKER_TYPE_EDIT = 1;
    public static final String HEADER = "--header--";
    
    
    //EXTRAS DATA 
    public static final String EXTRA_FILES_DIR = "extra_file_dir";
    
    //ROOT PREFERENCE SCREEN
    public static final String KEY_ROOT = "key_root";
    

    //BOOT TABLE
    public static final String BOOT_TABLE="Boot_Table";

    //UV TABLE TYPES
    public static final int TYPE_VDD = 0;
    public static final int TYPE_UV_MV_TABLE = 1;
    public static final int TYPE_NOT_FOUND = -1;
    
    //CPU Fragment files
    public static final String CPU_FREQ_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    public static final String MAX_FREQ_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
    public static final String GOVERNOR_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
    public static final String MIN_FREQ_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
    public static final String GOVERNOR_FILES_DIR = "/sys/devices/system/cpu/cpufreq/";
    public static final String HOTPLUG_FOLDER ="/sys/class/misc/mako_hotplug_control/"; 
    public static final String CPUQUIET_DIR = "/sys/devices/system/cpu/cpuquiet";
    public static final String CPUQUIET_FILE = "/sys/devices/system/cpu/cpuquiet/current_governor";
    public static final String CPUQUIET_GOVERNORS = "/sys/devices/system/cpu/cpuquiet/available_governors";
    public static final String CORE_1_PATH ="/sys/devices/system/cpu/cpu1/online";
    public static final String CORE_2_PATH ="/sys/devices/system/cpu/cpu2/online";
    public static final String CORE_3_PATH ="/sys/devices/system/cpu/cpu3/online";
    public static final String MAX_FREQ_COMMAND_PATH = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_max_freq";
    public static final String MIN_FREQ_COMMAND_PATH ="/sys/devices/system/cpu/cpu%d/cpufreq/scaling_min_freq";
    public static final String MAX_FREQ_COMMAND = "echo \'%s\' > /sys/devices/system/cpu/cpu%d/cpufreq/scaling_max_freq";
    public static final String MIN_FREQ_COMMAND = "echo \'%s\' > /sys/devices/system/cpu/cpu%d/cpufreq/scaling_min_freq";


    //GPU Fragment files
    public static final String GPU_FREQUENCIES_FILE = "/sys/class/kgsl/kgsl-3d0/gpu_available_frequencies";
    public static final String GPU_MAX_FREQ_FILE = "/sys/class/kgsl/kgsl-3d0/max_gpuclk";
    public static final String GPU_UP_THRESHOLD = "/sys/module/msm_kgsl_core/parameters/up_threshold";
    public static final String GPU_DOWN_THRESHOLD = "/sys/module/msm_kgsl_core/parameters/down_threshold";
    
    
    //UNDERVOLT
    public static final String VDD_TABLE_PATH = "/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels";
    public static final String UV_TABLE_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table";


    //Kernel Fragment files
    public static final String LOGCAT = "/sys/module/logger/parameters/enabled";
    public static final String FSYNC_FILE = "/sys/module/sync/parameters/fsync_enabled";
    public static final String SCHEDULER_FILE = "/sys/block/mmcblk0/queue/scheduler";
    public static final String READ_AHEAD_FILE = "/sys/block/mmcblk0/queue/read_ahead_kb";
    public static final String FCHARGE_FILE = "/sys/kernel/fast_charge/force_fast_charge";
    public static final String TEMP_FILE = "/sys/module/msm_thermal/parameters/temp_threshold";

    public static final String SOUNDCONTROL_FILE = "/sys/kernel/sound_control_3";
    public static final String HEADSET_BOOST_FILE = "/sys/devices/virtual/misc/soundcontrol/headset_boost";
    public static final String MIC_BOOST_FILE = "/sys/devices/virtual/misc/soundcontrol/mic_boost";
    public static final String SPEAKER_BOOST_FILE = "/sys/devices/virtual/misc/soundcontrol/speaker_boost";
    public static final String VOLUME_BOOST_FILE = "/sys/devices/virtual/misc/soundcontrol/volume_boost";
    public static final String TCP_OPTIONS = "sysctl net.ipv4.tcp_available_congestion_control";
    public static final String TCP_COMMAND_REGEX = "sysctl -w net.ipv4.tcp_congestion_control=%s";
    public static final String TCP_CURRENT = "sysctl net.ipv4.tcp_congestion_control";
    public static final String DT2W_FILE = "/sys/android_touch/doubletap2wake";
    public static final String S2W_FILE = "/sys/android_touch/sweep2wake";
    public static final String S2W_SLEEPONLY_FILE = "/sys/android_touch/s2w_s2sonly";
    public static final String INTELLIPLUG_FILE = "/sys/module/intelli_plug/parameters/intelli_plug_active";
    public static final String ECOMODE_FILE = "/sys/module/intelli_plug/parameters/eco_mode_active";
    public static final String DYNFSYNC_FILE = "/sys/kernel/dyn_fsync/Dyn_fsync_active";
    public static final String FAUXSOUND_FILE = "/sys/kernel/sound_control_3";
    public static final String VIBRATION_FILE = "/sys/class/timed_output/vibrator/amp";
    public static final String F2S_FILE = "sys/devices/virtual/htc_g_sensor/g_sensor/flick2sleep";
    public static final String F2W_FILE = "sys/devices/virtual/htc_g_sensor/g_sensor/flick2wake";

    //UV Files
    public static final String UV_TABLE_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table";
    public static final String UV_VDD_TABLE = "/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels";

    //HEADERS
    public static final String KEY_STATS_CPU = "key_stats_cpu";
    public static final String KEY_CPU = "key_cpu";
    public static final String KEY_GPU = "key_gpu";
    public static final String KEY_UV = "key_uv";
    public static final String KEY_KERNEL = "key_kernel";
    public static final String KEY_USER = "key_user";
    public static final String KEY_HEADER_STATS = "key_header_stats";
    public static final String KEY_HEADER_DEVICE = "key_header_device";
    public static final String KEY_HEADER_CUSTOM = "key_header_custom";

    //CPU FRAGMENT
    public static final String KEY_CPU_MAX_FREQ = "key_cpu_max_freq";
    public static final String KEY_CPU_MIN_FREQ = "key_cpu_min_freq";
    public static final String KEY_CPU_GOVERNOR = "key_cpu_governor";
    public static final String KEY_ADVANCED_GOVERNOR = "key_advanced_governor";
    public static final String KEY_ADVANCED_HOTPLUG = "key_advanced_hotplug";
    public static final String KEY_CPU_INFO = "key_cpu_info";
    public static final String KEY_CPU_CORE1 = "key_core_1";
    public static final String KEY_CPU_CORE2 = "key_core_2";
    public static final String KEY_CPU_CORE3 = "key_core_3";
    public static final String KEY_CPU_MPDEC = "key_mpdec";
    public static final String KEY_CORES_CATEGORY ="key_cores_category";
    public static final String KEY_CPU_ADVANCED_CATEGORY="key_advanced_category";
    
    //GPU FRAGMENT
    public static final String KEY_GPU_MAX_FREQ = "key_gpu_max_freq";
    public static final String KEY_GPU_UP_THRESHOLD = "key_gpu_up_threshold";
    public static final String KEY_GPU_DOWN_THRESHOLD = "key_gpu_down_threshold";
    
    //KERNEL FRAGMENT
    public static final String KEY_LOGCAT = "key_logcat";
    public static final String KEY_FSYNC = "key_fsync";
    public static final String KEY_SCHED = "key_sched";
    public static final String KEY_READ_AHEAD = "key_read_ahead";
    public static final String KEY_FCHARGE = "key_fcharge";
    public static final String KEY_TCP = "key_tcp";
    public static final String KEY_KERNEL_CATEGORY_IO = "key_kernel_io";
    public static final String KEY_KERNEL_CATEGORY_POWER = "key_kernel_power";
    public static final String KEY_KERNEL_CATEGORY_NET = "key_kernel_net";

    //CPU VALUES
    private String mCpuMaxFreq;
    private String mCpuMinFreq;
    private String mCpuGovernor;
    
    private String[] mCpuFreqValues;
    private String[] mCpuFreqEntries;
    private String[] mCpuGovernors;
    private String curMaxFreq;
    private String curMinFreq;
    private String curGovernor;
    
    //CPU VALUES
    private String mGpuMaxFreq;
    private String mGpuUpThreshold;
    private String mGpuDownThreshold;
    private String[] mGpuEntries;
    private String[] mGpuValues;
    
    //UV
    private ArrayList<String> uvValues = new ArrayList<String>();
    private String uvPath;
    private int UvType;
    
    
    //KERNEL
    private String mLogcatValue;
    


    private Config() {
        // unused //
    }

    public static synchronized Config getInstance() {
        if (mConfig == null) {
            mConfig = new Config();
        }
        return mConfig;
    }

    void init(Context context) {
        mConfig = new Config();
        SharedPreferences prefs = getSharedPreferences(context);
        Resources res = context.getResources();
        mListeners = new ArrayList<OnConfigChangedListener>();
        mBootCategory = new DatabaseHelpers().saveCategory(1,BOOT_TABLE);

        mCpuMaxFreq = prefs.getString(KEY_CPU_MAX_FREQ, 
                res.getString(R.string.cpu_max_freq_desc));
        mCpuMinFreq = prefs.getString(KEY_CPU_MIN_FREQ, 
                res.getString(R.string.cpu_min_freq_desc));
        mCpuGovernor = prefs.getString(KEY_CPU_GOVERNOR, 
                res.getString(R.string.cpu_governor_desc));

    }
    
    
    public void load() {
        class LoadData extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... arg0) {
                // TODO Auto-generated method stub
                mCpuFreqValues = Helpers.getFrequencies();
                mCpuFreqEntries = Helpers.getFreqToMhz(CPU_FREQ_FILE, 1000);
                mCpuGovernors = Helpers.getGovernors();
                curMaxFreq = Helpers.readOneLine(MAX_FREQ_FILE);
                curMinFreq = Helpers.readOneLine(MIN_FREQ_FILE);
                curGovernor = Helpers.readOneLine(GOVERNOR_FILE);
                mGpuMaxFreq = Helpers.readOneLine(GPU_MAX_FREQ_FILE);
                mGpuUpThreshold = Helpers.readOneLine(GPU_UP_THRESHOLD);
                mGpuDownThreshold = Helpers.readOneLine(GPU_DOWN_THRESHOLD);
                mGpuEntries = Helpers.getFreqToMhz(GPU_FREQUENCIES_FILE, 1000000);
                String gpuFreqs =  Helpers.readOneLine(GPU_FREQUENCIES_FILE);
                if(gpuFreqs != null) {
                   mGpuValues = gpuFreqs.split("\\w+");
                }
                uvPath = Helpers.checkUVTableType();
                
                mLogcatValue = Helpers.readOneLine(LOGCAT);
                return null;
            }
            
        }
        new LoadData().execute();
        
    }


    /////////////////
    // - SETTERS - //
    /////////////////

    public void setCpuMaxFreq(Context context, String value, OnConfigChangedListener listener) {
        saveOption(context, KEY_CPU_MAX_FREQ, value, listener, mCpuMaxFreq != (mCpuMaxFreq = value));
    }

    public void setCpuMinFreq(Context context, String value, OnConfigChangedListener listener) {
        saveOption(context, KEY_CPU_MIN_FREQ, value, listener, mCpuMinFreq != (mCpuMinFreq = value));
    }

    public void setCpuGovernor(Context context, String value, OnConfigChangedListener listener) {
        saveOption(context, KEY_CPU_GOVERNOR, value, listener, mCpuGovernor != (mCpuGovernor = value));
    }


    /////////////////
    // - GETTERS - //
    /////////////////
    
    public String getCpuMaxFreq() {
        return this.mCpuMaxFreq;
    }
    
    public String getCpuMinFreq() {
        return this.mCpuMinFreq;
    }
    
    public String getCpuGovernor() {
        return this.mCpuGovernor;
    }
    
    public String[] getCpuFreqValues() {
        return this.mCpuFreqValues;
    }
    
    public String[] getCpuFreqEntries() {
        return this.mCpuFreqEntries;
    }
    
    public String[] getCpuGovernors() {
        return this.mCpuGovernors;
    }
    
    public String getCurrentMaxFreq() {
        return this.curMaxFreq;
    }
    
    public String getCurrentMinFreq() {
        return this.curMinFreq;
    }
    
    public String getCurrentGovernor() {
        return this.curGovernor;
    }
    
    public String getCurrentGpuMaxFreq() {
        return this.mGpuMaxFreq;
    }
    
    public String getCurrentUpThreshold() {
        return this.mGpuUpThreshold;
    }
    
    public String getCurrentDownThreshold() {
        return this.mGpuDownThreshold;
    }
    
    public String[] getGpuEntries() {
        return this.mGpuEntries;
    }
    
    public String[] getGpuValues() {
        return this.mGpuValues;
    }
    
    public ArrayList<String> getUvValuesList() {
        return this.uvValues;
    }
    
    public String getUvPath() {
        if(uvPath == null) {
            return Helpers.checkUVTableType();
        }
        return this.uvPath;
    }
    
    public String getLogcatValue() {
        return this.mLogcatValue;
    }

    private void saveOption(Context context, String key, Object value,
            OnConfigChangedListener listener, boolean changed) {
        if (!changed) {
            // Don't update preferences if this change is a lie.
            return;
        }

        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if(value instanceof String) {
            editor.putString(key, (String)value);
        }else if (value instanceof Long) {
            editor.putLong(key, (Long)value);
        }else throw new IllegalArgumentException("Unknown option type.");
        editor.commit();

        mContext = context;
        notifyConfigChanged(key, value, listener);
        mContext = null;
    }

    private void notifyConfigChanged(String key, Object value, OnConfigChangedListener listener) {
        for (OnConfigChangedListener l : mListeners) {
            if (l == listener) continue;
            l.onConfigChanged(this, key, value);
        }
    }

    public SharedPreferences getSharedPreferences(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c);
    }

    public interface OnConfigChangedListener {
        public void onConfigChanged(Config config, String key, Object value);
    }

    public void addOnConfigChangedListener(OnConfigChangedListener listener) {
        mListeners.add(listener);
    }

    public void removeOnConfigChangedListener(OnConfigChangedListener listener) {
        mListeners.remove(listener);
    }

    public Category getBootCategory() {
        if(mBootCategory != null) {
            return mBootCategory;
        }
        return new DatabaseHelpers().saveCategory(1,BOOT_TABLE);
    }

}
