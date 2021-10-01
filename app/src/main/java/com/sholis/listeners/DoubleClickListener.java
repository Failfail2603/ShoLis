package com.sholis.listeners;

import android.view.View;

public abstract class DoubleClickListener implements View.OnClickListener {



    @Override
    public void onClick(View v) {

    }

    public abstract void onSingleClick(View v);
    public abstract void onDoubleClick(View v);
}
