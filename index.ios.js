/**
 * @providesModule QiscusMeet
 */

import { NativeModules, requireNativeComponent } from 'react-native';

export const QiscusMeetView = requireNativeComponent('RNJitsiMeetView');
export const QiscusMeetModule = NativeModules.RNJitsiMeetView;

export default QiscusMeetModule;


