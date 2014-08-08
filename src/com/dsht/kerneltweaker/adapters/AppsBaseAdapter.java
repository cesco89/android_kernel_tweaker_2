/*
 * Copyright (C) 2012 ParanoidAndroid Project
 * This code has been modified. Portions copyright (C) 2014, Francesco Rigamonti.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.dsht.kerneltweaker.adapters;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.database.AppProfile;
import com.dsht.kerneltweaker.database.DatabaseHelpers;
import com.dsht.kerneltweaker.database.Profile;
import com.dsht.kerneltweaker.utils.Helpers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppsBaseAdapter extends BaseAdapter {

    private Context mContext;
    private List<ResolveInfo> mInstalledAppInfo;
    private List<AppItem> mInstalledApps = new LinkedList<AppItem>();
    private PackageManager mPackageManager;
    private DatabaseHelpers mDb;
    private Resources res;

    class ViewHolder {
        private TextView title;
        private TextView desc;
        private ImageView icon;
    }

    public class AppItem implements Comparable<AppItem> {
        public CharSequence title;
        public String packageName;
        public Drawable icon;
        public AppProfile mProfile;
        public boolean haveProfile;

        @Override
        public int compareTo(AppItem another) {
            return this.title.toString().compareTo(another.title.toString());
        }
    }

    public AppsBaseAdapter(Context con, List<ResolveInfo> appsList) {
        // TODO Auto-generated constructor stub
        this.mInstalledAppInfo = appsList;
        this.mContext = con;
        this.mPackageManager = con.getPackageManager();
        this.res = con.getResources();
        this.mDb = new DatabaseHelpers();
    }
    
    public void checkChanges() {
        if(mInstalledAppInfo.size() != Helpers.getInstalleApps(mContext).size()) {
            update();
        }
    }

    public void update() {
        reloadList();
    }

    @Override
    public int getCount() {
        return mInstalledApps.size();
    }

    @Override
    public AppItem getItem(int position) {
        return mInstalledApps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mInstalledApps.get(position).packageName.hashCode();
    }
    
    public void updateItem(int position, AppProfile mApp) {
        AppItem mItem = getItem(position);
        mItem.haveProfile = true;
        mItem.mProfile = mApp;
        mInstalledApps.set(position, mItem);
        this.notifyDataSetChanged();
    }
    
    public void removeAppProfile(int position) {
        AppItem mItem = getItem(position);
        AppProfile temp = mItem.mProfile;
        mItem.haveProfile = false;
        mItem.mProfile = null;
        mInstalledApps.set(position, mItem);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            final LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.apps_list_row, null, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.desc = (TextView) convertView.findViewById(R.id.profile);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        AppItem applicationInfo = this.getItem(position);
        holder.title.setText(applicationInfo.title);
        Drawable loadIcon = applicationInfo.icon;
        holder.icon.setImageDrawable(loadIcon);
        if(applicationInfo.haveProfile) {
            holder.desc.setText(String.format(res.getString(R.string.app_profile_desc), 
                    applicationInfo.mProfile.profile.name));
        }else{
            holder.desc.setText("");
        }
        return convertView;
    }

    private void reloadList() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {

            @Override
            public void run() {
                synchronized (mInstalledApps) {
                    final List<AppItem> apps = new LinkedList<AppItem>();
                    for (ResolveInfo info : mInstalledAppInfo) {
                        final AppItem item = new AppItem();

                        item.title = info.loadLabel(mPackageManager);
                        item.icon = info.loadIcon(mPackageManager);
                        item.packageName = info.activityInfo.packageName;
                        AppProfile mApp = mDb.getAppProfileByName(info.activityInfo.packageName);
                        if(mApp != null) {
                            if(mApp.packageName.equals(info.activityInfo.packageName)){
                                item.mProfile = mApp;
                                item.haveProfile = true;
                            }
                        }else{
                            item.haveProfile = false;
                        }
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                int index = Collections.binarySearch(mInstalledApps, item);
                                if (index < 0) {
                                    index = -index - 1;
                                    apps.add(index, item);
                                }
                                mInstalledApps.clear();
                                mInstalledApps.addAll(apps);
                                notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        }).start();
    }
}
