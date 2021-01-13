package com.digitalnx.digitalnxclient.ui.relays;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.digitalnx.digitalnxclient.R;
import com.digitalnx.digitalnxclient.networkUtils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RelaysFragment extends Fragment {
    private LinearLayout articleLayout;
    private RequestQueue queue;
    public static RelaysFragment newInstance() {
        return new RelaysFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_relays, container, false);
        articleLayout = (LinearLayout) rootView.findViewById(R.id.relays);
        queue = Volley.newRequestQueue(requireActivity());

        // TODO MOVE queue down
        NetworkUtils.GETRequestWithToken(response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    TextView relayGroupTitle = new TextView(getActivity());
                    relayGroupTitle.setText(jsonObj.get("group_name").toString());
                    relayGroupTitle.setTypeface(null, Typeface.BOLD);
                    relayGroupTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    relayGroupTitle.setPadding(22, 50, 0, 30);
                    articleLayout.addView(relayGroupTitle);

                    JSONArray relays = (JSONArray) jsonObj.get("relays");
                    for (int j = 0; j < relays.length(); j++) {
                        JSONObject relay = (JSONObject) relays.get(j);
                        int relayId = (int) relay.get("id");
                        ToggleButton toggle = new ToggleButton(getActivity());
                        toggle.setHeight(170);
                        // Prevents the button's text to go ALL CAPS
                        toggle.setTransformationMethod(null);

                        toggle.setTextOn(relay.get("device_name").toString() + ": On");
                        toggle.setTextOff(relay.get("device_name").toString() + ": Off");
                        if (!(Boolean) relay.get("status")) {
                            toggle.setText(relay.get("device_name").toString() + ": Off");
                            // TODO SET STATE TOO

                        } else if (!(Boolean) relay.get("status")) {
                            toggle.setText(relay.get("device_name").toString() + ": On");
                        }

                        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    turnRelayOn(relayId);
                                } else {
                                    turnRelayOff(relayId);
                                }
                            }
                        });
                        articleLayout.addView(toggle);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, "/api/relays", queue);

        return rootView;
    }

    private void turnRelayOn(int relayId) {
        NetworkUtils.GETRequest(response -> {}, "/api/relay/" + Integer.toString(relayId) + "/on", queue);
    }

    private void turnRelayOff(int relayId) {
        NetworkUtils.GETRequest(response -> {}, "/api/relay/" + Integer.toString(relayId) + "/off", queue);
    }
}