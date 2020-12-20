package com.digitalnx.digitalnxclient.ui.about;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitalnx.digitalnxclient.R;

public class AboutFragment extends Fragment {

    private AboutViewModel mViewModel;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_about, container, false);

        LinearLayout aboutLayout = (LinearLayout) rootView.findViewById(R.id.about);
        TextView aboutTitle = new TextView(getActivity());
        TextView aboutContent = new TextView(getActivity());
        TextView aboutContentLink = new TextView(getActivity());

        aboutTitle.setText(R.string.about_fragment_title);
        aboutTitle.setTextColor(Color.parseColor("#000000"));
        aboutTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
        aboutTitle.setPadding(0,20,0,10);

        aboutContent.setText(R.string.about_fragment_content);
        aboutContent.setTextColor(Color.parseColor("#666666"));
        aboutContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        aboutContent.setPadding(0,0,0, 10);

        aboutContentLink.setText(R.string.projects_web_address);
        aboutContentLink.setMovementMethod(LinkMovementMethod.getInstance());
        aboutContentLink.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);

        aboutLayout.addView(aboutTitle);
        aboutLayout.addView(aboutContent);
        aboutLayout.addView(aboutContentLink);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AboutViewModel.class);
        // TODO: Use the ViewModel
    }

}