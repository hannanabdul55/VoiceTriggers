package com.example.voicetriggers;

import android.content.pm.ResolveInfo;

/**
 * Created by akanji1 on 27/04/15.
 */
public class ActionTrigger {
    public String tag;
    public ResolveInfo res;

    public ActionTrigger(String tag, ResolveInfo res) {
        this.tag = tag;
        this.res = res;
    }
}
