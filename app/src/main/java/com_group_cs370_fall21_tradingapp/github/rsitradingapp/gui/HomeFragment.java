package com_group_cs370_fall21_tradingapp.github.rsitradingapp.gui;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com_group_cs370_fall21_tradingapp.github.rsitradingapp.MainActivity;
import com_group_cs370_fall21_tradingapp.github.rsitradingapp.R;

public class HomeFragment extends Fragment {

    public HomeFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}
