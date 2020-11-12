#import "RNJitsiMeetViewManager.h"
#import "RNJitsiMeetView.h"
#import <JitsiMeet/JitsiMeetUserInfo.h>
#import <Foundation/Foundation.h>

@implementation RNJitsiMeetViewManager{
    RNJitsiMeetView *jitsiMeetView;
}

NSString *appId = @"";
NSString *urlString = @"";

RCT_EXPORT_MODULE(RNJitsiMeetView)
RCT_EXPORT_VIEW_PROPERTY(onConferenceJoined, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onConferenceTerminated, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onConferenceWillJoin, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onEnteredPip, RCTBubblingEventBlock)

- (UIView *)view
{
  jitsiMeetView = [[RNJitsiMeetView alloc] init];
  jitsiMeetView.delegate = self;
  return jitsiMeetView;
}

RCT_EXPORT_METHOD(initialize)
{
    RCTLogInfo(@"Initialize is deprecated in v2");
}

RCT_EXPORT_METHOD(call:(NSDictionary *)userInfo room:(NSString *)room videoMuted:(BOOL *)videoMuted audioMuted:(BOOL *)audioMuted token:(NSString *)token)
{
    RCTLogInfo(@"Load URL %@", urlString);
    NSString *baseUrl = [NSString stringWithFormat:@"%@/%@/%@", urlString, appId, room];

    JitsiMeetUserInfo * _userInfo = [[JitsiMeetUserInfo alloc] init];
    if (userInfo != NULL) {
      if (userInfo[@"displayName"] != NULL) {
        _userInfo.displayName = userInfo[@"displayName"];
      }
      if (userInfo[@"email"] != NULL) {
        _userInfo.email = userInfo[@"email"];
      }
      if (userInfo[@"avatar"] != NULL) {
        NSURL *url = [NSURL URLWithString:[userInfo[@"avatar"] stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]]];
        _userInfo.avatar = url;
      }
    }
    dispatch_sync(dispatch_get_main_queue(), ^{
        JitsiMeetConferenceOptions *options = [JitsiMeetConferenceOptions fromBuilder:^(JitsiMeetConferenceOptionsBuilder *builder) {        
            builder.room = baseUrl;
            builder.token = token;
            builder.audioMuted = audioMuted;
            builder.videoMuted = videoMuted;
            builder.userInfo = _userInfo;
        }];
        [jitsiMeetView join:options];
    });
}

RCT_EXPORT_METHOD(audioCall:(NSDictionary *)userInfo room:(NSString *)room audioMuted:(BOOL *)audioMuted token:(NSString *)token)
{
    RCTLogInfo(@"Load Audio only URL %@", urlString);
     NSString *baseUrl = [NSString stringWithFormat:@"%@/%@/%@", urlString, appId, room];

    JitsiMeetUserInfo * _userInfo = [[JitsiMeetUserInfo alloc] init];
    if (userInfo != NULL) {
      if (userInfo[@"displayName"] != NULL) {
        _userInfo.displayName = userInfo[@"displayName"];
      }
      if (userInfo[@"email"] != NULL) {
        _userInfo.email = userInfo[@"email"];
      }
      if (userInfo[@"avatar"] != NULL) {
        NSURL *url = [NSURL URLWithString:[userInfo[@"avatar"] stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]]];
        _userInfo.avatar = url;
      }
    }
    dispatch_sync(dispatch_get_main_queue(), ^{
        JitsiMeetConferenceOptions *options = [JitsiMeetConferenceOptions fromBuilder:^(JitsiMeetConferenceOptionsBuilder *builder) {        
            builder.room = urlString;
            builder.userInfo = _userInfo;
            builder.token = token;
            builder.audioMuted = audioMuted;
            builder.audioOnly = YES;
        }];
        [jitsiMeetView join:options];
    });
}

RCT_EXPORT_METHOD(setup:(NSString *)baseUrl idApp:(NSString *)idApp)
{
  appId = idApp;
  urlString = baseUrl;
}

RCT_EXPORT_METHOD(endCall)
{
    dispatch_sync(dispatch_get_main_queue(), ^{
        [jitsiMeetView leave];
    });
}

RCT_REMAP_METHOD(getSetup,
                 getSetupWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
  NSDictionary *setup = @{ @"url" : urlString, @"appId" : appId};
  if (setup) {
    resolve(setup);
  } else {
    NSError *error = @"Not found";
    reject(@"no_setup", @"There were no setup", error);
  }
}

#pragma mark JitsiMeetViewDelegate

- (void)conferenceJoined:(NSDictionary *)data {
    RCTLogInfo(@"Conference joined");
    if (!jitsiMeetView.onConferenceJoined) {
        return;
    }

    jitsiMeetView.onConferenceJoined(data);
}

- (void)conferenceTerminated:(NSDictionary *)data {
    RCTLogInfo(@"Conference terminated");
    if (!jitsiMeetView.onConferenceTerminated) {
        return;
    }

    jitsiMeetView.onConferenceTerminated(data);
}

- (void)conferenceWillJoin:(NSDictionary *)data {
    RCTLogInfo(@"Conference will join");
    if (!jitsiMeetView.onConferenceWillJoin) {
        return;
    }

    jitsiMeetView.onConferenceWillJoin(data);
}

- (void)enterPictureInPicture:(NSDictionary *)data {
    RCTLogInfo(@"Enter Picture in Picture");
    if (!jitsiMeetView.onEnteredPip) {
        return;
    }

    jitsiMeetView.onEnteredPip(data);
}

@end