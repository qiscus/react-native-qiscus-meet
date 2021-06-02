package com.reactnativejitsimeet;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.module.annotations.ReactModule;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetViewListener;

import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

@ReactModule(name = RNJitsiMeetViewManager.REACT_CLASS)
public class RNJitsiMeetViewManager extends SimpleViewManager<RNJitsiMeetView> implements JitsiMeetViewListener {
    public static final String REACT_CLASS = "RNJitsiMeetView";
    private IRNJitsiMeetViewReference mJitsiMeetViewReference;
    private ReactApplicationContext mReactContext;

    public RNJitsiMeetViewManager(ReactApplicationContext reactContext, IRNJitsiMeetViewReference jitsiMeetViewReference) {
        mJitsiMeetViewReference = jitsiMeetViewReference;
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public RNJitsiMeetView createViewInstance(ThemedReactContext context) {
        if (mJitsiMeetViewReference.getJitsiMeetView() == null) {
            RNJitsiMeetView view = new RNJitsiMeetView(context.getCurrentActivity());
            mJitsiMeetViewReference.setJitsiMeetView(view);
        }
        return mJitsiMeetViewReference.getJitsiMeetView();
    }

    //    @Override
//    protected void onConferenceWillJoin(HashMap<String, Object> extraData) {
//        super.onConferenceWillJoin(extraData);
//        WritableMap event = Arguments.createMap();
//        event.putString("url", (String) extraData.get("url"));
//        mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
//                mJitsiMeetViewReference.getJitsiMeetView().getId(),
//                "conferenceWillJoin",
//                event);
//    }
//
//    @Override
//    protected void onConferenceJoined(HashMap<String, Object> extraData) {
//        super.onConferenceJoined(extraData);
//        WritableMap event = Arguments.createMap();
//        event.putString("url", (String) extraData.get("url"));
//        mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
//                mJitsiMeetViewReference.getJitsiMeetView().getId(),
//                "conferenceJoined",
//                event);
//    }
//
//    @Override
//    protected void onConferenceTerminated(HashMap<String, Object> extraData) {
//        super.onConferenceTerminated(extraData);
//        WritableMap event = Arguments.createMap();
//        event.putString("url", (String) extraData.get("url"));
//        event.putString("error", (String) extraData.get("error"));
//        mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
//                mJitsiMeetViewReference.getJitsiMeetView().getId(),
//                "conferenceTerminated",
//                event);
//    }
//
//    @Override
//    protected void onParticipantJoined(HashMap<String, Object> extraData) {
//        super.onParticipantJoined(extraData);
//        WritableMap event = Arguments.createMap();
//        event.putString("url", (String) extraData.get("url"));
//        mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
//                mJitsiMeetViewReference.getJitsiMeetView().getId(),
//                "participantJoined",
//                event);
//    }
//
//    @Override
//    protected void onParticipantLeft(HashMap<String, Object> extraData) {
//        super.onParticipantLeft(extraData);
//        WritableMap event = Arguments.createMap();
//        event.putString("url", (String) extraData.get("url"));
//        mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
//                mJitsiMeetViewReference.getJitsiMeetView().getId(),
//                "participantLeft",
//                event);
//    }
    public void onConferenceJoined(Map<String, Object> data) {
        WritableMap event = Arguments.createMap();
        event.putString("url", (String) data.get("url"));
        mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                mJitsiMeetViewReference.getJitsiMeetView().getId(),
                "conferenceJoined",
                event);
    }


    public void onConferenceTerminated(Map<String, Object> data) {
        WritableMap event = Arguments.createMap();
        event.putString("url", (String) data.get("url"));
        event.putString("error", (String) data.get("error"));
        mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                mJitsiMeetViewReference.getJitsiMeetView().getId(),
                "conferenceTerminated",
                event);
    }

    public void onConferenceWillJoin(Map<String, Object> data) {
        WritableMap event = Arguments.createMap();
        event.putString("url", (String) data.get("url"));
        mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                mJitsiMeetViewReference.getJitsiMeetView().getId(),
                "conferenceWillJoin",
                event);
    }

    public void onParticipantJoined(Map<String, Object> data) {
        WritableMap event = Arguments.createMap();
        event.putString("url", (String) data.get("url"));
        mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                mJitsiMeetViewReference.getJitsiMeetView().getId(),
                "participantJoined",
                event);
    }

    public void onParticipantLeft(Map<String, Object> data) {
        WritableMap event = Arguments.createMap();
        event.putString("url", (String) data.get("url"));
        mReactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                mJitsiMeetViewReference.getJitsiMeetView().getId(),
                "participantLeft",
                event);
    }

    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put("conferenceJoined", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onConferenceJoined")))
                .put("conferenceTerminated", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onConferenceTerminated")))
                .put("conferenceWillJoin", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onConferenceWillJoin")))
                .put("participantJoined", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onParticipantJoined")))
                .put("participantLeft", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onParticipantLeft")))
                .build();
    }
}