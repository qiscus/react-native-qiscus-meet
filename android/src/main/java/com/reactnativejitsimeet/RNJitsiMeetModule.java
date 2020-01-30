package com.reactnativejitsimeet;

import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.module.annotations.ReactModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@ReactModule(name = RNJitsiMeetModule.MODULE_NAME)
public class RNJitsiMeetModule extends ReactContextBaseJavaModule {
    public static final String MODULE_NAME = "RNJitsiMeetModule";
    private IRNJitsiMeetViewReference mJitsiMeetViewReference;
    PreferencesHelper preferencesHelper;

    public RNJitsiMeetModule(ReactApplicationContext reactContext, IRNJitsiMeetViewReference jitsiMeetViewReference) {
        super(reactContext);
        mJitsiMeetViewReference = jitsiMeetViewReference;
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @ReactMethod
    public void initialize() {
        Log.d("JitsiMeet", "Initialize is deprecated in v2");
    }

    @ReactMethod
    public void setup(String basUrl) {
        preferencesHelper = PreferencesHelper.getInstance(getReactApplicationContext());
        preferencesHelper.setBaseUrl(basUrl);
    }

    @ReactMethod
    public void call(boolean isVideo, String room, String avatarUrl, String displayName, com.facebook.react.bridge.Callback errorCallback) {
        String url = preferencesHelper.getBaseUrl()+"/"+room;
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getJwtToken(isVideo, url, avatarUrl, displayName);
            }
        });
    }

    @ReactMethod
    public void answer(boolean isVideo, String room, String avatarUrl, String displayName, com.facebook.react.bridge.Callback errorCallback) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                .url(preferencesHelper.getBaseUrl()+"/get-room-size?room="+room)
                .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        errorCallback.invoke(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        assert response.body() != null;
                        String jsonData = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(jsonData);
                            if (jsonObject.get("participants").equals("0")){
                                errorCallback.invoke("You haven't participants");
                            } else {
                                String url = preferencesHelper.getBaseUrl()+"/"+room;
                                getJwtToken(isVideo, url, avatarUrl, displayName);
                            }
                        } catch (JSONException e) {
                            errorCallback.invoke(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @ReactMethod
    public void audioCall(String url) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mJitsiMeetViewReference.getJitsiMeetView() != null) {
                    RNJitsiMeetConferenceOptions options = new RNJitsiMeetConferenceOptions.Builder()
                            .setRoom(url)
                            .setAudioOnly(true)
                            .build();
                    mJitsiMeetViewReference.getJitsiMeetView().join(options);
                }
            }
        });
    }

    @ReactMethod
    public void endCall() {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mJitsiMeetViewReference.getJitsiMeetView() != null) {
                    mJitsiMeetViewReference.getJitsiMeetView().leave();
                }
            }
        });
    }

    public void getJwtToken(boolean isConference, String room, String avatar, String displayName) {

        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = preferencesHelper.getBaseUrl()+":9090/generate_url";

        OkHttpClient client = new OkHttpClient();

        JSONObject postdata = new JSONObject();
        try {
            postdata.put("baseUrl", room);
            postdata.put("avatar", avatar);
            postdata.put("name", displayName);
        } catch(JSONException e){
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage();
                Log.w("failure Response", mMessage);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                assert response.body() != null;
                String jsonData = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    String url = (String) jsonObject.get("url");

                    if (jsonObject.get("url") != null){

                        if (mJitsiMeetViewReference.getJitsiMeetView() != null) {
                            RNJitsiMeetConferenceOptions options = new RNJitsiMeetConferenceOptions.Builder()
                                    .setRoom(url)
                                    .setAudioOnly(!isConference)
                                    .build();
                            mJitsiMeetViewReference.getJitsiMeetView().join(options);
                        }

                    } else {
                        Log.d("QiscusMeet", "Failed get url");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
