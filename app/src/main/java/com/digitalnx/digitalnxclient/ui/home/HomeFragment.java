package com.digitalnx.digitalnxclient.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.digitalnx.digitalnxclient.R;
import com.digitalnx.digitalnxclient.networkUtils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private JSONArray infoArrayJSON;
    LinearLayout homeLayout;
    private RequestQueue queue;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        queue = Volley.newRequestQueue(requireActivity());
        homeLayout = (LinearLayout) rootView.findViewById(R.id.home);
        grabInfo();

        return rootView;
    }

    private void grabInfo() {
        NetworkUtils.GETRequestWithToken(response -> {
            try  {
                infoArrayJSON = new JSONArray(response);
                renderInfo();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, "/api/info_data", queue);
    }

    @SuppressLint("SetTextI18n")
    private void renderInfo() {
        for(int i = 0; i < infoArrayJSON.length(); i++) {
            try {
                JSONArray infoItemsJSON = (JSONArray) infoArrayJSON.getJSONObject(i).get("items");
                for(int j = 0; j  < infoItemsJSON.length(); j++) {
                    JSONObject infoItemJSON = (JSONObject) infoItemsJSON.get(j);
                    JSONArray contentJSON = (JSONArray) infoItemJSON.get("content");

                    LinearLayout infoItemLayout = new LinearLayout(getActivity());
                    infoItemLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    infoItemLayout.setOrientation(LinearLayout.VERTICAL);
                    infoItemLayout.setPadding(10,15, 10, 10);
                    infoItemLayout.setId(j);

                    TextView infoItemTitle = new TextView(getActivity());
                    infoItemTitle.setText(infoItemJSON.get("title").toString());
                    infoItemTitle.setTextColor(Color.parseColor("#000000"));
                    infoItemTitle.setTypeface(null, Typeface.BOLD);
                    infoItemTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    infoItemTitle.setPadding(10, 50, 10, 7);
                    infoItemLayout.addView(infoItemTitle);
                    if(infoItemJSON.get("type").toString().equals("SENSOR")) {
                        for(int k = 0; k < contentJSON.length(); k++) {
                            JSONObject item = (JSONObject) contentJSON.get(k);
                            String sensorType = item.get("sensor_type").toString();
                            LinearLayout itemContentLayout = new LinearLayout(getActivity());

                            if(sensorType.equals("BOOLEAN")) {
                                Switch switchBtn = new Switch(getContext());
                                switchBtn.setClickable(false);

                                TextView buttonTitle = new TextView(getActivity());
                                buttonTitle.setText(item.get("device_name").toString());
                                buttonTitle.setTextColor(Color.parseColor("#666666"));
                                buttonTitle.setTypeface(null, Typeface.NORMAL);
                                buttonTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                                buttonTitle.setPadding(5, 10, 0, 7);

                                if((Boolean) item.get("value"))
                                    switchBtn.setChecked(true);
                                else
                                    switchBtn.setChecked(false);

                                itemContentLayout.addView(buttonTitle);
                                itemContentLayout.addView(switchBtn);

                            } else if (sensorType.equals("FLOATING_POINT")) {
                                TextView sensorTitle = new TextView(getActivity());
                                sensorTitle.setText(item.get("device_name").toString() + ": ");
                                sensorTitle.setTextColor(Color.parseColor("#666666"));
                                sensorTitle.setTypeface(null, Typeface.NORMAL);
                                sensorTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                                sensorTitle.setPadding(5, 10, 0, 7);

                                TextView contentView = new TextView(getActivity());
                                contentView.setText(item.get("value").toString());
                                contentView.setTextColor(Color.parseColor("#666666"));
                                contentView.setTypeface(null, Typeface.BOLD);
                                contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                                contentView.setPadding(5, 10, 0, 7);

                                itemContentLayout.addView(sensorTitle);
                                itemContentLayout.addView(contentView);
                            }
                            infoItemLayout.addView(itemContentLayout);
                        }

                    } else {
                        for(int k = 0; k < contentJSON.length(); k++) {
                            String item = contentJSON.get(k).toString();
                            TextView contentView = new TextView(getActivity());
                            contentView.setText(item);
                            contentView.setTextColor(Color.parseColor("#666666"));
                            contentView.setTypeface(null, Typeface.NORMAL);
                            contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                            contentView.setPadding(5, 10, 0, 7);
                            infoItemLayout.addView(contentView);
                        }
                    }

                    homeLayout.addView(infoItemLayout);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
