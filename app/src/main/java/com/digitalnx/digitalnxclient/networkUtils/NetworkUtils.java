package com.digitalnx.digitalnxclient.networkUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class NetworkUtils {
    private static String baseUrl;
    private static String token;

    public static void setToken(String jwtToken) {
        token = jwtToken;
    }

    public static void GETRequest(final NetworkCallback callback, String route, RequestQueue queue) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl + route,
                response -> {
                    try {
                        callback.exec(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> { });
        queue.add(stringRequest);
    }

    public static void GETRequestWithToken(final NetworkCallback callback, String route, RequestQueue queue) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseUrl + route,
                response -> {
                    try {
                        callback.exec(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> { }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json; charset=UTF-8");
                        params.put("token", token);
                        return params;
                    };
            };
        queue.add(stringRequest);

    }

    public static void POSTRequest(final NetworkCallback callback, String requestBody, String route, RequestQueue queue, final NetworkCallbackError callbackError) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl + route,
            response -> {
                try {
                    callback.exec(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> {
                if(callbackError != null) {
                    callbackError.exec();
                }
            }
        ){
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("UTF_8");
                    } catch (UnsupportedEncodingException uee) {
                        return null;
                    }
                }
        };
        queue.add(stringRequest);
    }

    public static void POSTRequest(final NetworkCallback callback, String requestBody, String route, RequestQueue queue) {
        POSTRequest(callback, requestBody, route, queue, null);
    }

    public static void POSTRequestWithToken(final NetworkCallback callback, String requestBody, String route, RequestQueue queue) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl + route,
                response -> {
                    try {
                        callback.exec(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> { }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("token", token);
                return params;
            };
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("UTF_8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };
        queue.add(stringRequest);

    }
    public static void Login(final NetworkCallback callback, String requestBody, String serverAddress, String loginRoute, RequestQueue queue, final NetworkCallbackError callbackError) {
        if(serverAddress.length() > 10 && (!serverAddress.substring(0, 8).equals("http://") || !serverAddress.substring(0, 9).equals("https://"))) {
            serverAddress = "http://" + serverAddress;
        }

        baseUrl = serverAddress;
        POSTRequest(callback, requestBody, loginRoute, queue, callbackError);
    }

}
