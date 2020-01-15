package com.reactnativejitsimeet;

import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.greenrobot.eventbus.EventBus;
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

public class JitsiMeetModule extends ReactContextBaseJavaModule {
    private DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter;
    PreferencesHelper preferencesHelper;

    public JitsiMeetModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNJitsiMeetNavigatorManager";
    }

    @ReactMethod
    public void setup(String basUrl) {
      FLog.d("JitsiMeet", "Initialize");

      preferencesHelper = PreferencesHelper.getInstance(getReactApplicationContext());
      preferencesHelper.setBaseUrl(basUrl);

      ReactApplicationContext context = getReactApplicationContext();
      this.eventEmitter = context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
      JitsiBroadcastReceiver receiver = new JitsiBroadcastReceiver();
      receiver.setReactModule(this);
      IntentFilter filter = new IntentFilter();
      filter.addAction("CONFERENCE_FAILED");
      filter.addAction("CONFERENCE_JOINED");
      filter.addAction("CONFERENCE_LEFT");
      filter.addAction("CONFERENCE_WILL_JOIN");
      filter.addAction("CONFERENCE_WILL_LEAVE");
      filter.addAction("LOAD_CONFIG_ERROR");
      context.getCurrentActivity().registerReceiver(receiver, filter, context.getPackageName() + ".permission.JITSI_BROADCAST", null);
    }

    @ReactMethod
    public void answer(boolean isConference, String room, String avatarUrl, String displayName, com.facebook.react.bridge.Callback errorCallback) {
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
                    int intParticipant = (int) jsonObject.get("participants");
                    if (intParticipant > 0 ) {
                        String url = preferencesHelper.getBaseUrl() + "/" + room;
                        getJwtToken(isConference, url, avatarUrl, displayName);
                    } else {
                        errorCallback.invoke("You haven't participants");
                    }
                } catch (JSONException e) {
                    errorCallback.invoke(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

    }

    @ReactMethod
    public void call(boolean isConference, String room, String avatarUrl, String displayName) {
        String url = preferencesHelper.getBaseUrl()+"/"+room;
        getJwtToken(isConference, url, avatarUrl, displayName);
    }

    @ReactMethod
    public void endCall() {
        EventBus.getDefault().post(new MeetEvent(Event.ENDCALL));
    }

    public void onEventReceived(String event, WritableMap data) {
        eventEmitter.emit(event, data);
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

                        ReactApplicationContext context = getReactApplicationContext();

                        Intent intent = new Intent(context, JitsiMeetNavigatorActivity.class);
                        intent.putExtra("url", url);
                        intent.putExtra("isConference", isConference);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

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
