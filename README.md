# react-native-qiscus-meet
React native wrapper for Qiscus Meet SDK

## Install

`npm install react-native-qiscus-meet --save` 

If you are using React-Native < 0.60, you should use a version < 2.0.0.  
For versions higher than 2.0.0, you need to add the following piece of code in your ```metro.config.js``` file to avoid conflicts between react-native-qiscus-meet and react-native in metro bundler.

```
const blacklist = require('metro-config/src/defaults/blacklist');

module.exports = {
  resolver: {
    blacklistRE: blacklist([
      /ios\/Pods\/JitsiMeetSDK\/Frameworks\/JitsiMeet.framework\/assets\/node_modules\/react-native\/.*/,
    ]),
  },
};
```

Although most of the process is automated, you still have to follow the platform install guide below ([iOS](#ios-install-for-rn--060) and [Android](#android-install)) to get this library to work.


## Use (>= 2.0.0)

The following component is an example of use:

```
import React from 'react';
import { View } from 'react-native';
import QiscusMeet, { QiscusMeetView } from 'react-native-qiscus-meet';

class VideoCall extends React.Component {
  constructor(props) {
    super(props);
    this.onConferenceTerminated = this.onConferenceTerminated.bind(this);
    this.onConferenceJoined = this.onConferenceJoined.bind(this);
    this.onConferenceWillJoin = this.onConferenceWillJoin.bind(this);
    this.onParticipantJoined = this.onParticipantJoined.bind(this);
    this.onParticipantLeft = this.onParticipantLeft.bind(this);
  }

  componentDidMount() {
    setTimeout(() => {
      const url = 'https://your.qiscus.server/roomName'; // can also be only room name and will connect to jitsi meet servers
      const userInfo = { displayName: 'User', email: 'user@example.com', avatar: 'https:/gravatar.com/avatar/abc123' };
      QiscusMeet.call(url, userInfo);
      /* You can also use QiscusMeet.audioCall(url) for audio only call */
      /* You can programmatically end the call with QiscusMeet.endCall() */
    }, 1000);
  }

  onConferenceTerminated(nativeEvent) {
    /* Conference terminated event */
  }

  onConferenceJoined(nativeEvent) {
    /* Conference joined event */
  }

  onConferenceWillJoin(nativeEvent) {
    /* Conference will join event */
  }
  
  onParticipantJoined(nativeEvent) {
  /* Participant joined conference */
  }
  
  onParticipantLeft(nativeEvent){
  /* Participant left conference */
  }

  render() {
    return (
      <View style={{ backgroundColor: 'black',flex: 1 }}>
        <QiscusMeetView onConferenceTerminated={this.onConferenceTerminated} onConferenceJoined={this.onConferenceJoined} onConferenceWillJoin={this.onConferenceWillJoin} style={{ flex: 1, height: '100%', width: '100%' }} />
      </View>
    );
  }
}

export default VideoCall;
```

You can also check the [ExampleApp](https://github.com/rajapulau/react-native-qiscus-meet/tree/master/ExampleApp)

### Events

You can add listeners for the following events:
- onConferenceJoined
- onConferenceTerminated
- onConferenceWillJoin
- onParticipantJoined
- onParticipantLeft

## iOS Configuration

1.) navigate to `<ProjectFolder>/ios/<ProjectName>/`  
2.) edit `Info.plist` and add the following lines

```
<key>NSCameraUsageDescription</key>
<string>Camera Permission</string>
<key>NSMicrophoneUsageDescription</key>
<string>Microphone Permission</string>
```
3.) in `Info.plist`, make sure that 
```
<key>UIBackgroundModes</key>
<array>
</array>
```
contains `<string>voip</string>`

## iOS Install
1.) Modify your Podfile to have ```platform :ios, '10.0'``` and execute ```pod install```  
2.) In Xcode, under Build setting set Enable Bitcode to No  


## Android Install
1.) In `android/app/build.gradle`, add/replace the following lines:

```
project.ext.react = [
    entryFile: "index.js",
    bundleAssetName: "app.bundle",
]
```

2.) In `android/app/src/main/java/com/xxx/MainApplication.java` add/replace the following methods:

```
  import androidx.annotation.Nullable; // <--- Add this line if not already existing
  ...
    @Override
    protected String getJSMainModuleName() {
      return "index";
    }

    @Override
    protected @Nullable String getBundleAssetName() {
      return "app.bundle";
    }
```

3.) In `android/build.gradle`, add the following code 
```
allprojects {
    repositories {
        mavenLocal()
        jcenter()
        maven {
            // All of React Native (JS, Obj-C sources, Android binaries) is installed from npm
            url "$rootDir/../node_modules/react-native/android"
        }
        maven {
            url "https://maven.google.com"
        }
        maven { // <---- Add this block
            url "https://artifactory.qiscus.com/artifactory/qiscus-meet"
        }
        maven { url "https://jitpack.io" }
    }
}
```



### Side-note

If your app already includes `react-native-locale-detector` or `react-native-vector-icons`, you must exclude them from the `react-native-qiscus-meet` project implementation with the following code (even if you're app uses autolinking with RN > 0.60):

```
    implementation(project(':react-native-qiscus-meet')) {
      exclude group: 'com.facebook.react',module:'react-native-locale-detector'
      exclude group: 'com.facebook.react',module:'react-native-vector-icons'
    }
```
