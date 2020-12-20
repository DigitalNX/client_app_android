package com.digitalnx.digitalnxclient.ui.settings;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalnx.digitalnxclient.LoginActivity;
import com.digitalnx.digitalnxclient.R;

public class SettingsFragment extends Fragment {

    private SettingsViewModel mViewModel;
    LinearLayout settingsLayout;
    SharedPreferences sharedPref;
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);

        settingsLayout = (LinearLayout) rootView.findViewById(R.id.settings);
        sharedPref = getActivity().getSharedPreferences("AUTH_CREDENTIALS", Context.MODE_PRIVATE);
        // sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String username = sharedPref.getString("USERNAME", null);
        String backendVersion = sharedPref.getString("BACKEND_VER", null);

        Resources res = getResources();
        TextView appVersionView = rootView.findViewById(R.id.android_app_version);
        String appVersionString = String.format(res.getString(R.string.android_app_version_string), "0.1");
        appVersionView.setText(appVersionString);

        TextView backendVersionView = rootView.findViewById(R.id.backend_version);
        String backendVersionString = String.format(res.getString(R.string.backend_version_string), backendVersion);
        backendVersionView.setText(backendVersionString);

        TextView loginInfo = rootView.findViewById(R.id.logged_in_as);
        String loginInfoStr = username;
        String loggedInAs = String.format(res.getString(R.string.logged_in_as_string), loginInfoStr);
        loginInfo.setText(loggedInAs);

        Button logoutButton = rootView.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener((view) -> { logout(); });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        // TODO: Use the ViewModel
    }

    private void logout() {
        Toast.makeText(getActivity(), "Logging out...", Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("USERNAME");
        editor.remove("PASSWORD");
        editor.remove("TOKEN");
        editor.apply();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}