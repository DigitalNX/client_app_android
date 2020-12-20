package com.digitalnx.digitalnxclient.networkUtils;

import org.json.JSONException;

public interface NetworkCallback {
    void exec(String result) throws JSONException;
}
