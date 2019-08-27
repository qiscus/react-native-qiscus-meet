# react-native-qiscus-meet
React native wrapper for Jitsi Meet SDK

## Install

`npm install react-native-qiscus-meet --save` 

## Use

In your component, 

1.) import JitsiMeet and JitsiMeetEvents: `import QiscusMeet, { QiscusMeetEvents } from 'react-native-qiscus-meet';`

2.) add the following code: 

```
  const initiateVideoCall = () => {
    QiscusMeet.initialize();
    QiscusMeetEvents.addListener('CONFERENCE_LEFT', (data) => {
      console.log('CONFERENCE_LEFT');
    });
    setTimeout(() => {
      QiscusMeet.call(`<your url>`);
    }, 1000);
  };
```
### Events

You can add listeners for the following events:
- CONFERENCE_JOINED
- CONFERENCE_LEFT
- CONFERENCE_WILL_JOIN

## iOS Manual Install
### Step 1. Add Files Into Project
- 1-1.) in Xcode: Right click `Libraries` ➜ `Add Files to [project]`  
- 1-2.) choose `node_modules/react-native-qiscus-meet/ios/RNJitsiMeet.xcodeproj` then `Add`  
- 1-3.) add `node_modules/react-native-qiscus-meet/ios/WebRTC.framework` and `node_modules/react-native-qiscus-meet/ios/JitsiMeet.framework` to the Frameworks folder
- 1-4.) add `node_modules/react-native-qiscus-meet/ios/JitsiMeet.storyboard` in the same folder as AppDelegate.m
- 1-5.) Replace the following code in AppDelegate.m:

```
  UIViewController *rootViewController = [UIViewController new];
  rootViewController.view = rootView;
  self.window.rootViewController = rootViewController;
```
with this one
```
  UIViewController *rootViewController = [UIViewController new];
  UINavigationController *navigationController = [[UINavigationController alloc]initWithRootViewController:rootViewController];
  navigationController.navigationBarHidden = YES;
  rootViewController.view = rootView;
  self.window.rootViewController = navigationController; 
```

This will create a navigation controller to be able to navigate between the Jitsi component and your react native screens.

### Step 2. Add Library Search Path

2-1.) select `Build Settings`, find `Search Paths`  
2-2.) edit BOTH `Framework Search Paths` and `Library Search Paths`  
2-3.) add path on BOTH sections with: `$(SRCROOT)/../node_modules/react-native-qiscus-meet/ios` with `recursive`  

## Step 3. Change General Setting and Embed Framework

3-1.) go to `General` tab  
3-2.) change `Deployment Target` to `8.0`  
3-3.) add `WebRTC.framework` and `JitsiMeet.framework` in `Embedded Binaries` 

## Step 4. Link/Include Necessary Libraries

- 4-1.) click `Build Phases` tab, open `Link Binary With Libraries`  
- 4-2.) add `libRNJitsiMeet.a`  
- 4-3.) make sure `WebRTC.framework` and `JitsiMeet.framework` linked  
- 4-4.) add the following libraries depending on your version of XCode, some libraries might exist or not:  

```
AVFoundation.framework
AudioToolbox.framework
CoreGraphics.framework
GLKit.framework
CoreAudio.framework
CoreVideo.framework
VideoToolbox.framework
libc.tbd
libsqlite3.tbd
libstdc++.tbd
libc++.tbd
```

- 4-5.) Under `Build setting` set `Dead Code Stripping` to `No`, set `Enable Bitcode` to `No` and `Always Embed Swift Standard Libraries` to `Yes`
- 4-6.) Add the following script in a new "Run Script" phase in "Build Phases":

```
echo "Target architectures: $ARCHS"

APP_PATH="${TARGET_BUILD_DIR}/${WRAPPER_NAME}"

find "$APP_PATH" -name '*.framework' -type d | while read -r FRAMEWORK
do
FRAMEWORK_EXECUTABLE_NAME=$(defaults read "$FRAMEWORK/Info.plist" CFBundleExecutable)
FRAMEWORK_EXECUTABLE_PATH="$FRAMEWORK/$FRAMEWORK_EXECUTABLE_NAME"
echo "Executable is $FRAMEWORK_EXECUTABLE_PATH"
echo $(lipo -info "$FRAMEWORK_EXECUTABLE_PATH")

FRAMEWORK_TMP_PATH="$FRAMEWORK_EXECUTABLE_PATH-tmp"

# remove simulator's archs if location is not simulator's directory
case "${TARGET_BUILD_DIR}" in
*"iphonesimulator")
echo "No need to remove archs"
;;
*)
if $(lipo "$FRAMEWORK_EXECUTABLE_PATH" -verify_arch "i386") ; then
lipo -output "$FRAMEWORK_TMP_PATH" -remove "i386" "$FRAMEWORK_EXECUTABLE_PATH"
echo "i386 architecture removed"
rm "$FRAMEWORK_EXECUTABLE_PATH"
mv "$FRAMEWORK_TMP_PATH" "$FRAMEWORK_EXECUTABLE_PATH"
fi
if $(lipo "$FRAMEWORK_EXECUTABLE_PATH" -verify_arch "x86_64") ; then
lipo -output "$FRAMEWORK_TMP_PATH" -remove "x86_64" "$FRAMEWORK_EXECUTABLE_PATH"
echo "x86_64 architecture removed"
rm "$FRAMEWORK_EXECUTABLE_PATH"
mv "$FRAMEWORK_TMP_PATH" "$FRAMEWORK_EXECUTABLE_PATH"
fi
;;
esac

echo "Completed for executable $FRAMEWORK_EXECUTABLE_PATH"
echo $

done
```
This will run a script everytime you build to clean the unwanted architecture

## Step 5. Add Permissions

5-1.) navigate to `<ProjectFolder>/ios/<ProjectName>/`  
5-2.) edit `Info.plist` and add the following lines

```
<key>NSCameraUsageDescription</key>
<string>Camera Permission</string>
<key>NSMicrophoneUsageDescription</key>
<string>Microphone Permission</string>
```
5-3.) in `Info.plist`, make sure that 
```
<key>UIBackgroundModes</key>
<array>
</array>
```
contains `<string>voip</string>`

## Android Manual Install

1.) In `android/app/src/main/AndroidManifest.xml` add these permissions

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:tools="http://schemas.android.com/tools" // <--- Add this line if not already existing

...
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus"/>

<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<permission android:name="${applicationId}.permission.JITSI_BROADCAST"
    android:label="Jitsi Meet Event Broadcast"
    android:protectionLevel="normal"></permission>
<uses-permission android:name="${applicationId}.permission.JITSI_BROADCAST"/>
```

2.) In the `<application>` section of `android/app/src/main/AndroidManifest.xml`, add
 ```xml
 <activity android:name="com.reactnativejitsimeet.JitsiMeetNavigatorActivity" />
 ```
 
3.) In `android/settings.gradle`, include react-native-jitsi-meet module
```gradle
include ':react-native-jitsi-meet'
project(':react-native-jitsi-meet').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-jitsi-meet/android')
```

4.) In `android/app/build.gradle`, add react-native-jitsi-meet to dependencies
```gradle
android {
  ...
  packagingOptions {
      pickFirst 'lib/x86/libc++_shared.so'
      pickFirst 'lib/x86/libjsc.so'
      pickFirst 'lib/x86_64/libjsc.so'
      pickFirst 'lib/arm64-v8a/libjsc.so'
      pickFirst 'lib/arm64-v8a/libc++_shared.so'
      pickFirst 'lib/x86_64/libc++_shared.so'
      pickFirst 'lib/armeabi-v7a/libc++_shared.so'
      pickFirst 'lib/armeabi-v7a/libjsc.so'
  }
}
dependencies {
  ...
    implementation(project(':react-native-jitsi-meet'))
}
```
and add/replace the following lines:

```
project.ext.react = [
    entryFile: "index.js",
    bundleAssetName: "app.bundle",
]
```

5.) In `android/build.gradle`, add the following code 
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
and set your minSdkVersion to be at least 21.

6.) In `android/app/src/main/java/com/xxx/MainApplication.java`

```java
import com.reactnativejitsimeet.JitsiMeetPackage;  // <--- Add this line
import android.support.annotation.Nullable; // <--- Add this line if not already existing
...
    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
        new MainReactPackage(),
        new JitsiMeetPackage()                  // <--- Add this line
      );
    }
```

7.) In `android/app/src/main/java/com/xxx/MainApplication.java` add/replace the following methods:

```
    @Override
    protected String getJSMainModuleName() {
      return "index";
    }

    @Override
    protected @Nullable String getBundleAssetName() {
      return "app.bundle";
    }
```
