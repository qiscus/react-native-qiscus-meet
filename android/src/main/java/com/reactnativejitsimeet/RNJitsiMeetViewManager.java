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

import timber.log.Timber;

import static java.security.AccessController.getContext;

@ReactModule(name = RNJitsiMeetViewManager.REACT_CLASS)
public class RNJitsiMeetViewManager extends SimpleViewManager<RNJitsiMeetView>  {
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
            RNJitsiMeetView view = new RNJitsiMeetView(context.getCurrentActivity(),mReactContext,mJitsiMeetViewReference);
            mJitsiMeetViewReference.setJitsiMeetView(view);
        }
        return mJitsiMeetViewReference.getJitsiMeetView();
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