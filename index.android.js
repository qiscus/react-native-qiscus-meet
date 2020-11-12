/**
 * @providesModule JitsiMeet
 */

import { NativeModules, requireNativeComponent } from "react-native";

export const QiscusMeetView = requireNativeComponent("RNJitsiMeetView");
export const QiscusMeetModule = NativeModules.RNJitsiMeetModule;
const call = QiscusMeetModule.call;
const audioCall = QiscusMeetModule.audioCall;
const setup = QiscusMeetModule.setup;
const getSetup = QiscusMeetModule.getSetup;

// Get Token 
const getUserData = async ({
  displayName = "",
  email = "",
  room = "",
  avatar = "",
}) => {
  const { appId, url } = await getSetup();

  const response = await fetch(`${url}:9090/generate_url`, {
    method: "POST",
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      avatar: avatar,
      name: displayName,
      email: email,
      iss: "meetcall",
      sub: "call.qiscus.com",
      room: room,
      moderator: false,
      appId: appId,
    }),
  });

  const data = await response.json();
  return data;
};

QiscusMeetModule.call = async (info) => {
  const userInfo = info || {};
  const { videoMuted, audioMuted, room } = userInfo;

  if (!room) {
    console.log("Please provide room name");
    return;
  }
  try {
    const data = await getUserData(userInfo);
    if (!data.token) return;

    if (!userInfo.audioOnly) {
      call(userInfo, room, videoMuted, audioMuted, data.token);
    } else {
      audioCall(userInfo, room, audioMuted, data.token);
    }
  } catch (error) {
    console.log("Error get token", error);
  }
};

QiscusMeetModule.audioCall = (userInfo) => {
  userInfo = userInfo || {};
  audioCall(userInfo, audioMuted);
};

QiscusMeetModule.setup = (appId, url) => {
  setup(appId, url);
};
export default QiscusMeetModule;
