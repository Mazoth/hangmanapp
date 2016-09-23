package com.example.mkluf.hangmanapp;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.io.Serializable;
import java.util.ArrayList;

public class ButtonAdapter extends BaseAdapter implements Serializable {
    private ArrayList<Button> buttonSet = null;

    public ButtonAdapter(ArrayList<Button> buttonSet) {
        this.buttonSet = buttonSet;
    }

    @Override
    public int getCount() {
        return buttonSet.size();
    }

    @Override
    public Object getItem(int i) {
        return buttonSet.get(i);
    }

    @Override
    public long getItemId(int i) {
        return buttonSet.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Button button;
        if(view == null) button = buttonSet.get(i);
        else button = (Button) view;
        return button;
    }
}
