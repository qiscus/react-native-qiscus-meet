/**
 * @providesModule QiscusMeet
 */

import { NativeModules, requireNativeComponent } from 'react-native';

export const QiscusMeetView = requireNativeComponent('RNJitsiMeetView');
export const QiscusMeetModule = NativeModules.RNJitsiMeetModule
const call = QMeetModule.call;
const audioCall = QiscusMeetModule.audioCall;
QiscusMeetModule.call = (url, userInfo) => {
  userInfo = userInfo || {};
  call(url, userInfo);
}
QiscusMeetModule.audioCall = (url, userInfo) => {
  userInfo = userInfo || {};
  audioCall(url, userInfo);
}
export default QiscusMeetModule;


