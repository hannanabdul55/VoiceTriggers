package com.example.voicetriggers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by akanji1 on 27/04/15.
 */
public class ApplicationListAdapter extends BaseAdapter {

    private final List<ResolveInfo> resInfo;
    private Context con;

    @Override
    public int getCount() {
        return resInfo.size();
    }

    @Override
    public ResolveInfo getItem(int position) {
        return resInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        if (arg1 == null) {
            LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            arg1 = inflater.inflate(R.layout.list_item, arg2, false);
        }
        PackageManager pm = (PackageManager) con.getPackageManager();
        TextView appName = (TextView) arg1.findViewById(R.id.textView1);
        appName.setText(resInfo.get(arg0).loadLabel(pm));
        ImageView img_ico = (ImageView) arg1.findViewById(R.id.imageView1);
        img_ico.setImageDrawable(resInfo.get(arg0).loadIcon(pm));
        return arg1;
    }


    public ApplicationListAdapter(List<ResolveInfo> list, Context con) {
        this.resInfo = list;
        this.con = con;
    }
}
