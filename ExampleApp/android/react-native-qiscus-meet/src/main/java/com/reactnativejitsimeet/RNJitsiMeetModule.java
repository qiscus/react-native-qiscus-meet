package com.reactnativejitsimeet;

import android.util.Log;
import java.net.URL;
import java.net.MalformedURLException;

import java.util.HashMap;
import java.util.Map;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.uimanager.IllegalViewOperationException;

@ReactModule(name = RNJitsiMeetModule.MODULE_NAME)
public class RNJitsiMeetModule extends ReactContextBaseJavaModule {
    public static final String MODULE_NAME = "RNJitsiMeetModule";
    private IRNJitsiMeetViewReference mJitsiMeetViewReference;

    private String appId;
    private String url;

    public RNJitsiMeetModule(ReactApplicationContext reactContext, IRNJitsiMeetViewReference jitsiMeetViewReference) {
        super(reactContext);
        mJitsiMeetViewReference = jitsiMeetViewReference;
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @ReactMethod
    public void getSetup(Promise promise) {
        try {
            WritableMap map = Arguments.createMap();
             map.putString("appId", this.appId);
             map.putString("url", this.url);
            promise.resolve(map);
        } catch (IllegalViewOperationException e) {
            promise.reject("AppId and URL not set", e);
        }
    }

    @ReactMethod 
    public void initialize() {
        Log.d("JitsiMeet", "Initialize is deprecated in v2");
    }

    @ReactMethod
    public void setup(String appId, String url) {
        this.appId = appId;
        this.url = url;
    }

    @ReactMethod
    public void call(ReadableMap userInfo, String room, Boolean videoMuted, Boolean audioMuted, String token) {
        String appId = this.appId;
        String url = this.url;
        String serverUrl = url + "/" + appId + "/" + room;

        if(appId == null && appId.trim().isEmpty() ) {
            Log.d("QiscusMeet", "Please setup appID first");
            return;
        }

        if(url == null && url.trim().isEmpty() ) {
            Log.d("QiscusMeet","Please setup server URL first");
            return;
        }

        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mJitsiMeetViewReference.getJitsiMeetView() != null) {
                    RNJitsiMeetUserInfo _userInfo = new RNJitsiMeetUserInfo();
                    if (userInfo != null) {
                        if (userInfo.hasKey("displayName")) {
                            _userInfo.setDisplayName(userInfo.getString("displayName"));
                          }
                          if (userInfo.hasKey("email")) {
                            _userInfo.setEmail(userInfo.getString("email"));
                          }
                          if (userInfo.hasKey("avatar")) {
                            String avatarURL = userInfo.getString("avatar");
                            try {
                                _userInfo.setAvatar(new URL(avatarURL));
                            } catch (MalformedURLException e) {
                            }
                          }
                    }
                    RNJitsiMeetConferenceOptions options = new RNJitsiMeetConferenceOptions.Builder()
                            .setRoom(serverUrl)
                            .setAudioOnly(false)
                            .setToken(token)
                            .setUserInfo(_userInfo)
                            .setVideoMuted(videoMuted)
                            .setAudioMuted(audioMuted)
                            .build();
                    mJitsiMeetViewReference.getJitsiMeetView().join(options);
                }
            }
        });
    }

    @ReactMethod
    public void audioCall(ReadableMap userInfo, String room, Boolean audioMuted, String token) {
       String appId = this.appId;
       String url = this.url;
       String serverUrl = url + "/" + appId + "/" + room;

        if(appId == null && appId.trim().isEmpty() ) {
            Log.d("QiscusMeet","Please setup appID first");
            return;
        }

        if(url != null && !url.trim().isEmpty() ) {
            Log.d("QiscusMeet","Please setup server URL first");
            return;
        }
        

        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mJitsiMeetViewReference.getJitsiMeetView() != null) {
                    RNJitsiMeetUserInfo _userInfo = new RNJitsiMeetUserInfo();
                    if (userInfo != null) {
                        if (userInfo.hasKey("displayName")) {
                            _userInfo.setDisplayName(userInfo.getString("displayName"));
                          }
                          if (userInfo.hasKey("email")) {
                            _userInfo.setEmail(userInfo.getString("email"));
                          }
                          if (userInfo.hasKey("avatar")) {
                            String avatarURL = userInfo.getString("avatar");
                            try {
                                _userInfo.setAvatar(new URL(avatarURL));
                            } catch (MalformedURLException e) {
                            }
                          }
                    }
                    RNJitsiMeetConferenceOptions options = new RNJitsiMeetConferenceOptions.Builder()
                            .setRoom(serverUrl)
                            .setAudioOnly(true)
                            .setToken(token)
                            .setUserInfo(_userInfo)
                            .setAudioMuted(audioMuted)
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
}
