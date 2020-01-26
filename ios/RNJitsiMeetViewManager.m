#import "RNJitsiMeetViewManager.h"
#import "RNJitsiMeetView.h"

@implementation RNJitsiMeetViewManager{
    RNJitsiMeetView *jitsiMeetView;
}

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

RCT_EXPORT_METHOD(setup:(NSString *)urlString)
{
    RCTLogInfo(@"Setup");
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:urlString forKey:@"baseUrlMeet"];
    [defaults synchronize];

}

RCT_EXPORT_METHOD(call:(BOOL)isVideo room:(NSString *)room avatarUrl:(NSString *)avatarUrl displayName:(NSString *)displayName callback:(RCTResponseSenderBlock)callback)
{
    NSString *baseUrlMeet = [[NSUserDefaults standardUserDefaults]
                             stringForKey:@"baseUrlMeet"];

    NSString* baseUrl = [NSString stringWithFormat:@"%@/%@", baseUrlMeet, room];
    [self loadUrl:baseUrl isVideo:isVideo avatarUrl:avatarUrl displayName:displayName];
}

RCT_EXPORT_METHOD(answer:(BOOL)isVideo room:(NSString *)room avatarUrl:(NSString *)avatarUrl displayName:(NSString *)displayName callback:(RCTResponseSenderBlock)callback)
{
    RCTLogInfo(@"Load URL %@", room);
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    NSString *baseUrlMeet = [[NSUserDefaults standardUserDefaults]
                             stringForKey:@"baseUrlMeet"];

    NSString* baseUrl = [NSString stringWithFormat:@"%@/%@", baseUrlMeet, room];
    NSString* baseUrlGetParticipant = [NSString stringWithFormat:@"%@/get-room-size?room=%@", baseUrlMeet, room];
    
    [request setURL:[NSURL URLWithString:baseUrlGetParticipant]];
    [request setHTTPMethod:@"GET"];
    
    NSURLSession *session = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
    [[session dataTaskWithRequest:request completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        
        if (data) {
            NSMutableArray *json = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&error];
            NSString *countParticipants = [json valueForKey:@"participants"];
            NSInteger intPartcipant = [countParticipants integerValue];
            
            if(intPartcipant > 0){
                [self loadUrl:baseUrl isVideo:isVideo avatarUrl:avatarUrl displayName:displayName];
            } else {
                callback(@[@"You haven't participants"]);
            }
        } else {
            callback(@[@"Server Errors"]);
        }
        
    }] resume];
}

RCT_EXPORT_METHOD(audioCall:(NSString *)urlString)
{
    RCTLogInfo(@"Load Audio only URL %@", urlString);
    dispatch_sync(dispatch_get_main_queue(), ^{
        JitsiMeetConferenceOptions *options = [JitsiMeetConferenceOptions fromBuilder:^(JitsiMeetConferenceOptionsBuilder *builder) {        
            builder.room = urlString;
            builder.audioOnly = YES;
        }];
        [jitsiMeetView join:options];
    });
}

RCT_EXPORT_METHOD(endCall)
{
    dispatch_sync(dispatch_get_main_queue(), ^{
        [jitsiMeetView leave];
    });
}

#pragma mark JitsiMeetViewDelegate

- (void)loadUrl:(NSString *)room isVideo:(BOOL)isVideo avatarUrl:(NSString *)avatarUrl displayName:(NSString *)displayName {
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *baseUrlMeet = [defaults stringForKey:@"baseUrlMeet"];
    
    NSString *endPoint = @":9090/generate_url";
    NSString* generateTokenUrl = [NSString stringWithFormat:@"%@%@", baseUrlMeet, endPoint];
        
    NSDictionary *dictionary = @{ @"baseUrl" : room, @"avatar":avatarUrl, @"name":displayName };
    NSData *JSONData = [NSJSONSerialization dataWithJSONObject:dictionary
                                                       options:0
                                                         error:nil];
    
        
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    [request setURL:[NSURL URLWithString:generateTokenUrl]];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:JSONData];
    
    NSURLSession *session = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
    [[session dataTaskWithRequest:request completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {

        if(data){
            NSMutableArray *json = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&error];
            NSString *url = [json valueForKey:@"url"];
            dispatch_sync(dispatch_get_main_queue(), ^{
                JitsiMeetConferenceOptions *options = [JitsiMeetConferenceOptions fromBuilder:^(JitsiMeetConferenceOptionsBuilder *builder) {
                    builder.room = url;
                    builder.audioOnly = !isVideo;
                }];
                [self->jitsiMeetView join:options];
            });
        } else {
            [self endCall];
        }
        
    }] resume];
}

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
