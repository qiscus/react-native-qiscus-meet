/**
 * @providesModule JitsiMeet
 */

import { DeviceEventEmitter, NativeEventEmitter, NativeModules, Platform } from 'react-native';

const { RNJitsiMeetNavigatorManager } = NativeModules;
export const QiscusMeetEvents = Platform.select({
  ios: new NativeEventEmitter(RNJitsiMeetNavigatorManager),
  android: DeviceEventEmitter,
});
export default RNJitsiMeetNavigatorManager;
