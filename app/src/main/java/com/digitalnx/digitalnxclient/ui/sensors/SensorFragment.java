package com.digitalnx.digitalnxclient.ui.sensors;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.digitalnx.digitalnxclient.R;
import com.digitalnx.digitalnxclient.networkUtils.NetworkUtils;
import com.digitalnx.digitalnxclient.ui.home.HomeViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SensorFragment extends Fragment {

    private SensorViewModel mViewModel;
    private JSONArray sensorArrayJSON;
    LinearLayout sensorLayout;
    private RequestQueue queue;

    public static SensorFragment newInstance() {
        return new SensorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(SensorViewModel.class);
        View rootView = inflater.inflate(R.layout.fragment_sensors, container, false);

        queue = Volley.newRequestQueue(requireActivity());
        sensorLayout = (LinearLayout) rootView.findViewById(R.id.sensors);

        grabSensors();
        return rootView;
    }

    private void grabSensors() {
        NetworkUtils.GETRequestWithToken(response -> {
            try  {
                sensorArrayJSON = new JSONArray(response);
                renderSensors();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, "/api/sensors", queue);
    }

    @SuppressLint("SetTextI18n")
    private void renderSensors() {
        try {
            for (int i = 0; i < sensorArrayJSON.length(); i++) {
                LinearLayout sLayout = new LinearLayout(getActivity());
                JSONObject sensorObjectJSON = (JSONObject) sensorArrayJSON.get(i);

                sLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                sLayout.setOrientation(LinearLayout.VERTICAL);
                sLayout.setPadding(10, 15, 10, 10);

                TextView sensorGroupTitle = new TextView(getActivity());
                sensorGroupTitle.setText(sensorObjectJSON.get("group_name").toString());
                sensorGroupTitle.setTextColor(Color.parseColor("#000000"));
                sensorGroupTitle.setTypeface(null, Typeface.BOLD);
                sensorGroupTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                sensorGroupTitle.setPadding(10, 50, 10, 7);
                sLayout.addView(sensorGroupTitle);

                JSONArray sensorsJSON = (JSONArray) sensorObjectJSON.get("sensors");
                for (int j = 0; j < sensorsJSON.length(); j++) {
                    JSONObject sensorObj = (JSONObject) sensorsJSON.get(j);
                    LinearLayout layoutForEachSensorItem = new LinearLayout(getActivity());
                    String sensorType = sensorObj.get("sensor_type").toString();
                    if (sensorType.equals("BOOLEAN")) {
                        Switch switchBtn = new Switch(getContext());
                        switchBtn.setClickable(false);

                        TextView buttonTitle = new TextView(getActivity());
                        buttonTitle.setText(sensorObj.get("device_name").toString());
                        buttonTitle.setTextColor(Color.parseColor("#666666"));
                        buttonTitle.setTypeface(null, Typeface.NORMAL);
                        buttonTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                        buttonTitle.setPadding(5, 10, 0, 7);

                        if ((Boolean) sensorObj.get("value"))
                            switchBtn.setChecked(true);
                        else
                            switchBtn.setChecked(false);

                        layoutForEachSensorItem.addView(buttonTitle);
                        layoutForEachSensorItem.addView(switchBtn);

                    } else if (sensorType.equals("FLOATING_POINT")) {
                        TextView sensorTitle = new TextView(getActivity());
                        sensorTitle.setText(sensorObj.get("device_name").toString() + ": ");
                        sensorTitle.setTextColor(Color.parseColor("#666666"));
                        sensorTitle.setTypeface(null, Typeface.NORMAL);
                        sensorTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                        sensorTitle.setPadding(5, 10, 0, 7);

                        TextView contentView = new TextView(getActivity());
                        contentView.setText(sensorObj.get("value").toString());
                        contentView.setTextColor(Color.parseColor("#666666"));
                        contentView.setTypeface(null, Typeface.BOLD);
                        contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                        contentView.setPadding(5, 10, 0, 7);

                        layoutForEachSensorItem.addView(sensorTitle);
                        layoutForEachSensorItem.addView(contentView);
                    }
                    sLayout.addView(layoutForEachSensorItem);

                }
                sensorLayout.addView(sLayout);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}