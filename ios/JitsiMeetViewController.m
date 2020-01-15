//
//  ViewController.m
//  JitsiMobile
//
//  Created by Sébastien Krafft on 07/08/2018.
//  Copyright © 2018 Sébastien Krafft. All rights reserved.
//

#import "JitsiMeetViewController.h"

@implementation JitsiMeetViewController

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void) setDelegate:(id<JitsiMeetViewDelegate>) delegate {
    JitsiMeetView *jitsiMeetView = (JitsiMeetView *) self.view;
    if (delegate) {
        jitsiMeetView.delegate = delegate;
    }
}

- (void)loadUrl:(NSString *)room isVideo:(BOOL)isVideo avatarUrl:(NSString *)avatarUrl displayName:(NSString *)displayName {
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *baseUrlMeet = [defaults stringForKey:@"baseUrlMeet"];
//    BOOL isConference = [[NSUserDefaults standardUserDefaults]
//                            boolForKey:@"isConferenceMeet"];
    
    NSString *endPoint = @":9090/generate_url";
    NSString* generateTokenUrl = [NSString stringWithFormat:@"%@%@", baseUrlMeet, endPoint];
    
    
    // Create a simple dictionary with numbers.
    NSDictionary *dictionary = @{ @"baseUrl" : room, @"avatar":avatarUrl, @"name":displayName };
    
    // Convert the dictionary into JSON data.
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
                JitsiMeetView *jitsiMeetView = (JitsiMeetView *) self.view;
                JitsiMeetConferenceOptions *options = [JitsiMeetConferenceOptions fromBuilder:^(JitsiMeetConferenceOptionsBuilder *builder) {
                    builder.room = url;
                    builder.audioOnly = !isVideo;
                }];
                [jitsiMeetView join:options];
            });
        } else {
            [self endCall];
        }
        
    }] resume];
}

- (void)endCall {
    JitsiMeetView *jitsiMeetView = (JitsiMeetView *) self.view;
    [jitsiMeetView leave];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
