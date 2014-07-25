package com.dsht.kerneltweaker.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import android.util.Log;

import com.dsht.kerneltweaker.Config;
import com.dsht.kernetweaker.cmdprocessor.CMDProcessor;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

public class CMDHelpers {
    
    private String echo_regex = "echo \"%s\" > %s";
    private String echo_regex_vdd = "echo \"%s %s\" > %s";

    public CMDHelpers() {
        // TODO Auto-generated constructor stub
    }

    public void setValuesShell(String filePath, String value) {
        //CMDProcessor.runSuCommand("echo "+value+" > " + filePath);
        //Helpers.setPermissions(filePath);
        try {
            RootTools.getShell(true).add(new CommandCapture(0,"echo "+value+" > " + filePath ));
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
        Log.d("COMMAND", "echo "+value+" > " + filePath);
    }

    public void runCustomCommand(String command) {
        try {
            RootTools.getShell(true).add(new CommandCapture(0,command));
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
        //Log.d("COMMAND", command);
    }

    public void setMulticoreValues(String value, boolean maxfreq) {
        int numcores = Helpers.getNumOfCpus();
        String cmd = new String();
        for(int i = 0; i<numcores; i++) {
            if(maxfreq) {
                //Helpers.setPermissions(String.format(Config.MAX_FREQ_COMMAND_PATH,i));
                cmd = String.format(Config.MAX_FREQ_COMMAND, value, i);
            }else {
                //Helpers.setPermissions(String.format(Config.MIN_FREQ_COMMAND_PATH,i));
                cmd = String.format(Config.MIN_FREQ_COMMAND, value, i);
            }
            Log.d("CORES_COMMAND", cmd);
            runCustomCommand(cmd);
        }
    }
    
    public void setUndervolt(ArrayList<UvItem> uvItems, ArrayList<String> values, int TYPE, String path) {
        switch(TYPE) {
        case Config.TYPE_UV_MV_TABLE:
            Log.d("TYPE", "UV_MV_TABLE");
            String params = buildString(values);
            Log.d("PARAM", params);
            String formatted = String.format(echo_regex, params, path);
            Log.d("FORMAT", formatted);
            runCustomCommand(formatted);
            break;
        case Config.TYPE_VDD:
            Log.d("TYPE", "VDD_TABLE");
            for(int i = 0; i < uvItems.size(); i++) {
                UvItem mItem = uvItems.get(i);
                String formatVdd = String.format(echo_regex_vdd, mItem.getName(), mItem.getValue(), path);
                Log.d("FORMAT_VDD", formatVdd);
                runCustomCommand(formatVdd);
            }
            break;
        }
        
    }
    
    private String buildString(ArrayList<String> values) {
        String build = "";
        for(int i = 0; i<values.size(); i++) {
            if(i == values.size()-1) {
                build+=values.get(i);
            }else {
                build+=values.get(i)+" ";
            }
        }
        return build;
    }

}
