package com.dsht.kerneltweaker;

import java.util.ArrayList;

import com.dsht.kerneltweaker.database.Category;
import com.dsht.kerneltweaker.database.DatabaseHelpers;
import com.dsht.kerneltweaker.utils.Helpers;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class Config {

    private static Config mConfig;
    private Context mContext;
    private Category mBootCategory;
    public static final String FRAGMENT_TAG = "fragment";

    public static final String BROADCAST_INTENT = "com.dsht.kerneltweaker.APPLICATION_START";
    public static final String BROADCAST_EXTRA_KEY = "extra_key_send";

    public static final String BUNDLE_PROFILE_NAME = "name";
    public static final String BUNDLE_PROFILE_MAX = "max_freq";
    public static final String BUNDLE_PROFILE_MIN = "min_freq";
    public static final String BUNDLE_PROFILE_GOVERNOR = "governor";

    public static final String PERAPP_SERVICE_ID = "com.dsht.kerneltweaker/.services.PerAppService";


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
    public static final String KEY_PERAPP = "key_perapp";

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
    public static final String KEY_KERNEL_CATEGORY_LOG = "key_kernel_logging";
    public static final String KEY_KERNEL_CATEGORY_IO = "key_kernel_io";
    public static final String KEY_KERNEL_CATEGORY_POWER = "key_kernel_power";
    public static final String KEY_KERNEL_CATEGORY_NET = "key_kernel_net";
    public static final String KEY_TEMP_THRESHOLD = "key_temp_threshold";
    public static final String KEY_KERNEL_CATEGORY_FEATURES ="key_kernel_features";
    public static final String KEY_INTELLIPLUG = "key_intelliplug";


    //PER-APP
    public static final String KEY_CPU_MAX_FREQ_PROFILE = "key_cpu_max_freq_profile";
    public static final String KEY_CPU_MIN_FREQ_PROFILE = "key_cpu_min_freq_profile";
    public static final String KEY_CPU_GOVERNOR_PROFILE = "key_cpu_governor_profile";


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
        mContext = context;
        mConfig = new Config();
        SharedPreferences prefs = getSharedPreferences(context);
        Resources res = context.getResources();
        mBootCategory = new DatabaseHelpers().saveCategory(1,BOOT_TABLE);

    }


    /////////////////
    // - GETTERS - //
    /////////////////

    public String[] getCpuFreqValues() {
        return Helpers.getFrequencies();
    }

    public String[] getCpuFreqEntries() {
        return Helpers.getFreqToMhz(CPU_FREQ_FILE, 1000);
    }

    public String[] getCpuGovernors() {
        return Helpers.getGovernors();
    }

    public String getCurrentMaxFreq() {
        return Helpers.readOneLine(MAX_FREQ_FILE);
    }

    public String getCurrentMinFreq() {
        return Helpers.readOneLine(MIN_FREQ_FILE);
    }

    public String getCurrentGovernor() {
        return Helpers.readOneLine(GOVERNOR_FILE);
    }

    public String getCurrentGpuMaxFreq() {
        return Helpers.readOneLine(GPU_MAX_FREQ_FILE);
    }

    public String getCurrentUpThreshold() {
        return Helpers.readOneLine(GPU_UP_THRESHOLD);
    }

    public String getCurrentDownThreshold() {
        return Helpers.readOneLine(GPU_DOWN_THRESHOLD);
    }

    public String[] getGpuEntries() {
        return Helpers.getFreqToMhz(GPU_FREQUENCIES_FILE, 1000000);
    }

    public String[] getGpuValues() {
        return Helpers.readOneLine(GPU_FREQUENCIES_FILE).split("\\w+");
    }

    public String getUvPath() {
        return Helpers.checkUVTableType();
    }

    public String getLogcatValue() {
        return this.mLogcatValue;
    }

    public void saveOption(Context context, String key, Object value,
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

    }

    public SharedPreferences getSharedPreferences(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c);
    }

    public interface OnConfigChangedListener {
        public void onConfigChanged(Config config, String key, Object value);
    }


    public Category getBootCategory() {
        if(mBootCategory != null) {
            return mBootCategory;
        }
        return new DatabaseHelpers().saveCategory(1,BOOT_TABLE);
    }

    public String[] getArray(int resID) {
        return mContext.getResources().getStringArray(resID);
    }

    public int getColor(int resID) {
        return mContext.getResources().getColor(resID);
    }

}
