package com.dsht.kerneltweaker.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.database.AppProfile;
import com.dsht.kerneltweaker.database.DatabaseHelpers;
import com.dsht.kerneltweaker.database.Profile;
import com.dsht.kernetweaker.cmdprocessor.CMDProcessor;
import com.dsht.kernetweaker.cmdprocessor.CMDProcessor.CommandResult2;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

public class Helpers {

    private static final String FREQ_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    private static final String GOVERNOR_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
    static String[] error = {"error while reading frequencies. Please reload this page"};

    public static String[]  getFrequencies() {
        //setPermissions(FREQ_FILE);
        File freqfile = new File(FREQ_FILE);
        FileInputStream fin1 = null;
        byte fileContent[] = null;
        try {
            fin1 = new FileInputStream(freqfile);
            fileContent = new byte[(int)freqfile.length()];
            fin1.read(fileContent);
        }
        catch (FileNotFoundException e1) {
            //System.out.println("File not found" + e1);
        }
        catch (IOException ioe1) {
            //System.out.println("Exception while reading file " + ioe1);
        }
        finally {
            try {
                if (fin1 != null) {
                    fin1.close();
                }
            }
            catch (IOException ioe1) {
                //System.out.println("Error while closing stream: " + ioe1);
            }
        }
        return new String(fileContent).trim().split(" ");
    }
    /*
    public static void setPermissions(String file) {
        if(new File(file).exists()) {
            CommandCapture command = new CommandCapture(0, "chmod 655 "+file);
            try {
                RootTools.getShell(true).add(command);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (TimeoutException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (RootDeniedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
     */
    public static String binExist(String b) {
        CommandResult2 cr = null;
        cr = new CMDProcessor().sh.runWaitFor("busybox which " + b);
        if (cr.success()) {
            return cr.stdout;
        } else {
            return "NOT_FOUND";
        }
    }

    public static boolean writeOneLine(String fname, String value) {
        if (!new File(fname).exists()) {
            return false;
        }
        try {
            FileWriter fw = new FileWriter(fname);
            try {
                fw.write(value);
            } finally {
                fw.close();
            }
        } catch (IOException e) {
            String Error = "Error writing to " + fname + ". Exception: ";
            Log.e("TAG", Error, e);
            return false;
        }
        return true;
    }

    public static void runRootCommand(String command) {
        CommandCapture comm = new CommandCapture(0, command);
        try {
            RootTools.getShell(true).add(comm);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RootDeniedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static int getNumOfCpus() {
        int numOfCpu = 1;
        //setPermissions("/sys/devices/system/cpu/present");
        String numOfCpus = Helpers.readFileViaShell("/sys/devices/system/cpu/present", false);
        String[] cpuCount = numOfCpus.trim().split("-");
        Log.d("NUM", numOfCpus+ "----"+cpuCount.length);
        if (cpuCount.length > 1) {
            try {
                int cpuStart = Integer.parseInt(cpuCount[0]);
                int cpuEnd = Integer.parseInt(cpuCount[1]);

                numOfCpu = cpuEnd - cpuStart + 1;
                Log.d("NUM", numOfCpu+"");

                if (numOfCpu < 0) {
                    numOfCpu = 1;
                    Log.d("NUM", "ONE");
                }

            } catch (NumberFormatException ex) {
                numOfCpu = 1;
                Log.d("NUM", "ERROR");
            }
        }
        return numOfCpu;
    }

    public static String shExec(StringBuilder s, Context c, Boolean su) {
        get_assetsScript("run", c, s.toString(), "");
        if (isSystemApp(c)) {
            new CMDProcessor().sh.runWaitFor("busybox chmod 750 " + c.getFilesDir() + "/run");
        } else {
            new CMDProcessor().su.runWaitFor("busybox chmod 750 " + c.getFilesDir() + "/run");
        }
        CommandResult2 cr = null;
        if (su && !isSystemApp(c))
            cr = new CMDProcessor().su.runWaitFor(c.getFilesDir() + "/run");
        else
            cr = new CMDProcessor().sh.runWaitFor(c.getFilesDir() + "/run");
        if (cr.success()) {
            return cr.stdout;
        } else {
            Log.d("TAG", "execute: " + cr.stderr);
            return null;
        }
    }

    public static void get_assetsScript(String fn, Context c, String prefix, String postfix) {
        byte[] buffer;
        final AssetManager assetManager = c.getAssets();
        try {
            InputStream f = assetManager.open(fn);
            buffer = new byte[f.available()];
            f.read(buffer);
            f.close();
            final String s = new String(buffer);
            final StringBuilder sb = new StringBuilder(s);
            if (!postfix.equals("")) {
                sb.append("\n\n").append(postfix);
            }
            if (!prefix.equals("")) {
                sb.insert(0, prefix + "\n");
            }
            sb.insert(0, "#!" + Helpers.binExist("sh") + "\n\n");
            try {
                FileOutputStream fos;
                fos = c.openFileOutput(fn, Context.MODE_PRIVATE);
                fos.write(sb.toString().getBytes());
                fos.close();

            } catch (IOException e) {
                Log.d("TAG", "error write " + fn + " file");
                e.printStackTrace();
            }

        } catch (IOException e) {
            Log.d("TAG", "error read " + fn + " file");
            e.printStackTrace();
        }
    }

    public static void restart(final Activity activity) {
        if (activity == null)
            return;
        final int enter_anim = android.R.anim.fade_in;
        final int exit_anim = android.R.anim.fade_out;
        activity.overridePendingTransition(enter_anim, exit_anim);
        activity.finish();
        activity.overridePendingTransition(enter_anim, exit_anim);
        activity.startActivity(activity.getIntent());
    }


    public static String[]  getFrequenciesNames() {
        ArrayList<String> names = new ArrayList<String>();
        //setPermissions(FREQ_FILE);
        File freqfile = new File(FREQ_FILE);
        FileInputStream fin1 = null;
        String s1 = null;
        try {
            fin1 = new FileInputStream(freqfile);
            byte fileContent[] = new byte[(int)freqfile.length()];
            fin1.read(fileContent);
            s1 = new String(fileContent);
        }
        catch (FileNotFoundException e1) {
            //System.out.println("File not found" + e1);
        }
        catch (IOException ioe1) {
            //System.out.println("Exception while reading file " + ioe1);
        }
        finally {
            try {
                if (fin1 != null) {
                    fin1.close();
                }
            }
            catch (IOException ioe1) {
                //System.out.println("Error while closing stream: " + ioe1);
            }
        }
        if(s1 != null) {
            String[] frequencies = s1.trim().split(" ");
            for(String s : frequencies) {
                int conv = (Integer.parseInt(s) / 1000);
                names.add(conv + " Mhz");
            }
            String[] toMhz = new String[names.size()];
            toMhz = names.toArray(toMhz);
            return toMhz;
        }else {
            return error;
        }
    }


    public static String[]  getFreqToMhz(String file, int how) {
        if(fileExists(file)) {
            ArrayList<String> names = new ArrayList<String>();
            //setPermissions(file);
            File freqfile = new File(file);
            FileInputStream fin1 = null;
            byte fileContent[] = null;
            try {
                fin1 = new FileInputStream(freqfile);
                fileContent = new byte[(int)freqfile.length()];
                fin1.read(fileContent);
            }
            catch (FileNotFoundException e1) {
                //System.out.println("File not found" + e1);
            }
            catch (IOException ioe1) {
                //System.out.println("Exception while reading file " + ioe1);
            }
            finally {
                try {
                    if (fin1 != null) {
                        fin1.close();
                    }
                }
                catch (IOException ioe1) {
                    //System.out.println("Error while closing stream: " + ioe1);
                }
            }
            for(String s : new String(fileContent).trim().split(" ")) {
                names.add((Integer.parseInt(s) / how) + " Mhz");
            }
            String[] toMhz = new String[names.size()];
            return names.toArray(toMhz);
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static String[] getGovernors() {
        //setPermissions(GOVERNOR_FILE);
        File govfile = new File(GOVERNOR_FILE);
        FileInputStream fin = null;
        byte fileContent[] = null;
        try {
            fin = new FileInputStream(govfile);
            fileContent = new byte[(int)govfile.length()];
            fin.read(fileContent);
        }
        catch (FileNotFoundException e) {
        }
        catch (IOException ioe) {
            //System.out.println("Exception while reading file " + ioe);
        }
        finally {
            try {
                fin.close();
            }
            catch (IOException ioe) {
                //System.out.println("Error while closing stream: " + ioe);
            }
        }
        return new String(fileContent).trim().split(" ");
    }
    @Deprecated
    public static String[] getUvTableNames() {
        ArrayList<String> Tokens = new ArrayList<String>();

        try {
            FileInputStream fstream = null;
            File f = new File("/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels");
            if(f.exists()) {
                fstream = new FileInputStream("/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels");
            } 
            else {
                File ff = new File("/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table");
                if(ff.exists()) {
                    fstream = new FileInputStream("/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table");
                }
            }
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            // Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();

                if ((strLine.length()!=0)) {
                    String[] names = strLine.replaceAll(":", "").split("\\s+");
                    Tokens.add(names[0]);
                }


            }
            // Close the input stream
            in.close();
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        String[] names = new String[Tokens.size()-1];
        return Tokens.toArray(names);
    }


    public static ArrayList<UvItem> getUvTable(ArrayList<String> valuesList, String path) {
        ArrayList<UvItem> list = new ArrayList<UvItem>();
        if(valuesList.size() > 0) {
            valuesList.clear();
        }
        if(path != null) {
            try {
                FileInputStream stream = new FileInputStream(path);
                DataInputStream dataStream = new DataInputStream(stream);
                BufferedReader br = new BufferedReader(new InputStreamReader(dataStream));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    strLine = strLine.trim();

                    if ((strLine.length()!=0)) {
                        String[] row = strLine.replaceAll(":", "").split("\\s+");
                        UvItem item = new UvItem(row[0], row[1]);
                        list.add(item);
                        valuesList.add(row[1]);
                    }


                }
                dataStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;   
        }	    
        return null;
    }

    @Deprecated
    public static String[] getUvValues() {
        ArrayList<String> value = new ArrayList<String>();

        try {
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = null;
            File f = new File("/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels");
            if(f.exists()) {
                fstream = new FileInputStream("/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels");
            } 
            else {
                File ff = new File("/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table");
                if(ff.exists()) {
                    fstream = new FileInputStream("/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table");
                }
            }
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            // Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();

                if ((strLine.length()!=0)) {
                    String[] val = strLine.split("\\s+");
                    value.add(val[1]);
                }


            }
            // Close the input stream
            in.close();
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        String[] values = new String[value.size()-1];
        values = value.toArray(values);
        return values;
    }


    public static String checkUVTableType() {

        if(new File(Config.VDD_TABLE_PATH).exists()) {
            return Config.VDD_TABLE_PATH;
        }else if(new File(Config.UV_TABLE_PATH).exists()) {
            return Config.UV_TABLE_PATH;
        }
        return null;

    }

    public static int checkUVTableTypeInt() {

        if(new File(Config.VDD_TABLE_PATH).exists()) {
            return Config.TYPE_VDD;
        }else if(new File(Config.UV_TABLE_PATH).exists()) {
            return Config.TYPE_UV_MV_TABLE;
        }
        return Config.TYPE_NOT_FOUND;

    }

    public static boolean UvTableExists(String file) {
        File f = new File(file);
        if(f.exists()) {
            return true;
        }
        return false;
    }

    public static String[] getAvailableSchedulers() {
        File iofile = new File("/sys/block/mmcblk0/queue/scheduler"); 
        String s ="";
        FileInputStream fin2 = null;
        try {
            fin2 = new FileInputStream(iofile);
            byte fileContent[] = new byte[(int)iofile.length()];
            fin2.read(fileContent);
            s = new String(fileContent).trim().split("\n")[0];
        }
        catch (FileNotFoundException e) {
            //System.out.println("File not found" + e);
        }
        catch (IOException ioe) {
            //System.out.println("Exception while reading file " + ioe);
        }
        finally {
            try {
                if (fin2 != null) {
                    fin2.close();
                }
            }
            catch (IOException ioe) {
                //System.out.println("Error while closing stream: " + ioe);
            }
        } 
        String[] IOSchedulers = s.replace("[", "").replace("]", "").split(" ");
        return IOSchedulers;
    }

    public static String getCurrentScheduler() {
        File iofile = new File("/sys/block/mmcblk0/queue/scheduler"); 
        String s ="";
        FileInputStream fin2 = null;
        try {
            fin2 = new FileInputStream(iofile);
            byte fileContent[] = new byte[(int)iofile.length()];
            fin2.read(fileContent);
            s = new String(fileContent).trim().split("\n")[0];
        }
        catch (FileNotFoundException e) {
            //System.out.println("File not found" + e);
        }
        catch (IOException ioe) {
            //System.out.println("Exception while reading file " + ioe);
        }
        finally {
            try {
                if (fin2 != null) {
                    fin2.close();
                }
            }
            catch (IOException ioe) {
                //System.out.println("Error while closing stream: " + ioe);
            }
        } 
        int bropen = s.indexOf("[");
        int brclose = s.lastIndexOf("]");
        return s.substring(bropen + 1, brclose);
    }

    public static String getCurrentGovernor() {
        //setPermissions("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
        File govfile = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
        FileInputStream fin = null;
        String s = null;
        try {
            fin = new FileInputStream(govfile);
            byte fileContent[] = new byte[(int)govfile.length()];
            fin.read(fileContent);
            s = new String(fileContent);
        }
        catch (FileNotFoundException e) {
        }
        catch (IOException ioe) {
            //System.out.println("Exception while reading file " + ioe);
        }
        finally {
            try {
                fin.close();
            }
            catch (IOException ioe) {
                //System.out.println("Error while closing stream: " + ioe);
            }
        }

        return s.trim();
    }

    public static String getFileContent( File file) {
        //setPermissions(file.getAbsolutePath());
        FileInputStream fin = null;
        //Log.d("FILE", file.getAbsolutePath());
        byte fileContent[] = null;
        try {
            fin = new FileInputStream(file);
            fileContent = new byte[(int)file.length()];
            fin.read(fileContent);
        }
        catch (FileNotFoundException e) {
        }
        catch (IOException ioe) {
            //System.out.println("Exception while reading file " + ioe);
        }
        finally {
            try {
                if(fin != null) {
                    fin.close();
                    fin = null;
                }
            }
            catch (IOException ioe) {
                //System.out.println("Error while closing stream: " + ioe);
            }
        }
        return new String(fileContent).split("\n")[0];
    }

    public static void waitForMillis(final int millis, Context context) {
        Thread thread=  new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this){
                        wait(millis);
                    }
                }
                catch(InterruptedException ex){                    
                }

                // TODO              
            }
        };

        thread.start();
    }


    public static String readOneLine(String fname) {
        if(fileExists(fname)) {
            BufferedReader br = null;
            String line = null;
            try {
                br = new BufferedReader(new FileReader(fname), 1024);
                line = br.readLine();
            } catch (FileNotFoundException ignored) {
                Log.d("TAG", "File was not found! trying via shell...");
                return readFileViaShell(fname, true).trim().split("\\W+")[0];
            } catch (IOException e) {
                Log.d("TAG", "IOException while reading system file", e);
                return readFileViaShell(fname, true).trim().split("\\W+")[0];
            } finally {
                if (br != null) {
                    try {
                        br.close();
                        br = null;
                    } catch (IOException ignored) {
                        // failed to close reader
                    }
                }
            }
            return line.trim().split("\\W+")[0];
        }
        return null;
    }

    public static String[] getFileAsArray(String filepath,String splitter) {
        String content = getFileContent(new File(filepath)).trim();
        return content.split(splitter);
    }

    public static String readFileViaShell(String filePath, boolean useSu) {
        String command = new String("cat " + filePath);
        return useSu ? CMDProcessor.runSuCommand(command).getStdout().trim()
                : CMDProcessor.runShellCommand(command).getStdout().trim();
    }

    public static String readCommandStrdOut(String command, boolean useSu) {
        return useSu ? CMDProcessor.runSuCommand(command).getStdout().trim()
                : CMDProcessor.runShellCommand(command).getStdout().trim();
    }

    public static File[] listFilesViaShell(String dirPath, boolean useSu) {
        String command = new String("ls" + dirPath);
        String content = useSu ? CMDProcessor.runSuCommand(command).getStdout()
                : CMDProcessor.runShellCommand(command).getStdout();
        String[] folders = content.trim().split("\n");
        File[] list = new File[folders.length];
        for(int i = 0; i<folders.length; i++) {
            list[i] = new File(dirPath+"/"+folders[i]);
        }

        return list;
    }

    public static boolean getMount(String mount) {
        String[] mounts = getMounts("/system");
        if (mounts != null && mounts.length >= 3) {
            String device = mounts[0];
            String path = mounts[1];
            String point = mounts[2];
            String preferredMountCmd = new String("mount -o " + mount + ",remount -t " + point + ' ' + device + ' ' + path);
            if (CMDProcessor.runSuCommand(preferredMountCmd).success()) {
                return true;
            }
        }
        String fallbackMountCmd = new String("busybox mount -o remount," + mount + " /system");
        return CMDProcessor.runSuCommand(fallbackMountCmd).success();
    }

    public static String[] getMounts(CharSequence path) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/mounts"), 256);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(path)) {
                    return line.split(" ");
                }
            }
        } catch (FileNotFoundException ignored) {
            Log.d("TAG", "/proc/mounts does not exist");
        } catch (IOException ignored) {
            Log.d("TAG", "Error reading /proc/mounts");
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ignored) {
                    // ignored
                }
            }
        }
        return null;
    }

    public static boolean isSystemApp(Context c) {
        return c.getResources().getBoolean(R.bool.config_isSystemApp);
    }

    /*
     * Find value of build.prop item (/system can be ro or rw)
     *
     * @param prop /system/build.prop property name to find value of
     *
     * @returns String value of @param:prop
     */
    public static String findBuildPropValueOf(String prop) {
        String mBuildPath = "/system/build.prop";
        String DISABLE = "disable";
        String value = null;
        try {
            //create properties construct and load build.prop
            Properties mProps = new Properties();
            mProps.load(new FileInputStream(mBuildPath));
            //get the property
            value = mProps.getProperty(prop, DISABLE);
            Log.d("TAG", String.format("Helpers:findBuildPropValueOf found {%s} with the value (%s)", prop, value));
        } catch (IOException ioe) {
            Log.d("TAG", "failed to load input stream");
        } catch (NullPointerException npe) {
            //swallowed thrown by ill formatted requests
        }

        if (value != null) {
            return value;
        } else {
            return DISABLE;
        }
    }

    public static void debugger(Context mContext, String key, String message ) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String FILE_NAME = "KernelTweaker_log.txt";
        if(mPrefs.getBoolean(key, false)){
            String command = new String("echo \""+ message + "\" >> " + Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+FILE_NAME);
            CMDProcessor.runShellCommand(command);
        }
    }

    public static void checkApply(Context mContext, String key, String name, String curValue, String filepath) {
        String filevalue = readFileViaShell(filepath, true);
        if(!filevalue.contains(curValue)) {
            debugger(mContext,key, "\n-------------\n"+name + " " + "value: "+ curValue + " Not Applied --- File value is: "+ filevalue+"\n-------------\n");
        }else {
            debugger(mContext,key, "\n-------------\n"+name + " " + "value: "+ curValue + " Applied"+"\n-------------\n");
        }
    }

    public static int[] getScreenSize(Activity act) {
        Display display = act.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        int[] dimensions = new int[2];
        display.getSize(size);
        dimensions[0] = size.x;
        dimensions[1] = size.y;
        return dimensions; 
    }

    public int countLines(String filePath) {
        int numLines = 0;
        LineNumberReader lnr = null;
        try {
            lnr = new LineNumberReader(new FileReader(new File(filePath)));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            lnr.skip(Long.MAX_VALUE);
            numLines = lnr.getLineNumber();
            lnr.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return numLines;
    }

    public static boolean mpdecisionExists() {
        return new File("/system/bin/mpdecision").exists();
    }


    public static String getGovernorParamsDir(String[] governors) {
        File f = new File(Config.GOVERNOR_FILES_DIR);
        File[] files = f.listFiles();
        for(File ff : files) {
            if(arrayContains(governors, ff.getName())) {
                return ff.getAbsolutePath();
            }
        }
        return null;
    }

    public static boolean arrayContains(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    public static boolean fileExists(String path) {
        return new File(path).exists();
    }

    public static String[] readAheadValues() {
        ArrayList<String> values = new ArrayList<String>();
        int start = 128;
        for(int i = 1; i<=32; i++) {
            values.add((start*i)+"");
        }
        return values.toArray(new String[values.size()]);
    }

    public static String[] readAheadEntries() {
        ArrayList<String> values = new ArrayList<String>();
        int start = 128;
        for(int i = 1; i<=32; i++) {
            values.add((start*i)+" KB");
        }
        return values.toArray(new String[values.size()]);
    }

    public static String[] getAvailableTCP() {
        return Helpers.readCommandStrdOut(Config.TCP_OPTIONS, false).replaceAll("net.ipv4.tcp_available_congestion_control = ", "").replaceAll("\n", "").trim().split(" ");
    }

    public static String getCurrentTCP() {
        return Helpers.readCommandStrdOut(Config.TCP_CURRENT, false).replaceAll("net.ipv4.tcp_congestion_control = ","").replaceAll("\n", "");
    }

    public static List<ResolveInfo> getInstalleApps(Context mContext) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return mContext.getPackageManager().queryIntentActivities(mainIntent, 0);
    }

    public static void applyProfile(Context c, String packageName) {
        DatabaseHelpers mDb = new DatabaseHelpers();
        CMDHelpers cmd = new CMDHelpers();
        AppProfile mApp = mDb.getAppProfileByName(packageName);
        if(mApp != null) {
            Log.d("applyProfile()", "We have a profile. Apply it!");
            Profile mProfile = mApp.profile;
            String name = mProfile.name;
            String max = mProfile.maxFreq;
            String min = mProfile.minFreq;
            String governor = mProfile.governor;
            cmd.setMulticoreValues(max, true);
            cmd.setMulticoreValues(min, false);
            cmd.setValuesShell(Config.GOVERNOR_FILE, governor);
            Toast.makeText(c, 
                    String.format(c.getResources().getString(R.string.profile_apply_confirmed),
                            name), 
                            Toast.LENGTH_SHORT).show();
        }else{
            Log.d("applyProfile()", "No profile for this app");
            setProfileDefaults(c);
        }
    }

    public static String getCurrentHome(Context c){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = c.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    public static boolean isAccessibilityEnabled(Context context, String id) {
        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices) {
            if (id.equals(service.getId())) {
                return true;
            }
        }

        return false;
    }

    public static void logInstalledAccessiblityServices(Context context) {

        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getInstalledAccessibilityServiceList();
        for (AccessibilityServiceInfo service : runningServices) {
            Log.i("LOG ACCESSIBILITY SERVICES", service.getId());
        }
    }

    public static void setProfileDefaults(Context context){
        CMDHelpers cmd = new CMDHelpers();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean maxChanged = mPrefs.getString(Config.KEY_CPU_MAX_FREQ, "null")
                .equals(readOneLine(Config.MAX_FREQ_FILE)) ? false : true;
        boolean minChanged = mPrefs.getString(Config.KEY_CPU_MIN_FREQ, "null")
                .equals(readOneLine(Config.MIN_FREQ_FILE)) ? false : true;
        boolean govChanged = mPrefs.getString(Config.KEY_CPU_GOVERNOR, "null")
                .equals(readOneLine(Config.GOVERNOR_FILE)) ? false : true;
        if(maxChanged){
            cmd.setMulticoreValues(mPrefs.getString(Config.KEY_CPU_MAX_FREQ, "null"), true);
        }
        if(minChanged){
            cmd.setMulticoreValues(mPrefs.getString(Config.KEY_CPU_MIN_FREQ, "null"), false);
        }
        if(govChanged){
            cmd.setValuesShell(Config.GOVERNOR_FILE, mPrefs.getString(Config.KEY_CPU_GOVERNOR, "null"));
        }
        if(maxChanged || minChanged || govChanged) {
            Toast.makeText(context, 
                    R.string.profile_apply_stock, 
                    Toast.LENGTH_SHORT).show();
        }else{
            Log.d("PROFILE DEFAULTS", "Nothing to do");
        }
    }

    public static boolean mustSaveDefaults(Context context) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String maxFreq = mPrefs.getString(Config.KEY_CPU_MAX_FREQ, null );
        String minFreq = mPrefs.getString(Config.KEY_CPU_MIN_FREQ, null);
        String governor = mPrefs.getString(Config.KEY_CPU_GOVERNOR, null);

        if(maxFreq != null && minFreq != null && governor != null){
            return false;
        }
        return true;
    }

}

