package com.example.chatroomsmall.fcm;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendPushNotification {

    private String KEY = "AAAAOeXv4Tw:APA91bHnA5prZZV_NT1kkv6J5noaZdW-TwURffHs8NgQ_YVtWe_pYXZOv4bALIZnYjViTuXeFgVWxH_KkpJt39ZH_q1elWCHBG0p6XpVcDamrDD7KiYqwgisF5eVHBXTBiJviTTdabsE";
    //server key
    private Context context;

    public SendPushNotification(Context context) {
        this.context = context;
    }

    public void startPush(String username, String messageBody, String token) {

        HashMap<String, String> message = new HashMap<>();
        message.put("title", username);
        message.put("message", messageBody);
        sendPushNotification(message, token);
    }

    private void sendPushNotification(HashMap<String, String> message, String token) {
        // dung volley de post message len firebase
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String FCM_URL = "https://fcm.googleapis.com/fcm/send"; //Định nghĩa URL của dịch vụ Firebase Cloud Messaging (FCM) để gửi thông báo đẩy.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                FCM_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Send message 1", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put("data", new JSONObject(message));
                objectMap.put("to", "/topics/" + token);
                return new JSONObject(objectMap).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                // Phương thức này được sử dụng để xác định các tiêu đề của yêu cầu HTTP,
                // chẳng hạn như "Authorization" (mã thông báo xác thực) và "Content-Type" (kiểu nội dung).
                header.put("Authorization",
                        "key="+KEY);
                header.put("Content-Type", "application/json");
                Log.e("HEADER", header.toString());
                return header;
            }
        };
        requestQueue.add(stringRequest);
    }
}
