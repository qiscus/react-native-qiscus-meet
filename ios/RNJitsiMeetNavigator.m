#import "RNJitsiMeetNavigator.h"

@implementation RNJitsiMeetNavigatorManager {
    JitsiMeetViewController *jitsiMeetViewController;
}

RCT_EXPORT_MODULE()

- (void)conferenceJoined:(NSDictionary *)data {
    RCTLogInfo(@"Conference joined");
    [self sendEventWithName:@"CONFERENCE_JOINED" body:data];
}

- (void)conferenceTerminated:(NSDictionary *)data {
    RCTLogInfo(@"Conference left");
    UIViewController* rootViewController = [[[[UIApplication sharedApplication]delegate] window] rootViewController];
    UINavigationController *navigationController = (UINavigationController *) rootViewController;
    [navigationController popViewControllerAnimated:YES];
    [self sendEventWithName:@"CONFERENCE_LEFT" body:data];
}

- (void)conferenceWillJoin:(NSDictionary *)data {
    RCTLogInfo(@"Conference will join");
    [self sendEventWithName:@"CONFERENCE_WILL_JOIN" body:data];
}

- (void)enterPictureInPicture:(NSDictionary *)data {
    RCTLogInfo(@"Enter Picture in Picture");
    [self sendEventWithName:@"CONFERENCE_ENTER_PIP" body:data];
}

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"CONFERENCE_JOINED", @"CONFERENCE_LEFT", @"CONFERENCE_WILL_JOIN", @"CONFERENCE_ENTER_PIP"];
}

RCT_EXPORT_METHOD(setup:(NSString *)urlString isconference:(BOOL)isConference)
{
    RCTLogInfo(@"Setup");
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:urlString forKey:@"baseUrlMeet"];
    [defaults setBool:isConference forKey:@"isConferenceMeet"];
    [defaults synchronize];
    
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"JitsiMeet" bundle:nil];
    jitsiMeetViewController = [storyboard instantiateViewControllerWithIdentifier:@"jitsiMeetStoryBoardID"];
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
        NSMutableArray *json = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&error];
        NSString *countParticipants = [json valueForKey:@"participants"];
        if(![countParticipants  isEqual: @"0"]){
            dispatch_sync(dispatch_get_main_queue(), ^{
                UIViewController* rootViewController = [[[[UIApplication sharedApplication]delegate] window] rootViewController];
                UINavigationController *navigationController = (UINavigationController *) rootViewController;
                [navigationController pushViewController:self->jitsiMeetViewController animated:true];
                [self->jitsiMeetViewController setDelegate:self];
                [self->jitsiMeetViewController loadUrl:baseUrl isVideo:isVideo avatarUrl:avatarUrl displayName:displayName];
            });
        } else {
            callback(@[@"You haven't participants"]);
        }
    }] resume];
}

RCT_EXPORT_METHOD(call:(BOOL)isVideo room:(NSString *)room avatarUrl:(NSString *)avatarUrl displayName:(NSString *)displayName)
{
    dispatch_sync(dispatch_get_main_queue(), ^{
        NSString *baseUrlMeet = [[NSUserDefaults standardUserDefaults]
                                 stringForKey:@"baseUrlMeet"];
        
        NSString* baseUrl = [NSString stringWithFormat:@"%@,%@", baseUrlMeet, room];
        UIViewController* rootViewController = [[[[UIApplication sharedApplication]delegate] window] rootViewController];
        UINavigationController *navigationController = (UINavigationController *) rootViewController;
        [navigationController pushViewController:self->jitsiMeetViewController animated:true];
        [self->jitsiMeetViewController setDelegate:self];
        [self->jitsiMeetViewController loadUrl:baseUrl isVideo:isVideo avatarUrl:avatarUrl displayName:displayName];
    });
}

RCT_EXPORT_METHOD(endCall)
{
    RCTLogInfo(@"EndCall");
    dispatch_sync(dispatch_get_main_queue(), ^{
        [self->jitsiMeetViewController endCall];
    });
}
@end
