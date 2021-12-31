package com_group_cs370_fall21_tradingapp.github.rsitradingapp.gui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com_group_cs370_fall21_tradingapp.github.rsitradingapp.MainActivity;
import com_group_cs370_fall21_tradingapp.github.rsitradingapp.R;

public class LoginFragment extends Fragment {

    public LoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);

    }


}
